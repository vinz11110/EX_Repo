package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.models.Movie;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.MovieService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MovieController implements HttpHandler {
    private final String BASE = "/api/movies/";
    private List<Movie> movies = Movie.generateDummyMovies();
    Gson gson = new Gson();
    MovieService movieService = new MovieService(movies);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();


        //routing logic
        switch (path) {
            case BASE -> handleBaseRequest(method, exchange);
            case BASE + "delete" -> handleDeleteRequest(method, exchange);
            case BASE + "add" -> handlePostRequest(method, exchange);
            case BASE + "getAll" -> handleGetAllRequest(method, exchange);
            case BASE + "update" -> handleUpdateRequest(method, exchange);
            case BASE + "search" -> handleSearchQueryRequest(method, exchange);

            default -> {
                //if endpoint doesn't exist, return error
                String response = "{ \"error\": \"Path not found\"}";
                ApiUtils.sendResponse(exchange, 404, response);
            }
        }
    }

    private void handleGetAllRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "GET" -> {
                try{
                    String response = movieService.getAllMovies();
                    ApiUtils.sendResponse(exchange, 200, response);
                }catch(DatabaseException e){
                    String response = "{ \"error\": \"Internal Server Error\" }";
                    ApiUtils.sendResponse(exchange, 500, response);
                }catch(Exception e){
                    String response = "{ \"error\": \"An Unexpected Error occurred\" }";
                    ApiUtils.sendResponse(exchange, 500, response);
                }

            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void handlePostRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "POST" -> {
            try{
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie movie = gson.fromJson(requestBody, Movie.class);

               movieService.addMovie(movie);

               String response = "{ \"message\": \"Movie added successfully\" }";
               ApiUtils.sendResponse(exchange, 201, response);
            }catch (IllegalStateException e){
               String response = "{ \"error\": \"Movie already exists\"}";
               ApiUtils.sendResponse(exchange, 400, response);
            }catch (IllegalArgumentException e){
               String response = "{ \"error\": \"Invalid movie Data\"}";
               ApiUtils.sendResponse(exchange, 400, response);
            }catch(DatabaseException e){
                String response = "{ \"error\": \"Internal Server Error\"}";
               ApiUtils.sendResponse(exchange, 500, response);
            } catch (JsonSyntaxException e) {
                String response = "{ \"error\": \"Malformed Json Syntax\" }";
                ApiUtils.sendResponse(exchange, 400, response);
            }catch (NullPointerException e) {
                String response = "{ \"error\": \"Movie Data Missing from request\" }";
                ApiUtils.sendResponse(exchange, 400, response);
            }catch(Exception e){
                String response = "{ \"error\": \"An Unexpected Error occurred\" }";
                ApiUtils.sendResponse(exchange, 500, response);
            }
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void handleDeleteRequest(String method, HttpExchange exchange) throws IOException {
        if (!method.equals("DELETE")) {
            String response = "{ \"error\": \"Method not allowed\" }";
            ApiUtils.sendResponse(exchange, 405, response);
            return;
        }

        //read request Body
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (requestBody == null || requestBody.trim().isEmpty()) {
            String response = "{ \"error\": \"Invalid movie data\" }";
            ApiUtils.sendResponse(exchange, 400, response);
            return;
        }

        try {
            Movie movie = gson.fromJson(requestBody, Movie.class);
            String title = movie.getTitle();
            String genre = movie.getGenre();
            int releaseYear = movie.getReleaseYear();

            if (title == null || genre == null || releaseYear < 1900) {
                String response = "{ \"error\": \"Invalid movie data\" }";
                ApiUtils.sendResponse(exchange, 400, response);
                return;
            }

            movieService.deleteMovie(title, genre, releaseYear);

            String response = "{ \"message\": \"Movie deleted successfully\" }";
            ApiUtils.sendResponse(exchange, 200, response);
        }catch(MovieNotFoundException e){
            String response = "{ \"error\": \"Movie not found\" }";
            ApiUtils.sendResponse(exchange, 404, response);
        }catch(DatabaseException e){
            String response = "{ \"error\": \"Internal Server Error\" }";
            ApiUtils.sendResponse(exchange, 500, response);
        } catch (JsonSyntaxException e) {
            String response = "{ \"error\": \"Malformed Json Syntax\" }";
            ApiUtils.sendResponse(exchange, 400, response);
        }catch (NullPointerException e) {
            String response = "{ \"error\": \"Movie Data Missing from request\" }";
            ApiUtils.sendResponse(exchange, 400, response);
        }catch(Exception e){
            String response = "{ \"error\": \"An Unexpected Error occurred\" }";
            ApiUtils.sendResponse(exchange, 500, response);
        }
    }

    private void handleBaseRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "GET" -> {
                String response = "{ \"message\": \"Movie Controller is working!\" }";
                ApiUtils.sendResponse(exchange, 200, response);
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }

    private void handleUpdateRequest(String method, HttpExchange exchange) throws IOException {
        switch (method) {
            case "PUT" -> {
                 try {
                     InputStream inputStream = exchange.getRequestBody();
                     String requestBody = new String(inputStream.readAllBytes());
                     Movie movie = gson.fromJson(requestBody, Movie.class);
                     String id = String.valueOf(movie.getId());
                     String title = movie.getTitle();
                     String genre = movie.getGenre();
                     int releaseYear = movie.getReleaseYear();
                     if (!requestBody.contains("\"id\": \"") || !requestBody.contains("\"genre\": \"") || !requestBody.contains("\"title\": \"") || !requestBody.contains("\"releaseYear\": ") ||
                             Objects.requireNonNull(id).isEmpty() || Objects.requireNonNull(title).isEmpty() || Objects.requireNonNull(genre).isEmpty() || releaseYear <= 0) {
                         String response = "{ \"error\": \"Invalid movie data\" }";
                         ApiUtils.sendResponse(exchange, 400, response);
                     } else {
                         Movie movieObj = new Movie(title, genre, releaseYear);
                         movieService.updateMovie(UUID.fromString(id), movieObj);

                         String response = "{ \"message\": \"Movie updated successfully\" }";
                         ApiUtils.sendResponse(exchange, 200, response);
                     }
                 } catch (MovieNotFoundException e) {
                     String response = "{ \"error\": \"Movie not found\" }";
                     ApiUtils.sendResponse(exchange, 404, response);
                 } catch (DatabaseException e) {
                     String response = "{ \"error\": \"Internal Server Error\" }";
                     ApiUtils.sendResponse(exchange, 500, response);
                 } catch (JsonSyntaxException e) {
                     String response = "{ \"error\": \"Malformed Json Syntax\" }";
                     ApiUtils.sendResponse(exchange, 400, response);
                 }catch (NullPointerException e) {
                     String response = "{ \"error\": \"Movie Data Missing from request\" }";
                     ApiUtils.sendResponse(exchange, 400, response);
                 }catch(Exception e){
                     String response = "{ \"error\": \"An Unexpected Error occurred\" }";
                     ApiUtils.sendResponse(exchange, 500, response);
                 }
            }
            default -> {
                String response = "{ \"error\": \"Method not allowed\" }";
                ApiUtils.sendResponse(exchange, 405, response);
            }
        }
    }
    private void handleSearchQueryRequest(String method, HttpExchange exchange) throws IOException {

        if (!method.equals("GET")) {
            String response = "{ \"error\": \"Method not allowed\" }";
            ApiUtils.sendResponse(exchange, 405, response);
            return;
        }

        // Get query string from URL
        String query = exchange.getRequestURI().getQuery();
        // Parse into Map
        Map<String, String> params = ApiUtils.parseQueryParams(query);

        String title = params.get("title");
        String genre = params.get("genre");
        String yearStr = params.get("releaseYear");
        try{
            String response = movieService.searchMovies(title,genre, yearStr);
            ApiUtils.sendResponse(exchange, 200, response);
        }catch(DatabaseException e){
            String response = "{ \"error\": \"Internal Server Error\" }";
            ApiUtils.sendResponse(exchange, 500, response);
        }catch (NullPointerException e) {
            String response = "{ \"error\": \"Movie Data Missing from request\" }";
            ApiUtils.sendResponse(exchange, 400, response);
        }catch(Exception e){
            String response = "{ \"error\": \"An Unexpected Error occurred\" }";
            ApiUtils.sendResponse(exchange, 500, response);
        }
    }
}
