package com.ljuniversity.companion.util;

import com.ljuniversity.companion.model.BSTNode;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;

/**
 * Binary Search Tree implementation for managing and sorting Leaderboard data.
 * Extracted from EventsLeaderboardUI.java.
 */
public class BST {
    private BSTNode root;

    public void insert(String name, String enrollmentNo, int totalPoints) {
        root = insertRec(root, name, enrollmentNo, totalPoints);
    }

    private BSTNode insertRec(BSTNode root, String name, String enrollmentNo, int totalPoints) {
        if (root == null) return new BSTNode(name, enrollmentNo, totalPoints);
        // Sort by points: higher points go to the right (for reverse in-order traversal)
        if (totalPoints > root.totalPoints) root.right = insertRec(root.right, name, enrollmentNo, totalPoints);
        else root.left = insertRec(root.left, name, enrollmentNo, totalPoints);
        return root;
    }

    // Method used by the console application (LeaderboardSystem.java, if uncommented)
    public void printLeaderboard() {
        System.out.println("\n===== Leaderboard (Top Performers) =====\n");
        printReverseInOrder(root);
    }

    private void printReverseInOrder(BSTNode root) {
        if (root != null) {
            printReverseInOrder(root.right);
            System.out.printf("%s (%s) - %d points\n", root.name, root.enrollmentNo, root.totalPoints);
            printReverseInOrder(root.left);
        }
    }

    // Method to get sorted data as a list for the UI
    public List<BSTNode> getSortedData() {
        List<BSTNode> sortedList = new ArrayList<>();
        reverseInOrderToList(root, sortedList);
        return sortedList;
    }

    private void reverseInOrderToList(BSTNode root, List<BSTNode> list) {
        if (root != null) {
            reverseInOrderToList(root.right, list);
            list.add(root);
            reverseInOrderToList(root.left, list);
        }
    }
}