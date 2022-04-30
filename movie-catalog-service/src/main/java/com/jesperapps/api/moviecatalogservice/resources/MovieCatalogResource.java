package com.jesperapps.api.moviecatalogservice.resources;

import com.jesperapps.api.moviecatalogservice.models.*;
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
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){
        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/"+userId,UserRating.class);
        System.out.println("ratings"+ratings.getUserRating());
        return ratings.getUserRating().stream().map(rating -> {
            System.out.println("rating"+rating.getMovieId());
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
            System.out.println("movie"+movie+movie.getMovieId()+movie.getName());
            return new CatalogItem(movie.getName(), movie.getOverview(), rating.getRating());
    }).collect(Collectors.toList());

    }
}





//               Movie movie = webClientBuilder.build()
//                        .get()
//                        .uri("http://localhost:8082/movies/"+rating.getMovieId())
//                        .retrieve()
//                        .bodyToMono(Movie.class)
//                        .block();