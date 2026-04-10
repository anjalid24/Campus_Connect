package com.ljuniversity.companion.model;

/**
 * Record for a chat message, used by the Gemini Client.
 * Extracted from gemAI.java / CareerGuidance.java.
 */
public record Message(String role, String content) {
    @Override
    public String toString() {
        return (role.equals("user") ? "You: " : "Gemini: ") + content;
    }
}