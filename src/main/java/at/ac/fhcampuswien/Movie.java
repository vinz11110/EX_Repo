package at.ac.fhcampuswien;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private String title;
    private String genre;
    private String releaseYear;

    public Movie(String title, String genre, String releaseYear) {
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }

    public static List<Movie> generateDummyMovies() {
        List<Movie> dummyMovies = new ArrayList<>();

        return dummyMovies;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }
}


