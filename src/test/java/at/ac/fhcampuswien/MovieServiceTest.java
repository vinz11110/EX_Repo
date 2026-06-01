package at.ac.fhcampuswien;

import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.repositories.IMovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import at.ac.fhcampuswien.repositories.MovieRepository;
import services.MovieService;
import at.ac.fhcampuswien.exceptions.DatabaseException;
import at.ac.fhcampuswien.exceptions.MovieNotFoundException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//enabling Mockito
@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {
    //creating the service that is being tested
    private MovieService movieService;

    //mocked version of database
    @Mock
    private IMovieRepository movieRepository;

    //list of movies that is being tested
    private List<Movie> testMovies;

    @BeforeEach
    void setUp() {
        testMovies = new ArrayList<>(Arrays.asList(
                new Movie("The Machinist", "Thriller", 2005),
                new Movie("Matrix", "Science-Fiction", 1999),
                new Movie("No Country for Old Men", "Thriller", 2007),
                new Movie("Inception", "Sci-Fi", 2010)
        ));

        //injecting fake repostiory into MovieService
        movieService = new MovieService(movieRepository);
    }

    @Test
    void should_throw_database_exception_when_deleting_movie_with_db_error() throws DatabaseException, MovieNotFoundException {
        Movie movie = testMovies.get(3);
        when(movieRepository.findAll()).thenReturn(testMovies);
        when(movieRepository.delete(movie)).thenThrow(new DatabaseException("Database connection Error"));
        //verify that error has been caught correctly by the Database
        assertThrows(DatabaseException.class, () -> {
            movieService.deleteMovie("Inception", "Sci-Fi", 2010);
        });
    }

    @Test
    void should_throw_movie_not_found_exception_when_updating_non_existent_movie() throws DatabaseException{
        Movie updateData = new Movie("Unknown", "Drama", 2000);
        //Database can't find movie to update
        when(movieRepository.findAll()).thenReturn(testMovies);

        //MovieNotFoundException
        assertThrows(MovieNotFoundException.class, () -> {
            movieService.updateMovie(updateData.getId(), updateData);
        });
    }

    @Test
    void givenMovieList_whenGetAllMovies_thenReturnsJsonArray() throws DatabaseException {
        //Database selects fake movie list
        when(movieRepository.findAll()).thenReturn(testMovies);

        // calling real service method
        String jsonResult = movieService.getAllMovies();
        //check if correctly formatted
        assertTrue(jsonResult.startsWith("["));
        assertTrue(jsonResult.endsWith("]"));
        assertTrue(jsonResult.contains("Matrix"));
        assertTrue(jsonResult.contains("No Country for Old Men"));
        assertTrue(jsonResult.contains("The Machinist"));
    }

    @Test
    void givenEmptyMovieList_whenGetAllMovies_thenReturnsEmptyJsonArray() throws DatabaseException {
        //simulate empty database
        when(movieRepository.findAll()).thenReturn(new ArrayList<>());
        String jsonResult = movieService.getAllMovies();
        assertEquals("[]", jsonResult);
    }

    @Test
    void shouldFilterByTitle() throws DatabaseException {
        //fake movies into database
        when(movieRepository.findAll()).thenReturn(testMovies);

        //search for matrix
        String result = movieService.searchMovies("matrix", null, null);

        //Matrix is found, other titles are filtered
        assertTrue(result.toLowerCase().contains("matrix"));
        assertFalse(result.toLowerCase().contains("machinist"));
    }

    @Test
    void shouldFilterByGenre() throws DatabaseException {
        when(movieRepository.findAll()).thenReturn(testMovies);
        //search for Thriller genre
        String result = movieService.searchMovies(null, "Thriller", null);
        //Thrillers are found, Science-Fiction are filtered
        assertTrue(result.toLowerCase().contains("thriller"));
        assertFalse(result.toLowerCase().contains("science-fiction"));
    }

    @Test
    void shouldFilterByReleaseYear() throws DatabaseException {
        //fake movies into database
        when(movieRepository.findAll()).thenReturn(testMovies);
        //Search for year 1999
        String result = movieService.searchMovies(null, null, "1999");
        //1999 is found, 2007 is filtered
        assertTrue(result.contains("1999"));
        assertFalse(result.contains("2007"));
    }

    @Test
    void givenExistingMovie_whenDeleteMovie_thenRepositoryDeleteIsCalled() throws DatabaseException, MovieNotFoundException {
        //Database deletes successfully
        when(movieRepository.delete(any(Movie.class))).thenReturn(true);
        when(movieRepository.findAll()).thenReturn(testMovies);
        //Deleting movie
        movieService.deleteMovie("No Country for Old Men", "Thriller", 2007);
        //checks that database deletes the movie one time
        verify(movieRepository, times(1)).delete(any(Movie.class));
    }

    @Test
    void shouldAddMovieSuccessfully() throws DatabaseException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);
        //adding movie
        movieService.addMovie(movie);
        // Verify that the service passed the movie to the database
        verify(movieRepository, times(1)).add(movie);
    }

    @Test
    void shouldReturnInvalidMovieDataIfYearTooLow() {
        //Creating movie with invalid year
        Movie movie = new Movie("Not working", "Horror", 1700);
        //service catches bad data
        assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(movie);
        });
    }

    @Test
    void shouldReturnInvalidMovieDataIfTitleMissing() {
        //creating movie with null title
        Movie movie = new Movie(null, "Action", 2000);
        //rejecting movie
        assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(movie);
        });
    }

    @Test
    void update_inputID_correct_return_true() throws DatabaseException, MovieNotFoundException {
        Movie movie = testMovies.get(1);
        when(movieRepository.findAll()).thenReturn(testMovies);

        //telling the database to return 'true' if updated successfully
        when(movieRepository.update(any(Movie.class))).thenReturn(true);

        boolean isUpdated = movieService.updateMovie(movie.getId(), movie);

        //Check that it returns true
        assertTrue(isUpdated);
        verify(movieRepository, times(1)).update(any(Movie.class));
    }
}



