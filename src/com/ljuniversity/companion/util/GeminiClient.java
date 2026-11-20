package com.ljuniversity.companion.util;

import com.google.gson.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;

/** Optional network client. Nothing is sent without an explicitly configured key. */
public class GeminiClient {
    public String sendMessage(String message) {
        String key = System.getenv("GEMINI_API_KEY");
        if (key == null || key.isBlank()) return "AI is not connected. Configure your own GEMINI_API_KEY to opt in. Do not send sensitive information.";
        if (message == null || message.isBlank() || message.length() > 8000) return "Enter a message between 1 and 8000 characters.";
        String model = System.getenv().getOrDefault("GEMINI_MODEL", "gemini-2.5-flash");
        if (!model.matches("[a-zA-Z0-9._-]+")) return "Invalid GEMINI_MODEL configuration.";
        JsonObject part = new JsonObject(); part.addProperty("text", message);
        JsonArray parts = new JsonArray(); parts.add(part);
        JsonObject content = new JsonObject(); content.add("parts", parts);
        JsonArray contents = new JsonArray(); contents.add(content);
        JsonObject body = new JsonObject(); body.add("contents", contents);
        try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent"))
                .timeout(Duration.ofSeconds(30)).header("Content-Type", "application/json").header("x-goog-api-key", key)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString())).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return "AI request was not accepted (HTTP " + response.statusCode() + "). Check your account, quota and model settings. No provider error details are logged.";
            JsonArray candidates = JsonParser.parseString(response.body()).getAsJsonObject().getAsJsonArray("candidates");
            if (candidates == null || candidates.isEmpty()) return "No text response was returned.";
            JsonArray output = candidates.get(0).getAsJsonObject().getAsJsonObject("content").getAsJsonArray("parts");
            StringBuilder result = new StringBuilder();
            for (JsonElement item : output) if (item.getAsJsonObject().has("text")) result.append(item.getAsJsonObject().get("text").getAsString());
            return result.isEmpty() ? "No text response was returned." : result.toString();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); return "AI request cancelled.";
        } catch (Exception e) { return "AI is unavailable. Check your network and configuration, then try again."; }
    }
}
