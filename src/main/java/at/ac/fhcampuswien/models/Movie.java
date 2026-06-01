package at.ac.fhcampuswien.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Movie {

    private UUID id;
    private String title;
    private String genre;
    private int releaseYear;


    public Movie() {
        this.id = UUID.randomUUID();
    }

    public Movie(String title, String genre, int releaseYear) {
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.id = UUID.randomUUID();
    }

    public Movie(UUID id, String title, String genre, int releaseYear) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
    }


    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public UUID getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    @Override
    public String toString() {
        return "Movie{id=" + this.getId() + ", title = " + this.getTitle() + ", genre= "
                + this.getGenre() + ", releaseYear= " + getReleaseYear() + "}";
    }


//    public static List<Movie> generateDummyMovies(){
//        List<Movie> movies = new ArrayList<>();
//        Random random = new Random();
//
//        String[] titles = {"Inception","The Dark knight","A Beautiful Mind","The Machinist","Parasite","The Lord of the Rings"};
//        String[] genres = {"Action", "Comedy","Sci-Fi", "Drama", "Horror", "Romance", "Thriller", };
//
//        for (int i = 0; i < 20; i++){
//            String title = titles[random.nextInt(titles.length)];
//            String genre = genres[random.nextInt(genres.length)];
//            int year = 1960 + random.nextInt(66);
//
//            movies.add(new Movie(title, genre, year));
//        }
//        return movies;
//    }
}
