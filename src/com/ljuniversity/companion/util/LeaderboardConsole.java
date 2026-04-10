package com.ljuniversity.companion.util;


import com.ljuniversity.companion.model.BSTNode;
import java.sql.*;
import java.util.*;


public class LeaderboardConsole {

    static final String DB_URL = "jdbc:mysql://localhost:3306/events";
    static final String USER = "root";
    static final String PASS = ""; // Update if you have a password

    public static void main(String[] args) {
        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        BST leaderboard = new BST(); // Uses imported BST utility

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // NOTE: This DB URL 'events' is different from the UI's 'campus-connect'.
            con = DriverManager.getConnection(DB_URL, USER, PASS);

            // Using subquery instead of JOIN to fetch leaderboard data
            String query = "SELECT name, enrollment_no, " +
                    "(SELECT SUM(points_earned) FROM participants p WHERE p.enrollment_no = u.enrollment_no) AS total_points " +
                    "FROM users u WHERE EXISTS (SELECT 1 FROM participants p WHERE p.enrollment_no = u.enrollment_no)";

            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            // Insert each student into BST with their total points
            while (rs.next()) {
                String name = rs.getString("name");
                String enrollment = rs.getString("enrollment_no");
                int points = rs.getInt("total_points");

                leaderboard.insert(name, enrollment, points);
            }

            // Print sorted leaderboard using BST (highest points first)
            leaderboard.printLeaderboard();

        } catch (Exception e) {
            System.out.println("Error: Cannot connect to database. Ensure MySQL is running and the 'events' database is set up.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception ignored) {}
            try {
                if (pst != null) pst.close();
            } catch (Exception ignored) {}
            try {
                if (con != null) con.close();
            } catch (Exception ignored) {}
        }
    }
}