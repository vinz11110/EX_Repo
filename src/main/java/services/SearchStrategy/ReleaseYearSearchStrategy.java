package services.SearchStrategy;

import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public class ReleaseYearSearchStrategy implements SearchStrategy {
    private final String releaseYear;

    public ReleaseYearSearchStrategy(String releaseYear){
        this.releaseYear = releaseYear;
    }

    @Override
    public List<Movie> search(List<Movie> movies) {
        List<Movie> filteredMovies = movies.stream().filter(movie ->
                movie.getReleaseYear() == Integer.parseInt(releaseYear)).toList();
        return filteredMovies;
    }
}
