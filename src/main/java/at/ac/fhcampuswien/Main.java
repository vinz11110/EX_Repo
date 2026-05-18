package at.ac.fhcampuswien;

import at.ac.fhcampuswien.controllers.HelloController;
import at.ac.fhcampuswien.controllers.MovieController;
import at.ac.fhcampuswien.models.Movie;
import at.ac.fhcampuswien.utils.DatabaseUtil;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Main {
    private final static int SERVER_PORT = 8080;

    public static void main(String[] args) throws IOException {
        // Create an HTTP server listening on defined port
        HttpServer server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);

        // Register controllers and their handlers - REST endpoints
        registerController(server, "/api/movies/", new MovieController());

        // Start the server
        server.setExecutor(null);
        server.start();
        System.out.printf("Server is running on http://localhost:%d", SERVER_PORT);
        try (Connection conn = DatabaseUtil.getConnection()){
            String createTableSQL = "CREATE TABLE IF NOT EXISTS movies (" +
                                    "id UUID PRIMARY KEY," +
                                    "title VARCHAR(255) NOT NULL," +
                                    "genre VARCHAR(100) NOT NULL," +
                                    "releaseYear INT NOT NULL" +
                                    ")";
            try (PreparedStatement pstmt = conn.prepareStatement(createTableSQL)){
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void registerController(HttpServer server, String path, HttpHandler handler) {
        HttpContext context = server.createContext(path, handler);
        // Optionally add more configurations to context if needed
    }
}