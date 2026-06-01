package services.SearchStrategy;

import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public class GenreSearchStrategy implements SearchStrategy {
    private final String genre;

    public GenreSearchStrategy(String genre){
        this.genre = genre;
    }

    @Override
    public List<Movie> search(List<Movie> movies) {
        List<Movie> filteredMovies = movies.stream().filter(movie ->
                movie.getGenre().toLowerCase().contains(genre)).toList();
        return filteredMovies;
    }
}
