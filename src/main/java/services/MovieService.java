package services;

import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.IMovieRepository;
import at.ac.fhcampuswien.repositories.MovieRepository;
import com.google.gson.Gson;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;


public class MovieService {
   private final IMovieRepository repository;
    Gson gson = new Gson();

    public MovieService(IMovieRepository repository) {
        this.repository = repository;
    }

    public String getAllMovies() {
        return repository.findAll().stream()
                .map(movie -> "{\"id\": \"" + movie.getId() + "\", \"title\": \"" + movie.getTitle() + "\", \"genre\": \"" + movie.getGenre() + "\", \"releaseYear\": " + movie.getReleaseYear() + "}")
                .collect(Collectors.joining(",", "[", "]"));
    }

    public void addMovie(Movie movie) {
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
        }else if (exists) {
            throw new IllegalStateException();
        }

        repository.add(movie);
    }

    public void deleteMovie(UUID id) {
        Movie movie = repository.findAll().stream()
                .filter(m -> m.getId().equals(id)
                ).findFirst().orElseThrow(NoSuchElementException::new);

        boolean deleted = repository.delete(movie);
        if(!deleted) {
            throw new NoSuchElementException();
        }
    }

    public boolean updateMovie(UUID id, Movie updateData) {
        Movie movie = repository.findAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
        if(movie == null){
            throw new MovieNotFoundException();
        }
        movie.setTitle(updateData.getTitle());
        movie.setGenre(updateData.getGenre());
        movie.setReleaseYear(updateData.getReleaseYear());

        return repository.update(movie);
    }

    public String searchMovies(String title, String genre, String releaseYear) {
         return gson.toJson(repository.findAll());
    }
}


