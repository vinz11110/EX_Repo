package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.models.Movie;
import com.google.gson.Gson;
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
               String response = movieService.getAllMovies();
               ApiUtils.sendResponse(exchange, 200, response);

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
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Movie movie = gson.fromJson(requestBody, Movie.class);
               try{
                   movieService.addMovie(movie);

                   String response = "{ \"message\": \"Movie added successfully\" }";
                   ApiUtils.sendResponse(exchange, 201, response);
               }catch (IllegalStateException e){
                   String response = "{ \"error\": \"Movie already exists\"}";
                   ApiUtils.sendResponse(exchange, 400, response);
               }catch (IllegalArgumentException e){
                   String response = "{ \"error\": \"Invalid movie Data\"}";
                   ApiUtils.sendResponse(exchange, 400, response);
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
            String title = extractJsonValue(requestBody, "title");
            String genre = extractJsonValue(requestBody, "genre");
            String releaseYearString = extractJsonValue(requestBody, "releaseYear");

            if (title == null || genre == null || releaseYearString == null) {
                String response = "{ \"error\": \"Invalid movie data\" }";
                ApiUtils.sendResponse(exchange, 400, response);
                return;
            }

            int releaseYear = Integer.parseInt(releaseYearString);
            try{
                movieService.deleteMovie(title, genre, releaseYear);

                String response = "{ \"message\": \"Movie deleted successfully\" }";
                ApiUtils.sendResponse(exchange, 200, response);
            }catch(NoSuchElementException e){
                String response = "{ \"error\": \"Movie not found\" }";
                ApiUtils.sendResponse(exchange, 404, response);
            }

        } catch (Exception e) {
            String response = "{ \"error\": \"Invalid movie data\" }";
            ApiUtils.sendResponse(exchange, 400, response);
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
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes());
        String id = extractJsonValue(requestBody, "id");
        String title = extractJsonValue(requestBody, "title");
        String genre = extractJsonValue(requestBody, "genre");
        String releaseYear2 = extractJsonValue(requestBody, "releaseYear");
        assert releaseYear2 != null;
        int releaseYear = Integer.parseInt(releaseYear2);

        switch (method) {
            case "PUT" -> {
                if (!requestBody.contains("\"id\": \"") || !requestBody.contains("\"genre\": \"") || !requestBody.contains("\"title\": \"") || !requestBody.contains("\"releaseYear\": ") ||
                        Objects.requireNonNull(id).isEmpty() || Objects.requireNonNull(title).isEmpty() || Objects.requireNonNull(genre).isEmpty() || releaseYear <= 0) {
                    String response = "{ \"error\": \"Invalid movie data\" }";
                    ApiUtils.sendResponse(exchange, 400, response);
                } else {
                    Movie movie = new Movie(title,genre,releaseYear);
                        if (movieService.updateMovie(UUID.fromString(id), movie)) {
                            String response = "{ \"message\": \"Movie updated successfully\" }";
                            ApiUtils.sendResponse(exchange, 200, response);
                            return;
                        }
                    String response = "{ \"error\": \"Movie not found\" }";
                    ApiUtils.sendResponse(exchange, 404, response);
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

        final Integer releaseYear = Integer.parseInt(yearStr);
        String response = movieService.searchMovies(title,genre, yearStr);
        ApiUtils.sendResponse(exchange, 200, response);
    }

//    private Movie parseMovie(String jsonFile) {
//        try {
//            String title = extractJsonValue(jsonFile, "title");
//            String genre = extractJsonValue(jsonFile, "genre");
//            String releaseYearStr = extractJsonValue(jsonFile, "releaseYear");
//            int releaseYear = 0;
//            if (releaseYearStr != null && !releaseYearStr.isEmpty()) {
//                releaseYear = Integer.parseInt(releaseYearStr);
//            }
//
//            return new Movie(title, genre, releaseYear);
//        } catch (Exception e) {
//            return null;
//        }
//    }

    private String extractJsonValue(String json, String key) {
        Movie movie = gson.fromJson(json, Movie.class);
        String value = null;
        switch (key) {
            case "id" -> {
                value = String.valueOf(movie.getId());
            }
            case "title" -> {
                value = movie.getTitle();
            }
            case "genre" -> {
                value = movie.getGenre();
            }
            case "releaseYear" -> {
                value = String.valueOf(movie.getReleaseYear());
            }
        }
        return value;
//        String searchKey = "\"" + key + "\":";
//        int startIndex = json.indexOf(searchKey);
//
//        if (startIndex == -1) return null;
//
//        startIndex += searchKey.length();
//        int endIndex = json.indexOf(",", startIndex);
//
//        if (endIndex == -1) {
//            endIndex = json.indexOf("}", startIndex);
//        }
//
//        String value = json.substring(startIndex, endIndex).trim();
//
//        if (value.startsWith("\"") && value.endsWith("\""))  {
//            value = value.substring(1, value.length() - 1);
//        }
//        return value;
    }
}
