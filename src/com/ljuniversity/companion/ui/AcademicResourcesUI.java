package com.ljuniversity.companion.ui; // Added Package

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * AcademicResourcesUI.java
 * ------------------------
 * A simple, professional UI for accessing academic resources.
 * (UI Logic)
 */
public class AcademicResourcesUI extends JFrame {

    // Color palette
    private static final Color DARK_BLUE = new Color(25, 118, 210);
    private static final Color LIGHT_GRAY = new Color(240, 242, 245);
    private static final Color WHITE = Color.WHITE;

    public AcademicResourcesUI() {
        // 1. Frame Setup
        setTitle("Academic Resources");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());

        // 2. Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(DARK_BLUE);
        headerPanel.setPreferredSize(new Dimension(900, 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        JLabel headerLabel = new JLabel("Academic Resources");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headerLabel.setForeground(WHITE);
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // 3. Main Content Panel (for folders)
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(LIGHT_GRAY);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 30)); // Use FlowLayout to center the single item

        // 4. Create and Add Folder Buttons
        // Currently, there is only one link. More can be added in the future.
        mainPanel.add(createFolderButton("All Subjects Notes", "https://drive.google.com/drive/u/1/folders/1hT6Tg-RzTP1T_I0iMNc5F_1TL6m3BeZt"));


        add(mainPanel, BorderLayout.CENTER);

        // 5. Footer Panel with Back Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        footerPanel.setBackground(LIGHT_GRAY);

        JButton backButton = new JButton("Back to Home");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBackground(DARK_BLUE);
        backButton.setForeground(WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Action to go back to the homepage
        backButton.addActionListener(e -> {
            dispose(); // Close this window
            com.ljuniversity.companion.ui.HomePageSwing.show(); // Show the homepage
        });

        footerPanel.add(backButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Helper method to create a styled button that looks like a folder.
     */
    private JPanel createFolderButton(String subject, String url) {
        // Main container for the button and label
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(LIGHT_GRAY);

        // Simple button styled to look like a folder icon
        JButton button = new JButton("📁"); // Unicode folder icon
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        button.setBackground(WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(20, 30, 20, 30)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Label for the subject name
        JLabel label = new JLabel(subject, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(DARK_BLUE);

        // Action to open the URL in a browser
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(
                            AcademicResourcesUI.this,
                            "Could not open the link: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        panel.add(button, BorderLayout.CENTER);
        panel.add(label, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Main method to run the UI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AcademicResourcesUI().setVisible(true);
        });
    }
}