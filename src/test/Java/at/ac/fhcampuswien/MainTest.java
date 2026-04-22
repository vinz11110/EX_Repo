package at.ac.fhcampuswien;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.MovieService;
import at.ac.fhcampuswien.models.Movie;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

/* Example Test File */
public class MainTest {

    private MovieService movieService;
    private List<Movie> movies;
    @BeforeEach
    void setup() {
        movies = Movie.generateDummyMovies();
        movieService = new MovieService(movies);
    }

    @Test
    void update_inputID_invalid_returns_false() {
        assertThrows(IllegalArgumentException.class, () -> {
            movieService.updateMovie(UUID.fromString("67"),movies.get(2));
        });
    }
    @Test
    void update_inputID_valid_correct_returns_true() {
        assertEquals(true, movieService.updateMovie(movies.get(3).getId(), movies.get(3)));
    }
    @Test
    void update_inputID_valid_incorrect_returns_false() {
        assertEquals(false, movieService.updateMovie(UUID.randomUUID(), movies.get(3)));
    }
    @Test
    void update_inputID_correct_updating_title_returns_true() {
        Movie movie = new Movie("KINGKONG",movies.get(1).getGenre(),movies.get(1).getReleaseYear());
        movieService.updateMovie(movies.get(1).getId(), movie);
        assertEquals(movies.get(1).getTitle(), "KINGKONG");
    }

    @Test
    void update_inputID_correct_updating_genre_returns_true() {
        Movie movie = new Movie(movies.get(1).getTitle(),"HORROR",movies.get(1).getReleaseYear());
        movieService.updateMovie(movies.get(1).getId(), movie);
        assertEquals(movies.get(1).getGenre(), "HORROR");
    }

    @Test
    void update_inputID_correct_updating_releaseYear_returns_true() {
        Movie movie = new Movie(movies.get(1).getTitle(),movies.get(1).getGenre(),2023);
        movieService.updateMovie(movies.get(1).getId(), movie);
        assertEquals(movies.get(1).getReleaseYear(), 2023);
    }

    @Test
    void update_inputID_correct_updating_all_returns_true() {
        Movie movie = new Movie("KINGKONG","HORROR",2023);
        movieService.updateMovie(movies.get(1).getId(), movie);
        assertEquals(movies.get(1).getTitle(), "KINGKONG");
        assertEquals(movies.get(1).getGenre(), "HORROR");
        assertEquals(movies.get(1).getReleaseYear(), 2023);
    }
}

