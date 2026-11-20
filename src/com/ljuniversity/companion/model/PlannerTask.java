package com.ljuniversity.companion.model;

import java.time.LocalDate;
import java.util.UUID;

/** A personal offline task. Never sent to a campus server. */
public record PlannerTask(String id, String title, String notes, LocalDate due, String category, boolean done) {
    public PlannerTask {
        if (id == null || id.isBlank() || id.length() > 80) throw new IllegalArgumentException("Invalid task ID");
        title = title == null ? "" : title.strip();
        notes = notes == null ? "" : notes.strip();
        if (title.isBlank() || title.length() > 120) throw new IllegalArgumentException("Use a task title from 1 to 120 characters.");
        if (notes.length() > 2000) throw new IllegalArgumentException("Notes can contain up to 2000 characters.");
        if (due == null) throw new IllegalArgumentException("Choose a valid due date (YYYY-MM-DD).");
        if (!java.util.List.of("Academic", "Personal", "Campus").contains(category)) throw new IllegalArgumentException("Choose a task category.");
    }
    public static PlannerTask create(String title, String notes, String due, String category) {
        return new PlannerTask(UUID.randomUUID().toString(), title, notes, LocalDate.parse(due), category, false);
    }
    public PlannerTask completed(boolean value) { return new PlannerTask(id, title, notes, due, category, value); }
    public boolean overdue(LocalDate today) { return !done && due.isBefore(today); }
}
