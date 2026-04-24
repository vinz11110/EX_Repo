package at.ac.fhcampuswien;

import at.ac.fhcampuswien.models.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.MovieService;

import java.beans.BeanProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {
    private MovieService movieService;
    private List<Movie> testMovies;

    @BeforeEach
    void setUp() {
        // Initialize list of movies before taking a test
        testMovies = new ArrayList<>();
        testMovies.add(new Movie("The Machinist", "Thriller", 2005));
        testMovies.add(new Movie("Matrix", "Science Fiction", 1999));
        testMovies.add(new Movie("No Country for Old Men", "Thriller", 2007));

        movieService = new MovieService(testMovies);
    }

    // Tests for getAllMovies()

    @Test
    void givenMovieList_whenGetAllMovies_thenReturnsJsonArray() {
        // When: Requesting all movies
        String jsonResult = movieService.getAllMovies();

        // Then: Returned strings should be JSON array
        assertTrue(jsonResult.startsWith("["));
        assertTrue(jsonResult.endsWith("]"));
        assertTrue(jsonResult.contains("Matrix"));
        assertTrue(jsonResult.contains("No Country for Old Men"));
        assertTrue(jsonResult.contains("The Machinist"));
    }

    @Test
    void givenEmptyMovieList_whenGetAllMovies_thenReturnsEmptyJsonArray() {
        // Given: empty list
        MovieService emptyService = new MovieService(new ArrayList<>());

        // When: requesting all movies
        String jsonResult = emptyService.getAllMovies();

        // Then: Should return only "[]"
        assertEquals("[]", jsonResult);
    }

    // Tests for deleteMovie():

    @Test
    void givenExistingMovie_whenDeleteMovie_thenMovieIsRemovedFromList() {
        // Given: "No Country for Old Men" is in the list

        // When: Movie is deleted
        movieService.deleteMovie("No Country for Old Men", "Thriller", 2007);

        // Then: List should decrease in size and movie should be removed
        assertEquals(2, testMovies.size());
        assertFalse(testMovies.stream().anyMatch(m -> m.getTitle().equals("No Country for Old Men")));
    }

    @Test
    void givenNonExistingMovie_whenDeleteMovie_thenThrowNoSuchElementException() {
        // Given: Movie that doesn't exist in the list

        // When: Attempting to delete the movie should throw an exception
        assertThrows(NoSuchElementException.class, () -> {
            movieService.deleteMovie("Batman Begins", "Action", 2005);
        });

        // list size should remain the same
        assertEquals(3, testMovies.size());
    }

    @Test
    void givenPartialMovieMatch_whenDeleteMovie_thenThrowsNoSuchElementException() {

        // should throw an exception because movie doesn't fully match, in this case wrong releaseYear
        assertThrows(NoSuchElementException.class, () -> {
            movieService.deleteMovie("No Country for Old Men", "Thriller", 1999);
        });

        // list size should remain the same
        assertEquals(3, testMovies.size());
    }
}
