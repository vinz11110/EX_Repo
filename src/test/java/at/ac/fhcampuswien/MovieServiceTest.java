package at.ac.fhcampuswien;

import at.ac.fhcampuswien.models.Movie;
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

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {
    private MovieService movieService;

    @Mock
    private MovieRepository movieRepository;

    private List<Movie> testMovies;

    @BeforeEach
    void setUp() throws DatabaseException {
        testMovies = new ArrayList<>(Arrays.asList(
                new Movie("The Machinist", "Thriller", 2005),
                new Movie("Matrix", "Science-Fiction", 1999),
                new Movie("No Country for Old Men", "Thriller", 2007)
        ));

        movieService = new MovieService(movieRepository);
    }

    @Test
    void should_throw_database_exception_when_deleting_movie_with_db_error() throws DatabaseException, MovieNotFoundException {
        Movie movie = testMovies.get(0);
        when(movieRepository.findAll()).thenReturn(testMovies);
        when(movieRepository.delete(movie)).thenThrow(new DatabaseException("Database connection Error"));

        assertThrows(DatabaseException.class, () -> {
            movieService.deleteMovie("Inception", "Sci-Fi", 2010);
        });
    }

    @Test
    void should_throw_movie_not_found_exception_when_updating_non_existent_movie() throws DatabaseException, MovieNotFoundException {
        Movie updateData = new Movie("Unknown", "Drama", 2000);
        when(movieRepository.findAll()).thenReturn(testMovies);
        doThrow(new MovieNotFoundException("Movie does not exist in database")).when(movieRepository).update(any(Movie.class));

        assertThrows(MovieNotFoundException.class, () -> {
            movieService.updateMovie(updateData.getId(), updateData);
        });
    }

    @Test
    void givenMovieList_whenGetAllMovies_thenReturnsJsonArray() throws DatabaseException {
        when(movieRepository.findAll()).thenReturn(testMovies);

        String jsonResult = movieService.getAllMovies();
        assertTrue(jsonResult.startsWith("["));
        assertTrue(jsonResult.endsWith("]"));
        assertTrue(jsonResult.contains("Matrix"));
        assertTrue(jsonResult.contains("No Country for Old Men"));
        assertTrue(jsonResult.contains("The Machinist"));
    }

    @Test
    void givenEmptyMovieList_whenGetAllMovies_thenReturnsEmptyJsonArray() throws DatabaseException {
        when(movieRepository.findAll()).thenReturn(new ArrayList<>());
        String jsonResult = movieService.getAllMovies();
        assertEquals("[]", jsonResult);
    }

    @Test
    void shouldFilterByTitle() throws DatabaseException {
        when(movieRepository.findAll()).thenReturn(testMovies);

        String result = movieService.searchMovies("matrix", null, null);

        assertTrue(result.toLowerCase().contains("matrix"));
        assertFalse(result.toLowerCase().contains("machinist"));
    }

    @Test
    void shouldFilterByGenre() throws DatabaseException {
        when(movieRepository.findAll()).thenReturn(testMovies);

        String result = movieService.searchMovies(null, "Thriller", null);

        assertTrue(result.toLowerCase().contains("thriller"));
        assertFalse(result.toLowerCase().contains("science-fiction"));
    }

    @Test
    void shouldFilterByReleaseYear() throws DatabaseException {
        when(movieRepository.findAll()).thenReturn(testMovies);

        String result = movieService.searchMovies(null, null, "1999");

        assertTrue(result.contains("1999"));
        assertFalse(result.contains("2007"));
    }

    @Test
    void givenExistingMovie_whenDeleteMovie_thenRepositoryDeleteIsCalled() throws DatabaseException, MovieNotFoundException {
        when(movieRepository.delete(any(Movie.class))).thenReturn(true);
        when(movieRepository.findAll()).thenReturn(testMovies);
        movieService.deleteMovie("No Country for Old Men", "Thriller", 2007);

        verify(movieRepository, times(1)).delete(any(Movie.class));
    }

    @Test
    void shouldAddMovieSuccessfully() throws IOException, DatabaseException {
        Movie movie = new Movie("Inception", "Sci-Fi", 2010);

        movieService.addMovie(movie);

        verify(movieRepository, times(1)).add(movie);
    }

    @Test
    void shouldReturnInvalidMovieDataIfYearTooLow() {
        Movie movie = new Movie("Not working", "Horror", 1700);

        assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(movie);
        });
    }

    @Test
    void shouldReturnInvalidMovieDataIfTitleMissing() {
        Movie movie = new Movie(null, "Action", 2000);

        assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(movie);
        });
    }

    @Test
    void update_inputID_correct_return_true() throws DatabaseException, MovieNotFoundException {
        Movie movie = testMovies.get(1);
        when(movieRepository.findAll()).thenReturn(testMovies);
        when(movieRepository.update(any(Movie.class))).thenReturn(true);

        boolean isUpdated = movieService.updateMovie(movie.getId(), movie);

        assertTrue(isUpdated);
        verify(movieRepository, times(1)).update(any(Movie.class));
    }
}



