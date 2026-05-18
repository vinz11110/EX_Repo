package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.MovieController;
import at.ac.fhcampuswien.models.Movie;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import services.MovieService;
import at.ac.fhcampuswien.repositories.*;
import at.ac.fhcampuswien.models.Movie;
import java.io.IOException;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieServiceTest {
    @Mock
    private IMovieRepository repository;

    private MovieService dummyMovieService;
    private MovieService movieService;
    private List<Movie> testMovies;
    private List<Movie> movies;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize list of movies before taking a test
        testMovies = new ArrayList<>();
        testMovies.add(new Movie("The Machinist", "Thriller", 2005));
        testMovies.add(new Movie("Matrix", "Science Fiction", 1999));
        testMovies.add(new Movie("No Country for Old Men", "Thriller", 2007));

        movieService = new MovieService(repository);

        movies = Movie.generateDummyMovies();
        dummyMovieService = new MovieService(repository);
    }

    // Tests for getAllMovies()

    @Test
    void givenMovieList_whenGetAllMovies_thenReturnsJsonArray() {
        when(repository.findAll()).thenReturn(testMovies);
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
        MovieService emptyService = new MovieService(repository);

        // When: requesting all movies
        String jsonResult = emptyService.getAllMovies();

        // Then: Should return only "[]"
        assertEquals("[]", jsonResult);
    }

    // Tests for deleteMovie():

    @Test
    void givenExistingMovie_whenDeleteMovie_thenMovieIsRemovedFromList() {
        // Given: "No Country for Old Men" is in the list
        Movie movieToDelete = testMovies.get(2);

        // When: Movie is deleted
        when(repository.findAll()).thenReturn(testMovies);
        when(repository.delete(movieToDelete)).thenReturn(true);

        movieService.deleteMovie(movieToDelete.getId());
        // Then: List should decrease in size and movie should be removed
        verify(repository).delete(movieToDelete);
    }

    @Test
    void givenNonExistingMovie_whenDeleteMovie_thenThrowNoSuchElementException() {
        // Given: Movie that doesn't exist in the list

        // When: Attempting to delete the movie should throw an exception
        when(repository.findAll()).thenReturn(testMovies);
        UUID nonExistingID = UUID.randomUUID();
        assertThrows(NoSuchElementException.class, () -> {
            movieService.deleteMovie(nonExistingID);
        });

        // deletion never occurred
        verify(repository, never()).delete(any());
    }



        @Test
        void shouldAddMovieSuccessfully() throws IOException {
            Movie movie = new Movie("Inception", "Sci-Fi", 2010);

            when(repository.findAll()).thenReturn(testMovies);

            movieService.addMovie(movie);

            verify(repository).add(movie);
        }

        @Test
        void shouldReturnErrorIfMovieAlreadyExists()  {
            Movie movie = new Movie("Matrix", "Science Fiction", 1999);
            when(repository.findAll()).thenReturn(testMovies);

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
        when(repository.findAll()).thenReturn(testMovies);
        String result = movieService.searchMovies("matrix", null, null);


        assertTrue(result.toLowerCase().contains("matrix"));
        assertFalse(result.toLowerCase().contains("machinist"));
        verify(repository, atLeastOnce()).findAll();
        }
        @Test
        void shouldFilterByGenre()  {
            when(repository.findAll()).thenReturn(testMovies);
            String result = movieService.searchMovies(null, "Thriller", null);

            assertTrue(result.toLowerCase().contains("thriller"));
            assertFalse(result.toLowerCase().contains("science fiction"));
            verify(repository, atLeastOnce()).findAll();
        }

        @Test
        void shouldFilterByReleaseYear() {
            when(repository.findAll()).thenReturn(testMovies);
            String result = movieService.searchMovies(null, null, "1999");


            assertTrue(result.contains("1999"));
            assertFalse(result.contains("2007"));
            verify(repository, atLeastOnce()).findAll();
        }



    @Test
    void update_inputID_invalid_returns_false() {
        assertThrows(IllegalArgumentException.class, () -> {
            movieService.updateMovie(UUID.fromString("67"),movies.get(2));
        });
    }
    @Test
    void update_inputID_valid_correct_returns_true() {
        when(repository.findAll()).thenReturn(testMovies);
        when(repository.update(any(Movie.class))).thenReturn(true);
        assertEquals(true, dummyMovieService.updateMovie(testMovies.get(2).getId(), testMovies.get(2)));
    }
    @Test
    void update_inputID_valid_incorrect_returns_false() {
        when(repository.findAll()).thenReturn(testMovies);
        assertEquals(false, dummyMovieService.updateMovie(UUID.randomUUID(), movies.get(3)));
    }
    @Test
    void update_inputID_correct_updating_title_returns_true() {
        when(repository.findAll()).thenReturn(testMovies);
        when(repository.update(any(Movie.class))).thenReturn(true);

        Movie movie = new Movie("KINGKONG", testMovies.get(1).getGenre(), testMovies.get(1).getReleaseYear());
        boolean result = dummyMovieService.updateMovie(testMovies.get(1).getId(), movie);


        assertTrue(result);
    }

    @Test
    void update_inputID_correct_updating_genre_returns_true() {
        when(repository.findAll()).thenReturn(testMovies);
        when(repository.update(any(Movie.class))).thenReturn(true);

        Movie movie = new Movie(testMovies.get(1).getTitle(), "HORROR", testMovies.get(1).getReleaseYear());
        boolean result = dummyMovieService.updateMovie(testMovies.get(1).getId(), movie);


        assertTrue(result);
    }

    @Test
    void update_inputID_correct_updating_releaseYear_returns_true() {
        when(repository.findAll()).thenReturn(testMovies);
        when(repository.update(any(Movie.class))).thenReturn(true);

        Movie movie = new Movie(testMovies.get(1).getTitle(), testMovies.get(1).getGenre(), 2023);
        boolean result = dummyMovieService.updateMovie(testMovies.get(1).getId(), movie);


        assertTrue(result);
    }

    @Test
    void update_inputID_correct_updating_all_returns_true() {

        when(repository.findAll()).thenReturn(testMovies);
        when(repository.update(any(Movie.class))).thenReturn(true);

        Movie updatedMovie = new Movie("KINGKONG", "HORROR", 2023);

        boolean result = dummyMovieService.updateMovie(testMovies.get(1).getId(), updatedMovie);

        testMovies.get(1).setTitle("KINGKONG");
        testMovies.get(1).setGenre("HORROR");
        testMovies.get(1).setReleaseYear(2023);

        assertTrue(result);

        assertEquals("KINGKONG", testMovies.get(1).getTitle());
        assertEquals("HORROR", testMovies.get(1).getGenre());
        assertEquals(2023, testMovies.get(1).getReleaseYear());
    }
}


