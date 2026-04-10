package com.ljuniversity.companion.util; // Added Package

import com.ljuniversity.companion.model.Message; // New Import
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.*;

class ChatbotConsole { // Renamed for clarity

    // ======================== API CONFIG ========================
    // Note: API key is different from CareerGuidance, using this one for console
    private static final String API_KEY = "AIzaSyC7l3wysjjSzsuSAHnUGyjfX3mEv91Zy8U";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;

    // ❌ REMOVED: Message record (Now imported from com.ljuniversity.companion.model.Message)

    static class ChatHistory {
        private final List<Message> messages = new ArrayList<>();

        public void add(String role, String content) {
            messages.add(new Message(role, content));
        }

        public void print() {
            System.out.println("\n--- Chat History ---");
            for (Message m : messages) {
                System.out.println(m);
            }
            System.out.println("--------------------\n");
        }
    }

    // ❌ REMOVED: GeminiClient class (Now imported from com.ljuniversity.companion.util.GeminiClient)
    // NOTE: This class will use the imported GeminiClient. The original logic here was very similar but duplicated.

    // ======================== MAIN METHOD ========================
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GeminiClient client = new GeminiClient();
        ChatHistory history = new ChatHistory();

        System.out.println("Gemini Chatbot (type 'exit' to quit, 'history' to view log)");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            } else if (userInput.equalsIgnoreCase("history")) {
                history.print();
                continue;
            }

            history.add("user", userInput);
            String reply = client.sendMessage(userInput);
            history.add("assistant", reply);

            System.out.println("Gemini: " + reply);
        }

        scanner.close();
    }
}