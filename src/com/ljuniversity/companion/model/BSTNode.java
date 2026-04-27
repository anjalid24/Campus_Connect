package com.ljuniversity.companion.model;

/**
 * Node structure for the Binary Search Tree (BST) used in the Leaderboard.
 * Extracted from EventsLeaderboardUI.java.
 */
public class BSTNode {
    public String name;
    public String enrollmentNo;
    public int totalPoints;
    public BSTNode left, right;

    public BSTNode(String name, String enrollmentNo, int totalPoints) {
        this.name = name;
        this.enrollmentNo = enrollmentNo;
        this.totalPoints = totalPoints;
        left = right = null;
    }
}