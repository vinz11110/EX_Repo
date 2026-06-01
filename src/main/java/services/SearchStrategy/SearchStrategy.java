package services.SearchStrategy;

import at.ac.fhcampuswien.controllers.MovieController;
import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public interface SearchStrategy {
    List<Movie> search(List<Movie> movies);
}
