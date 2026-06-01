package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;

public interface IRepository {
    void add(Object object) throws DatabaseException;

    boolean delete(Object object) throws MovieNotFoundException, DatabaseException;

    boolean update(Object object) throws MovieNotFoundException, DatabaseException;

}
