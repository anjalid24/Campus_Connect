package com.ljuniversity.companion.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data class for a single task. Extracted from TaskManagerUI.java.
 */
public class Task {
    public int id;
    public String title, description, deadline, status;

    public Task(ResultSet rs) throws SQLException {
        id = rs.getInt("id");
        title = rs.getString("title");
        description = rs.getString("description");
        deadline = rs.getString("deadline");
        status = rs.getString("status");
    }

    @Override
    public String toString() {
        return title + " (" + status + ")";
    }
}