package com.tarimtoptan.android.net;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {
    public interface Callback {
        void onSuccess(String body);
        void onError(String message);
    }

    // Canli Render API adresi. Yerel backend ile test icin "http://10.0.2.2:3000/v1" kullanilabilir.
    public static final String API_BASE_URL = "https://tarimtoptan-api.onrender.com/v1";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void get(String path, String token, Callback callback) {
        request("GET", path, null, token, callback);
    }

    public void post(String path, JSONObject body, String token, Callback callback) {
        request("POST", path, body, token, callback);
    }

    public void put(String path, JSONObject body, String token, Callback callback) {
        request("PUT", path, body, token, callback);
    }

    public void patch(String path, JSONObject body, String token, Callback callback) {
        request("PATCH", path, body, token, callback);
    }

    public void delete(String path, String token, Callback callback) {
        request("DELETE", path, null, token, callback);
    }

    private void request(String method, String path, JSONObject body, String token, Callback callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(API_BASE_URL + path);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(30000);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                if (token != null && !token.trim().isEmpty()) {
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }

                if (body != null) {
                    connection.setDoOutput(true);
                    byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
                    try (OutputStream stream = connection.getOutputStream()) {
                        stream.write(bytes);
                    }
                }

                int status = connection.getResponseCode();
                InputStream stream = status >= 200 && status < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();
                String responseBody = readBody(stream);

                if (status >= 200 && status < 300) {
                    String finalBody = responseBody == null || responseBody.isEmpty()
                            ? "{\"ok\":true,\"status\":" + status + "}"
                            : responseBody;
                    mainHandler.post(() -> callback.onSuccess(finalBody));
                } else {
                    mainHandler.post(() -> callback.onError("HTTP " + status + " - " + responseBody));
                }
            } catch (Exception exception) {
                mainHandler.post(() -> callback.onError(exception.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private String readBody(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        }
        return builder.toString();
    }
}
