package at.ac.fhcampuswien.services;

import at.ac.fhcampuswien.models.Movie;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class MovieService {
    private final List<Movie> movies;

    public MovieService(List<Movie> movies) {
        this.movies = movies;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public boolean addMovie(Movie movie) {
        if (movie == null || movie.getTitle() == null || movie.getGenre() == null || movie.getReleaseYear() < 1900 || movie.getReleaseYear() > 2100) {
            throw new IllegalArgumentException("Invalid movie Data");
        }

        boolean exists = movies.stream().anyMatch(m ->
                m.getTitle().equalsIgnoreCase(movie.getTitle()) &&
                m.getGenre().equalsIgnoreCase(movie.getGenre()) &&
                m.getReleaseYear() == movie.getReleaseYear());

        if (exists) {
            return false;
        }

        movies.add(movie);
        return true;
    }

    public boolean deleteMovie(String title, String genre, int releaseYear) {
        return movies.removeIf(m ->
                m.getTitle().equals(title) &&
                m.getGenre().equals(genre) &&
                m.getReleaseYear() == releaseYear);
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

    public List<Movie> searchMovies(String title, String genre, String releaseYear) {
        return movies.stream()
                .filter(m -> title == null || m.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(m -> genre == null || m.getGenre().toLowerCase().contains(genre.toLowerCase()))
                .filter(m -> releaseYear == null || String.valueOf(m.getReleaseYear()).equals(releaseYear))
                .collect(Collectors.toList());
    }
}


