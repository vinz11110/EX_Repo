package services;

import at.ac.fhcampuswien.models.Movie;
import com.google.gson.Gson;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;


public class MovieService {
    private final List<Movie> movies;
    Gson gson = new Gson();

    public MovieService(List<Movie> movies) {
        this.movies = movies;
    }

    public String getAllMovies() {
        return movies.stream()
                .map(movie -> "{\"id\": \"" + movie.getId() + "\", \"title\": \"" + movie.getTitle() + "\", \"genre\": \"" + movie.getGenre() + "\", \"releaseYear\": " + movie.getReleaseYear() + "}")
                .collect(Collectors.joining(",", "[", "]"));
    }

    public void addMovie(Movie movie) {
        boolean exists = movies.stream().anyMatch(m ->
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

        movies.add(movie);
    }

    public void deleteMovie(String title, String genre, int releaseYear) {
        if(movies.removeIf(m ->
                m.getTitle().equals(title) &&
                        m.getGenre().equals(genre) &&
                        m.getReleaseYear() == releaseYear)){
            return;
        } else {
            throw new NoSuchElementException();
        }
    }

    public boolean updateMovie(UUID id, Movie updateData) {
        return movies.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .map(m -> {
                    m.setTitle(updateData.getTitle());
                    m.setGenre(updateData.getGenre());
                    m.setReleaseYear(updateData.getReleaseYear());
                    return true;
                }).orElse(false);
    }

    public String searchMovies(String title, String genre, String releaseYear) {
         return movies.stream()
                .filter(m -> title == null || m.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(m -> genre == null || m.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .filter(m -> releaseYear == null || String.valueOf(m.getReleaseYear()).equals(releaseYear))
                .map(movie -> "{\"id\": \"" + movie.getId() + "\", \"title\": \"" + movie.getTitle() + "\", \"genre\": \"" + movie.getGenre() + "\", \"releaseYear\": " + movie.getReleaseYear() + "}")
                .collect(Collectors.joining(",", "[", "]"));
    }
}


