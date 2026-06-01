package services;

import at.ac.fhcampuswien.Adapter;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.IMovieRepository;
import at.ac.fhcampuswien.repositories.MovieRepository;
import services.SearchStrategy.SearchStrategy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;


public class MovieService {
   private final IMovieRepository repository;
    Adapter adapter = new Adapter();

    public MovieService(IMovieRepository repository) {
        this.repository = repository;
    }

    public String getAllMovies() throws DatabaseException {
        return adapter.getJsonFromMovieList(repository.findAll());
    }

    public void addMovie(Movie movie) throws DatabaseException {
        boolean exists = repository.findAll().stream().anyMatch(m ->
                m.getTitle().equalsIgnoreCase(movie.getTitle()) &&
                        m.getGenre().equalsIgnoreCase(movie.getGenre()) &&
                        m.getReleaseYear() == movie.getReleaseYear());
        if (movie == null ||
                movie.getTitle() == null ||
                movie.getGenre() == null ||
                movie.getReleaseYear() < 1900 ||
                movie.getReleaseYear() > 2100) {

            throw new IllegalArgumentException();
        } else if (exists) {
            throw new IllegalStateException();
        }

        repository.add(movie);
    }

    public void deleteMovie(String title, String genre, int releaseYear) throws MovieNotFoundException, DatabaseException {
        Movie movie = repository.findAll().stream()
                .filter(m -> m.getTitle().equals(title) &&
                        m.getGenre().equals(genre) &&
                        m.getReleaseYear() == releaseYear
                ).findFirst().orElseThrow(NoSuchElementException::new);

        boolean deleted = repository.delete(movie);
        if (!deleted) {
            throw new MovieNotFoundException("Invalid Movie Data provided");
        }
    }

    public boolean updateMovie(UUID id, Movie updateData) throws MovieNotFoundException, DatabaseException {
        Movie movie = repository.findAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (movie == null) {
            throw new MovieNotFoundException("Invalid Movie Data provided");
        }
        movie.setTitle(updateData.getTitle());
        movie.setGenre(updateData.getGenre());
        movie.setReleaseYear(updateData.getReleaseYear());

        return repository.update(movie);
    }

    public List<Movie> searchMovies(SearchStrategy strategy) throws DatabaseException {
        List<Movie> movies = repository.findAll();

        return strategy.search(movies);
    }
}


