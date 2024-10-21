package com.example.rider;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class PlaceSearch {
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public String searchPlace(String query) throws Exception {
        String requestUrl = NOMINATIM_BASE_URL + query.replace(" ", "+");
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
