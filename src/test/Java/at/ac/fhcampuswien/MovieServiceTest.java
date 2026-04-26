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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

        private HttpExchange mockExchangePost(String json) throws IOException {
            HttpExchange exchange = mock(HttpExchange.class);

            when(exchange.getRequestMethod()).thenReturn("POST");
            when(exchange.getRequestURI()).thenReturn(java.net.URI.create("/api/movies/add"));
            when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

            when(exchange.getResponseHeaders()).thenReturn(new Headers());
            OutputStream responseStream = new ByteArrayOutputStream();
            when(exchange.getResponseBody()).thenReturn(responseStream);

            return exchange;
        }

        private HttpExchange mockExchangeGetSearch(String query, ByteArrayOutputStream response) throws IOException {
            HttpExchange exchange = mock(HttpExchange.class);

            when(exchange.getRequestMethod()).thenReturn("GET");
            when(exchange.getRequestURI())
                    .thenReturn(java.net.URI.create("/api/movies/search?" + query));

            when(exchange.getResponseHeaders()).thenReturn(new Headers());
            when(exchange.getResponseBody()).thenReturn(response);

            return exchange;
        }


        @Test
        void shouldAddMovieSuccessfully() throws IOException {
            MovieController controller = new MovieController();

            String json = """
                {
                "title": "Inception",
                "genre": "Sci-Fi",
                "releaseYear": 2010
                }
                """;

            HttpExchange exchange = mockExchangePost(json);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(201), anyLong());
        }

        @Test
        void shouldReturnErrorIfMovieAlreadyExists() throws IOException {
            MovieController controller = new MovieController();

            String json = """
            {
              "title": "Duplicate",
              "genre": "Drama",
              "releaseYear": 2010
            }
            """;

            HttpExchange exchange1 = mockExchangePost(json);
            HttpExchange exchange2 = mockExchangePost(json);

            controller.handle(exchange1); // first add
            controller.handle(exchange2); // duplicate

            verify(exchange2).sendResponseHeaders(eq(400), anyLong());
        }

        @Test
        void shouldReturnInvalidMovieDataIfYearTooLow() throws IOException {
            MovieController controller = new MovieController();

            String json = """
            {
              "title": "Bad Movie",
              "genre": "Horror",
              "releaseYear": 1800
            }
            """;

            HttpExchange exchange = mockExchangePost(json);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(400), anyLong());
        }

        @Test
        void  shouldReturnInvalidMovieDataIfTitleMissing() throws IOException {
            MovieController controller = new MovieController();

            String json = """
            {
              "genre": "Action",
              "releaseYear": 2000
            }
            """;

            HttpExchange exchange = mockExchangePost(json);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(400), anyLong());
        }
        @Test
        void  shouldReturnInvalidMovieDataIfGenreMissing() throws IOException {
            MovieController controller = new MovieController();

            String json = """
            {
              "Title": "Inception",
              "releaseYear": 2000
            }
            """;

            HttpExchange exchange = mockExchangePost(json);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(400), anyLong());
        }
        @Test
        void  shouldReturnInvalidMovieDataIfReleaseYearMissing() throws IOException {
            MovieController controller = new MovieController();

            String json = """
            {
              "Title": "Inception",
              "genre": "Action"
            }
            """;

            HttpExchange exchange = mockExchangePost(json);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(400), anyLong());
        }







        @Test
        void shouldFilterByTitle() throws IOException {
            MovieController controller = new MovieController();

            ByteArrayOutputStream response = new ByteArrayOutputStream();

            HttpExchange exchange = mockExchangeGetSearch("title=dark", response);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(200), anyLong());

            String result = response.toString().toLowerCase();
            assertTrue(result.contains("dark"));
        }
        @Test
        void shouldFilterByGenre() throws IOException {
            MovieController controller = new MovieController();

            ByteArrayOutputStream response = new ByteArrayOutputStream();

            HttpExchange exchange = mockExchangeGetSearch("genre=action", response);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(200), anyLong());

            String result = response.toString().toLowerCase();
            assertTrue(result.contains("action"));
        }

        @Test
        void shouldFilterByReleaseYear() throws IOException {
            MovieController controller = new MovieController();

            ByteArrayOutputStream response = new ByteArrayOutputStream();

            HttpExchange exchange = mockExchangeGetSearch("releaseYear=2000", response);

            controller.handle(exchange);

            verify(exchange).sendResponseHeaders(eq(200), anyLong());

            String result = response.toString().toLowerCase();
            assertTrue(result.contains("2000"));
        }
    }


