package services.SearchStrategy;

import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public class TitleSearchStrategy implements SearchStrategy {
    private final String title;

    public TitleSearchStrategy(String title){
        this.title = title;
    }

    @Override
    public List<Movie> search(List<Movie> movies) {
        List<Movie> filteredMovies = movies.stream().filter(movie ->
                movie.getTitle().toLowerCase().contains(title)).toList();
        return filteredMovies;
    }
}
