package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public interface IMovieRepository {
    void add(Movie movie);

    List<Movie> findAll();

    boolean delete(Movie movie);

    boolean update(Movie movie);
}
