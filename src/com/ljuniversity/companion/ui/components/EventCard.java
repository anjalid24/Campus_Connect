package com.ljuniversity.companion.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Event Card Component - Reusable UI Component.
 * Extracted from EventsLeaderboardUI.java.
 */
public class EventCard extends NeonPanel {
    private String eventName;
    private String description;
    private int points;
    private String date;
    private Color accentColor;

    public EventCard(String eventName, String description, int points, String date, Color accentColor) {
        this.eventName = eventName;
        this.description = description;
        this.points = points;
        this.date = date;
        this.accentColor = accentColor;

        setLayout(new BorderLayout(10, 10)); // Added hgap, vgap for better spacing
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setGlowEnabled(true);
        setGlowColor(accentColor);


        createComponents();
    }

    private void createComponents() {
        // Top panel for event name and points
        JPanel topInfoPanel = new JPanel(new BorderLayout());
        topInfoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(eventName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Increased font size
        nameLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        topInfoPanel.add(nameLabel, BorderLayout.WEST);

        JLabel pointsLabel = new JLabel("Points: " + points);
        pointsLabel.setForeground(accentColor);
        pointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Increased font size
        topInfoPanel.add(pointsLabel, BorderLayout.EAST);

        // Middle panel for truncated description
        JTextArea descPreviewArea = new JTextArea();
        descPreviewArea.setText(truncateDescription(description, 100)); // Truncate for preview
        descPreviewArea.setEditable(false);
        descPreviewArea.setWrapStyleWord(true);
        descPreviewArea.setLineWrap(true);
        descPreviewArea.setOpaque(false);
        descPreviewArea.setForeground(new Color(200, 200, 200));
        descPreviewArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descPreviewArea.setBorder(new EmptyBorder(0, 0, 10, 0)); // Added bottom padding

        // Bottom panel for date and description button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setOpaque(false);

        JLabel dateLabel = new JLabel(date);
        dateLabel.setForeground(new Color(180, 180, 180));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bottomPanel.add(dateLabel, BorderLayout.WEST);

        NeonButton descriptionBtn = new NeonButton("Description");
        descriptionBtn.setPreferredSize(new Dimension(100, 35));
        descriptionBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descriptionBtn.addActionListener(e -> showDescriptionDialog());
        bottomPanel.add(descriptionBtn, BorderLayout.EAST);

        add(topInfoPanel, BorderLayout.NORTH);
        add(descPreviewArea, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private String truncateDescription(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    private void showDescriptionDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), eventName + " Description", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setBackground(new Color(15, 20, 30));

        NeonPanel dialogPanel = new NeonPanel(15);
        dialogPanel.setLayout(new BorderLayout(15, 15));
        dialogPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        dialogPanel.setGlowEnabled(true);
        dialogPanel.setGlowColor(accentColor);

        JLabel titleLabel = new JLabel(eventName, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        dialogPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea fullDescriptionArea = new JTextArea(description);
        fullDescriptionArea.setEditable(false);
        fullDescriptionArea.setWrapStyleWord(true);
        fullDescriptionArea.setLineWrap(true);
        fullDescriptionArea.setBackground(new Color(25, 30, 40));
        fullDescriptionArea.setForeground(new Color(220, 220, 220));
        fullDescriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fullDescriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(fullDescriptionArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(accentColor.darker(), 1));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dialogPanel.add(scrollPane, BorderLayout.CENTER);

        NeonButton closeButton = new NeonButton("Close");
        closeButton.setPreferredSize(new Dimension(80, 35));
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        buttonPanel.add(closeButton);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(dialogPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}