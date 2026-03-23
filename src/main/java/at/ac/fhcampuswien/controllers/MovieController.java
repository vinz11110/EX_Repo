package at.ac.fhcampuswien.controllers;

import at.ac.fhcampuswien.ApiUtils;
import at.ac.fhcampuswien.models.Movie;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MovieController implements HttpHandler {
    private final String BASE = "/api/movies";
    private List<Movie> movies = Movie.generateDummyMovies();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        //routing logic
        switch (path) {
            case BASE -> handleBaseRequest(method, exchange);
            case BASE + "/" -> handleBaseRequest(method, exchange);
            case BASE + "/delete" -> handleDeleteRequest(method, exchange);

            default -> {
                //if endpoint doesn't exist, return error
                String response = "{ \"error\": \"Path not found\"}";
                ApiUtils.sendResponse(exchange, 404, response);
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
            String response = "{ \"error\": \"Bad Request\" }";
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

            boolean removed = movies.removeIf(m ->
                    m.getTitle().equals(title) &&
                            m.getGenre().equals(genre) &&
                            m.getReleaseYear() == releaseYear);

            if (removed) {
                String response = "{ \"message\": \"Movie deleted successfully\" }";
                ApiUtils.sendResponse(exchange, 200, response);
            }   else {
                String response = "{ \"error\": \"Movie not found\" }";
                ApiUtils.sendResponse(exchange, 404, response);
            }
        }
        catch (Exception e) {
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

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);

        if (startIndex == -1) return null;

        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);

        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }

        String value = json.substring(startIndex, endIndex).trim();

        if (value.startsWith("\"") && value.endsWith("\""))  {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }
}
