package com.jesperapps.api.moviecatalogservice.resources;

import com.jesperapps.api.moviecatalogservice.models.*;
import com.netflix.hystrix.contrib.javanica.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.cloud.client.discovery.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import org.springframework.web.reactive.function.client.*;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private WebClient.Builder webClientBuilder;

    //get all rated movie IDs
    //for each movie ID, call movie info service and get details
    //put them all together
    @RequestMapping("/{userId}")
    @HystrixCommand(fallbackMethod = "getFallbackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
        UserRating ratings = getUserRating(userId);
        System.out.println("ratings"+ratings.getUserRating());
        return ratings.getUserRating().stream()
                .map(rating -> getCatalogItem(rating))
        .collect(Collectors.toList());
    }

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem")
    private CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+ rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), movie.getOverview(), rating.getRating());
    }

    private CatalogItem getFallbackCatalogItem(Rating rating) {

    }

    @HystrixCommand(fallbackMethod = "getFallbackUserRating")
    private UserRating getUserRating(@PathVariable("userId") String userId) {
        return restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);
    }

    private UserRating getFallbackUserRating(@PathVariable("userId") String userId) {
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
    }

    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId){
        return Arrays.asList(new CatalogItem("No movie","",0));
    }
}





//               Movie movie = webClientBuilder.build()
//                        .get()
//                        .uri("http://localhost:8082/movies/"+rating.getMovieId())
//                        .retrieve()
//                        .bodyToMono(Movie.class)
//                        .block();