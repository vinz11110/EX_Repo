package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.MovieController;
import at.ac.fhcampuswien.models.Movie;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.MovieService;

import java.beans.BeanProperty;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MovieServiceTest {
    private MovieService dummyMovieService;
    private MovieService movieService;
    private List<Movie> testMovies;
    private List<Movie> movies;

    @BeforeEach
    void setUp() {
        // Initialize list of movies before taking a test
        testMovies = new ArrayList<>();
        testMovies.add(new Movie("The Machinist", "Thriller", 2005));
        testMovies.add(new Movie("Matrix", "Science Fiction", 1999));
        testMovies.add(new Movie("No Country for Old Men", "Thriller", 2007));

        movieService = new MovieService(testMovies);

        movies = Movie.generateDummyMovies();
        dummyMovieService = new MovieService(movies);
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


        @Test
        void shouldAddMovieSuccessfully() throws IOException {
            Movie movie = new Movie("Inception", "Sci-Fi", 2010);

            movieService.addMovie(movie);

            assertTrue(testMovies.stream().anyMatch(m -> m.getTitle().equals("Inception") &&
                                                                m.getGenre().equals("Sci-Fi") &&
                                                                m.getReleaseYear() == 2010));


        }

        @Test
        void shouldReturnErrorIfMovieAlreadyExists()  {
            Movie movie = new Movie("Matrix", "Science Fiction", 1999);

            assertThrows(IllegalStateException.class, () -> { movieService.addMovie(movie);});
            assertEquals(3, testMovies.size());
        }

        @Test
        void shouldReturnInvalidMovieDataIfYearTooLow() {
            Movie movie = new Movie("Bad Movie", "Horror", 1800);

            assertThrows(IllegalArgumentException.class, () -> {movieService.addMovie(movie);});
            assertEquals(3, testMovies.size());
        }

        @Test
        void  shouldReturnInvalidMovieDataIfTitleMissing() {
            Movie movie = new Movie(null, "Action", 2000);

            assertThrows(IllegalArgumentException.class, () -> {movieService.addMovie(movie);});
        }
        @Test
        void  shouldReturnInvalidMovieDataIfGenreMissing() {
            Movie movie = new Movie("Inception", null, 2000);

            assertThrows(IllegalArgumentException.class, () -> {movieService.addMovie(movie);});
        }

        @Test
        void shouldFilterByTitle()  {
        String result = movieService.searchMovies("matrix", null, null);

        assertTrue(result.toLowerCase().contains("matrix"));
        assertFalse(result.toLowerCase().contains("machinist"));

        }
        @Test
        void shouldFilterByGenre()  {
            String result = movieService.searchMovies(null, "Thriller", null);

            assertTrue(result.toLowerCase().contains("thriller"));
            assertFalse(result.toLowerCase().contains("science fiction"));

        }

        @Test
        void shouldFilterByReleaseYear() {
            String result = movieService.searchMovies(null, null, "1999");
            assertTrue(result.contains("1999"));
            assertFalse(result.contains("2007"));
        }



    @Test
    void update_inputID_invalid_returns_false() {
        assertThrows(IllegalArgumentException.class, () -> {
            movieService.updateMovie(UUID.fromString("67"),movies.get(2));
        });
    }
    @Test
    void update_inputID_valid_correct_returns_true() {
        assertEquals(true, dummyMovieService.updateMovie(movies.get(3).getId(), movies.get(3)));
    }
    @Test
    void update_inputID_valid_incorrect_returns_false() {
        assertEquals(false, dummyMovieService.updateMovie(UUID.randomUUID(), movies.get(3)));
    }
    @Test
    void update_inputID_correct_updating_title_returns_true() {
        Movie movie = new Movie("KINGKONG",movies.get(1).getGenre(),movies.get(1).getReleaseYear());
        dummyMovieService.updateMovie(movies.get(1).getId(), movie);
        assertEquals(movies.get(1).getTitle(), "KINGKONG");
    }

    @Test
    void update_inputID_correct_updating_genre_returns_true() {
        Movie movie = new Movie(movies.get(1).getTitle(),"HORROR",movies.get(1).getReleaseYear());
        dummyMovieService.updateMovie(movies.get(1).getId(), movie);
        assertEquals(movies.get(1).getGenre(), "HORROR");
    }

    @Test
    void update_inputID_correct_updating_releaseYear_returns_true() {
        Movie movie = new Movie(movies.get(1).getTitle(),movies.get(1).getGenre(),2023);
        dummyMovieService.updateMovie(movies.get(1).getId(), movie);
        assertEquals(movies.get(1).getReleaseYear(), 2023);
    }

    @Test
    void update_inputID_correct_updating_all_returns_true() {
        Movie movie = new Movie("KINGKONG","HORROR",2023);
        dummyMovieService.updateMovie(movies.get(1).getId(), movie);
        assertEquals(movies.get(1).getTitle(), "KINGKONG");
        assertEquals(movies.get(1).getGenre(), "HORROR");
        assertEquals(movies.get(1).getReleaseYear(), 2023);
    }
}


