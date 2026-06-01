package at.ac.fhcampuswien.controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public abstract class IController implements HttpHandler {

    public void handle(HttpExchange exchange) throws IOException{

    }

    void handleGetAllRequest(String method, HttpExchange exchange) throws IOException{

    }

     void handlePostRequest(String method, HttpExchange exchange) throws IOException{

    }

     void handleDeleteRequest(String method, HttpExchange exchange) throws IOException{

    }

     void handleBaseRequest(String method, HttpExchange exchange) throws IOException{

    }

     void handleUpdateRequest(String method, HttpExchange exchange) throws IOException{

    }
}
