package com.ljuniversity.companion.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Leaderboard Row Component - Reusable UI Component.
 * Extracted from EventsLeaderboardUI.java.
 */
public class LeaderboardRow extends NeonPanel {
    private int rank;
    private String name;
    private String enrollmentNo;
    private int totalPoints;

    public LeaderboardRow(int rank, String name, String enrollmentNo, int totalPoints) {
        this.rank = rank;
        this.name = name;
        this.enrollmentNo = enrollmentNo;
        this.totalPoints = totalPoints;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 20, 15, 20));
        setGlowEnabled(true);
        setGlowColor(getRankColor(rank));
        setPreferredSize(new Dimension(800, 60));

        createComponents();
    }

    private Color getRankColor(int rank) {
        switch (rank) {
            case 1: return new Color(255, 215, 0); // Gold
            case 2: return new Color(192, 192, 192); // Silver
            case 3: return new Color(205, 127, 50); // Bronze
            default: return new Color(255, 50, 150); // Neon pink
        }
    }

    private void createComponents() {
        // Rank
        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setForeground(getRankColor(rank));
        rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rankLabel.setPreferredSize(new Dimension(60, 0));

        // Name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Enrollment
        JLabel enrollmentLabel = new JLabel(enrollmentNo);
        enrollmentLabel.setForeground(new Color(200, 200, 200));
        enrollmentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Points
        JLabel pointsLabel = new JLabel(String.valueOf(totalPoints));
        pointsLabel.setForeground(new Color(50, 255, 255));
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Layout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(rankLabel, BorderLayout.WEST);
        leftPanel.add(nameLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(enrollmentLabel, BorderLayout.WEST);
        rightPanel.add(pointsLabel, BorderLayout.EAST);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
}