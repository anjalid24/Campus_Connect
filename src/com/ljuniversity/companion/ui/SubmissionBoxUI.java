package com.ljuniversity.companion.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * SubmissionBoxUI - Modern, creative, and easy-on-the-eyes UI for anonymous submissions.
 * (UI Logic)
 */
public class SubmissionBoxUI extends JFrame {
    // JDBC connection variables (same as terminal code)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/campus-connect";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // UI components
    private JComboBox<String> typeCombo;
    private JTextArea descriptionArea;
    private JButton submitBtn, backBtn;
    private JLabel statusLabel;

    public SubmissionBoxUI() {
        // Frame setup: full screen, no window bar, dark background
        setTitle("🕊️ Speak Freely, Stay Anonymous");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(28, 32, 38)); // dark blue-gray

        // ====== HEADING BAR WITH LOGO (like homepage) ======
        JPanel headingPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(36, 123, 255), getWidth(), 0, new Color(0, 212, 255)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
                g2.dispose();
            }
        };
        headingPanel.setPreferredSize(new Dimension(0, 80));
        headingPanel.setOpaque(false);
        headingPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel headingLabel = new JLabel("Submission Box");
        headingLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        headingLabel.setForeground(Color.WHITE);
        headingLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Logo loading using classpath
        ImageIcon scaledLogoIcon = null;
        try {
            ImageIcon originalLogoIcon = new ImageIcon(getClass().getResource("/src/resources/img.png"));
            Image img = originalLogoIcon.getImage();
            Image newImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            scaledLogoIcon = new ImageIcon(newImg);
        } catch (Exception e) {
            System.err.println("Submission Box: Could not load /img.png from classpath.");

        }

        JLabel logo = new JLabel(scaledLogoIcon);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(60, 60));
        logo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        headingPanel.add(headingLabel, BorderLayout.WEST);
        headingPanel.add(logo, BorderLayout.EAST);
        add(headingPanel, BorderLayout.NORTH);

        // ====== MAIN CONTENT (centered) ======
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // Main panel with soft gradient and rounded corners
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(36, 123, 255, 180), getWidth(), getHeight(), new Color(0, 212, 255, 180)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 36, 36);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(540, 540));

        // Header inside the box
        JLabel header = new JLabel(" Speak Freely, Stay Anonymous", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(new Color(220, 240, 255));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(header);
        mainPanel.add(Box.createVerticalStrut(10));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(400, 2));
        mainPanel.add(sep);
        mainPanel.add(Box.createVerticalStrut(20));

        // Type dropdown
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setOpaque(false);
        JLabel typeLabel = new JLabel(" Type: ");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        typeLabel.setForeground(new Color(200, 220, 255));
        typeCombo = new JComboBox<>(new String[]{"Complaint", "Confession", "Suggestion"});
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        typeCombo.setBackground(new Color(36, 123, 255));
        typeCombo.setForeground(Color.WHITE);
        typePanel.add(typeLabel);
        typePanel.add(typeCombo);
        mainPanel.add(typePanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Description area
        JLabel descLabel = new JLabel(" Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        descLabel.setForeground(new Color(200, 220, 255));
        mainPanel.add(descLabel);
        descriptionArea = new JTextArea(8, 36);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(new Color(38, 44, 54));
        descriptionArea.setForeground(new Color(220, 240, 255));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(0, 212, 255), 2));
        mainPanel.add(descScroll);
        mainPanel.add(Box.createVerticalStrut(20));

        // Status label for feedback
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(0, 212, 255));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Buttons panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        btnPanel.setOpaque(false);
        submitBtn = new JButton("Submit ");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        submitBtn.setBackground(new Color(0, 212, 255));
        submitBtn.setForeground(new Color(28, 32, 38));
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        submitBtn.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
        btnPanel.add(submitBtn);

        backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        backBtn.setBackground(new Color(38, 44, 54));
        backBtn.setForeground(new Color(0, 212, 255));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 30));
        btnPanel.add(backBtn);

        mainPanel.add(btnPanel);

        // Center the main panel in the frame
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(mainPanel, gbc);
        add(centerPanel, BorderLayout.CENTER);

        // Submit button action: insert into DB
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });
        // Back button action: return to home page
        backBtn.addActionListener(e -> {
            dispose();
            HomePageSwing.show();
        });
    }

    /**
     * Handles the submission: validates input, inserts into complainbox, and shows feedback.
     */
    private void handleSubmit() {
        String type = (String) typeCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();

        if (description.isEmpty()) {
            statusLabel.setText("Please enter a complaint.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            statusLabel.setText("Error: MySQL Driver not found. Check setup.");
            statusLabel.setForeground(Color.RED);
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Insert only into complainbox (trigger will set status = 'Pending')
            String query = "INSERT INTO complainbox (complain) VALUES (?)";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, description);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    statusLabel.setText("Submission received! Thank you for your feedback.");
                    statusLabel.setForeground(new Color(0, 220, 120));
                    descriptionArea.setText("");
                } else {
                    statusLabel.setText("Failed to submit complaint.");
                    statusLabel.setForeground(Color.RED);
                }
            }
        } catch (SQLException e) {
            statusLabel.setText("Error: Database operation failed. " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        } catch (Exception e) {
            statusLabel.setText("Unknown Error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }


    /**
     * Opens the Submission Box UI in full screen mode.
     */
    public static void openSubmissionBox() {
        SwingUtilities.invokeLater(() -> {
            SubmissionBoxUI ui = new SubmissionBoxUI();
            ui.setVisible(true);
        });
    }

    public static void main(String[] args) {
        openSubmissionBox();
    }

    // Close any resources when window is disposed
    @Override
    public void dispose() {
        super.dispose();
    }
}