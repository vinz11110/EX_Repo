package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import at.ac.fhcampuswien.models.Movie;

import java.util.List;

public interface IMovieRepository {
    void add(Movie movie) throws DatabaseException;

    List<Movie> findAll() throws DatabaseException;

    boolean delete(Movie movie) throws MovieNotFoundException, DatabaseException;

    boolean update(Movie movie) throws MovieNotFoundException, DatabaseException;
}
