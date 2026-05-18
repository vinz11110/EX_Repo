package at.ac.fhcampuswien.repositories;

import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.utils.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MovieRepository implements IMovieRepository {


    public void add(Movie movie) {
        try (Connection conn = DatabaseUtil.getConnection()){
            String insertSQL = "INSERT INTO movies(id,title,genre,releaseYear) VALUES (?,?,?,?)";
            try(PreparedStatement statement = conn.prepareStatement(insertSQL) ){
                statement.setObject(1, movie.getId());
                statement.setString(2, movie.getTitle());
                statement.setString(3, movie.getGenre());
                statement.setInt(4, movie.getReleaseYear());

                statement.executeUpdate();
                System.out.println("Movie added Successfully");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new DatabaseException("Error in Databank");
        }
    }

    public List<Movie> findAll(){
        List<Movie> movies = new ArrayList<>();

        String querySQL = "SELECT * FROM movies";
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(querySQL);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UUID id = (UUID) resultSet.getObject("id");
                    String title = resultSet.getString("title");
                    String genre = resultSet.getString("genre");
                    int releaseYear = resultSet.getInt("releaseYear");

                    Movie movie = new Movie(id, title, genre, releaseYear);
                    movies.add(movie);
                }

            }
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new DatabaseException("Error in Databank");
        }
        return movies;
    }

    public boolean delete(Movie movie){
        String deleteSQL = "DELETE FROM movies WHERE title = ? AND genre = ? AND releaseYear = ?";
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(deleteSQL)) {
                statement.setString(1, movie.getTitle());
                statement.setString(2, movie.getGenre());
                statement.setInt(3, movie.getReleaseYear());

                int rowsUpdated = statement.executeUpdate();
                if(rowsUpdated == 0){
                    throw new MovieNotFoundException("Movie not found for deletion");
                }else{
                    return true;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new DatabaseException("Error in Databank");
        }
    }
    public boolean update(Movie movie){
        String updateSQL = "UPDATE MOVIES SET title = ?, genre = ?, releaseYear = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(updateSQL)) {
                statement.setString(1, movie.getTitle());
                statement.setString(2, movie.getGenre());
                statement.setInt(3, movie.getReleaseYear());
                statement.setObject(4, movie.getId());

                if(statement.executeUpdate()==0){
                    throw new MovieNotFoundException("No Movie found to update");
                }else{
                return true;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            throw new DatabaseException("Error in Databank");
        }
    }
}

