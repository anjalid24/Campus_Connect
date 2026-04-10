package com.ljuniversity.companion.util;

import com.ljuniversity.companion.model.Message;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Client for interacting with the Gemini API.
 * Extracted from gemAI.java and CareerGuidance.java.
 */
public class GeminiClient {

    // Use the API key from CareerGuidance for consistency, but note it's incomplete
    private static final String API_KEY = "AIzaSyBpQAbNmd1xebiej4vQxwh1KlHegUyHFtI";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    public String sendMessage(String userMessage) {
        try {
            // Simplified JSON payload using the structure from the source files
            String jsonInput = "{\"contents\":[{\"parts\":[{\"text\":\"" + escapeJson(userMessage) + "\"}]}]}";

            HttpURLConnection conn = (HttpURLConnection) new URI(API_URL).toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
            }

            InputStream is = conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
            String jsonResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray candidates = jsonObject.getAsJsonArray("candidates");

            if (candidates != null && candidates.size() > 0) {
                JsonArray parts = candidates.get(0)
                        .getAsJsonObject().getAsJsonObject("content")
                        .getAsJsonArray("parts");
                return parts.get(0).getAsJsonObject().get("text").getAsString();
            }
            return "No response from Gemini.";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}