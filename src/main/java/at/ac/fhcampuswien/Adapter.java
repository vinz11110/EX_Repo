package at.ac.fhcampuswien;

import at.ac.fhcampuswien.models.Movie;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;

public class Adapter {
    Gson gson = new Gson();


    public Movie getMovieObjectFromJson(String requestbody){
        Movie movie = gson.fromJson(requestbody, Movie.class);
        return movie;
    }

    public String getJsonFromMovieList(List<Movie> list){
        return gson.toJson(list);
    }
}
