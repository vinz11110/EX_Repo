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
        return gson.toJson(repository.findAll());
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

    public void deleteMovie(String title, String genre, int releaseYear) {
        Movie movie = repository.findAll().stream()
                .filter(m -> m.getTitle().equals(title) &&
                                    m.getGenre().equals(genre) &&
                                    m.getReleaseYear() == releaseYear
                ).findFirst().orElseThrow(NoSuchElementException::new);

        boolean deleted = repository.delete(movie);
    }

    public boolean updateMovie(UUID id, Movie updateData) {
        Movie movie = repository.findAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
        if(movie == null){
            return false;
        }
        movie.setTitle(updateData.getTitle());
        movie.setGenre(updateData.getGenre());
        movie.setReleaseYear(updateData.getReleaseYear());

        return repository.update(movie);
    }

    public String searchMovies(String title, String genre, String releaseYear) {
        List<Movie> filteredMovies = repository.findAll().stream()
                .filter(movie -> {
                    boolean matches = true;
                    // Filter by title
                    if (title != null && !title.isBlank()) {
                        matches = matches &&
                                movie.getTitle().toLowerCase()
                                        .contains(title.toLowerCase());
                    }
                    // Filter by genre
                    if (genre != null && !genre.isBlank()) {
                        matches = matches &&
                                movie.getGenre().contains(genre);
                    }
                    // Filter by release year
                    if (releaseYear != null && !releaseYear.isBlank()) {
                        try {
                            int year = Integer.parseInt(releaseYear);
                            matches = matches &&
                                    movie.getReleaseYear() == year;
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid release year");
                        }
                    }
                    return matches;
                })
                .toList();

        return gson.toJson(filteredMovies);
    }
}


