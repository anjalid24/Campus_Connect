package com.ljuniversity.companion.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Event Manager Card Component - Reusable UI Component.
 * Extracted from EventsLeaderboardUI.java.
 */
public class EventManagerCard extends NeonPanel {
    private String managerName;
    private String description;
    private Color accentColor;
    private ActionListener clickListener;
    private String imagePath;

    public EventManagerCard(String managerName, String description, Color accentColor, ActionListener clickListener, String imagePath) {
        this.managerName = managerName;
        this.description = description;
        this.accentColor = accentColor;
        this.clickListener = clickListener;
        this.imagePath = imagePath;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Adjusted padding
        setGlowEnabled(true);
        setGlowColor(accentColor);
        setPreferredSize(new Dimension(300, 250)); // Increased preferred size to accommodate image
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        createComponents();
        addMouseListener();
    }

    private void createComponents() {
        // Image Label
        JLabel imageLabel = new JLabel();
        if (this.imagePath != null && !this.imagePath.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(this.imagePath);
                Image originalImage = originalIcon.getImage();
                // Scale image to fit within the card, e.g., 280x150 (adjust as needed)
                Image scaledImage = originalImage.getScaledInstance(280, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(scaledImage));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageLabel.setBorder(new EmptyBorder(0, 0, 10, 0)); // Padding below image
            } catch (Exception e) {
                System.err.println("Error loading image: " + this.imagePath + " - " + e.getMessage());
                imageLabel.setText("Image Error"); // Fallback text
                imageLabel.setForeground(Color.RED);
            }
        }
        add(imageLabel, BorderLayout.NORTH); // Add image to the top

        // Panel for name and description
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding for text

        // Manager name
        JLabel nameLabel = new JLabel(managerName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align text

        // Description
        JLabel descLabel = new JLabel("<html><div style='text-align: center;'>" + description + "</div></html>"); // HTML for center alignment and wrapping
        descLabel.setForeground(new Color(200, 200, 200));
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align text

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(5)); // Small gap
        textPanel.add(descLabel);

        add(textPanel, BorderLayout.CENTER); // Add text panel to the center
    }

    private void addMouseListener() {
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (clickListener != null) {
                    clickListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, managerName));
                }
            }
        });
    }
}