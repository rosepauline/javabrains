package com.jesperapps.api.movieinfoservice.resources;

import com.jesperapps.api.movieinfoservice.models.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;

@RestController
@RequestMapping("/movies")
public class MovieResource {

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/{movieId}")
    public Movie getMovieInfo(@PathVariable("movieId") String movieId){
        System.out.println("movieIdinfo"+movieId);
        MovieSummary movieSummary = restTemplate.getForObject(
                "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + apiKey,
                MovieSummary.class);
        System.out.println("movieSummary"+movieSummary.getTitle()+"------"+movieSummary.getOverview());
        return new Movie(movieId,movieSummary.getTitle(), movieSummary.getOverview());
    }
}
