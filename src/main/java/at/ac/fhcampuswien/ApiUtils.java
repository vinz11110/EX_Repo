package at.ac.fhcampuswien;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ApiUtils {
    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
    public static Map<String, String> parseQueryParams(String query){
        Map<String, String> params = new HashMap<>();

        if (query == null || query.isEmpty()){
            return params;
        }

        String[] pairs = query.split("&");

        for (String pair:pairs){
            String[] keyValue = pair.split("=");

            if (keyValue.length == 2){
                String key = keyValue[0];
                String value = keyValue[1];
                params.put(key, value);
            }
        }
        return params;
    }


}
