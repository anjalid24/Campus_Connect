package com.ljuniversity.companion.model;

/**
 * Data model for a single entry in the Career Guidance chart.
 * Extracted from CareerGuidance.java.
 */
public class Entry {
    public final String field;
    public final int percent;
    public final String tools;

    public Entry(String field, int percent, String tools) {
        this.field = field;
        this.percent = percent;
        this.tools = tools;
    }
}