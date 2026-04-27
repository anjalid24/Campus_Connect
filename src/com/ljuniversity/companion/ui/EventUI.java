package com.ljuniversity.companion.ui; // Added Package

// UI COMPONENTS (Moved to components package)
import com.ljuniversity.companion.ui.components.NeonGradientPanel;
import com.ljuniversity.companion.ui.components.NeonPanel;
import com.ljuniversity.companion.ui.components.NeonButton;
import com.ljuniversity.companion.ui.components.EventManagerCard;
import com.ljuniversity.companion.ui.components.EventCard;
import com.ljuniversity.companion.ui.components.LeaderboardRow;


// DATA STRUCTURES (Moved to model/util packages)
import com.ljuniversity.companion.model.BSTNode;
import com.ljuniversity.companion.util.BST;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;
import java.sql.*;


class EventsLeaderboardUI {
    static final String DB_URL = "jdbc:mysql://localhost:3306/campus-connect";
    static final String USER = "root";
    static final String PASS = "";

    private JPanel eventsContentPanel;
    private JPanel leaderboardContentPanel;
    private CardLayout cardLayout;
    private String currentView = "MANAGERS"; // Track current view: MANAGERS, EVENTS, LEADERBOARD
    private String currentManager = ""; // Track which manager is selected
    private NeonButton headerBackButton; // Reference to header back button

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventsLeaderboardUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        applyBlueTheme();
        JFrame frame = new JFrame("Gaming Leaderboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 900);
        frame.setLayout(new BorderLayout());

        // Set futuristic neon gradient background
        frame.setContentPane(new NeonGradientPanel());

        // Create main layout
        JPanel mainLayout = new JPanel(new BorderLayout());
        mainLayout.setOpaque(false);

        // Create header, content, footer
        JPanel headerPanel = createHeaderPanel();
        JPanel contentPanel = createContentPanel();
        JPanel footerPanel = createFooterPanel();

        mainLayout.add(headerPanel, BorderLayout.NORTH);
        mainLayout.add(contentPanel, BorderLayout.CENTER); // content fills center
        mainLayout.add(footerPanel, BorderLayout.SOUTH);   // footer at bottom

        frame.add(mainLayout, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void applyBlueTheme() {
        // UIManager tweaks for blue-black-white scheme
        UIManager.put("ToolTip.background", new Color(20, 24, 35));
        UIManager.put("ToolTip.foreground", Color.WHITE);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Table.background", new Color(15, 20, 30));
        UIManager.put("Table.foreground", Color.WHITE);
        UIManager.put("Table.selectionBackground", new Color(50, 120, 255));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", new Color(40, 50, 70));
        UIManager.put("TableHeader.background", new Color(20, 28, 45));
        UIManager.put("TableHeader.foreground", Color.WHITE);
        UIManager.put("ScrollBar.thumb", new Color(50, 120, 255));
        UIManager.put("ScrollBar.track", new Color(15, 20, 30));
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(true);
        headerPanel.setBackground(new Color(10, 12, 18)); // solid full-width bar
        headerPanel.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Back to Home button
        headerBackButton = new NeonButton("< Back");
        headerBackButton.setPreferredSize(new Dimension(96, 28));
        headerBackButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerBackButton.setToolTipText("Go back to Home");
        headerBackButton.addActionListener(e -> handleBackNavigation());

        // Centered navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        navPanel.setOpaque(false);
        NeonButton eventsTab = new NeonButton("Events");
        eventsTab.setPreferredSize(new Dimension(120, 32));
        eventsTab.setFont(new Font("Segoe UI", Font.BOLD, 13));
        eventsTab.setToolTipText("View Events");
        eventsTab.addActionListener(e -> showEventsView());
        NeonButton leaderboardTab = new NeonButton("Leaderboard");
        leaderboardTab.setPreferredSize(new Dimension(120, 32));
        leaderboardTab.setFont(new Font("Segoe UI", Font.BOLD, 13));
        leaderboardTab.setToolTipText("View Leaderboard");
        leaderboardTab.addActionListener(e -> showLeaderboardView());
        navPanel.add(eventsTab);
        navPanel.add(leaderboardTab);

        headerPanel.add(headerBackButton, BorderLayout.WEST);
        headerPanel.add(navPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private void handleBackNavigation() {
        if (currentView.equals("EVENTS")) {
            // If viewing events, go back to managers
            showEventManagersView();
        } else {
            // If on managers or leaderboard, go to home
            Component comp = headerBackButton;
            while (comp != null && !(comp instanceof JFrame)) {
                comp = comp.getParent();
            }
            if (comp != null) {
                ((JFrame) comp).dispose();
            }
            com.ljuniversity.companion.ui.HomePageSwing.show();
        }
    }

    private JPanel createTabPanel() {
        NeonPanel tabPanel = new NeonPanel(0);
        tabPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 15));
        tabPanel.setBackground(new Color(20, 20, 35, 200));
        tabPanel.setBorder(new EmptyBorder(10, 30, 10, 30));

        // Events tab
        NeonButton eventsTab = new NeonButton("📅 Events");
        eventsTab.setPreferredSize(new Dimension(120, 40));
        eventsTab.setToolTipText("View Events");
        eventsTab.addActionListener(e -> showEventsView());

        // Leaderboard tab
        NeonButton leaderboardTab = new NeonButton("🏆 Leaderboard");
        leaderboardTab.setPreferredSize(new Dimension(150, 40));
        leaderboardTab.setToolTipText("View Leaderboard");
        leaderboardTab.addActionListener(e -> showLeaderboardView());

        tabPanel.add(eventsTab);
        tabPanel.add(leaderboardTab);

        return tabPanel;
    }

    private JPanel createContentPanel() {
        cardLayout = new CardLayout();
        JPanel contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0)); // Removed padding

        // Create content panels
        eventsContentPanel = createEventsContentPanel();
        leaderboardContentPanel = createLeaderboardContentPanel();

        contentPanel.add(eventsContentPanel, "EVENTS");
        contentPanel.add(leaderboardContentPanel, "LEADERBOARD");

        // Show events by default
        cardLayout.show(contentPanel, "EVENTS");

        return contentPanel;
    }

    private JPanel createEventsContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Create event managers view (first view)
        JPanel eventManagersPanel = createEventManagersView();
        mainPanel.add(eventManagersPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createEventManagersView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 0)); // Removed padding from this panel

        // Title (text only, no emoji)
        JLabel titleLabel = new JLabel("Select Event Manager to Browse Events", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0)); // Reduced bottom padding

        // Event managers grid
        JPanel managersGrid = new JPanel(new GridLayout(0, 3, 30, 30));
        managersGrid.setOpaque(false);

        // Binary Brains
        EventManagerCard binaryBrainsCard = new EventManagerCard(
                "Binary Brains",
                "Technical and coding events",
                new Color(60, 140, 255),
                e -> showEventManagerEvents("Binary Brains"),
                "src/resources/binarybrainsimg.jpeg" // Added image path
        );

        // LFA
        EventManagerCard lfaCard = new EventManagerCard(
                "LFA",
                "Innovation and technology events",
                new Color(120, 200, 255),
                e -> showEventManagerEvents("LFA"),
                "src/resources/lfa.jpeg" // Added image path
        );

        // LJSC
        EventManagerCard ljscCard = new EventManagerCard(
                "LJSC Events",
                "Cultural and sports events",
                new Color(100, 160, 255),
                e -> showEventManagerEvents("LJSC"),
                "src/resources/ljsc.jpeg" // Added image path
        );

        managersGrid.add(binaryBrainsCard);
        managersGrid.add(lfaCard);
        managersGrid.add(ljscCard);

        JScrollPane scrollPane = new JScrollPane(managersGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showEventManagerEvents(String managerName) {
        currentView = "EVENTS";
        currentManager = managerName;
        headerBackButton.setToolTipText("Go back to Event Managers");
        eventsContentPanel.removeAll();

        JPanel eventsPanel = new JPanel(new BorderLayout());
        eventsPanel.setOpaque(false);
        eventsPanel.setBorder(new EmptyBorder(0, 0, 0, 0)); // Removed padding from this panel

        // Title
        JLabel titleLabel = new JLabel(managerName + " Events", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 0, 30, 0));

        // Events grid (responsive columns)
        JPanel eventsGrid = new JPanel();
        eventsGrid.setLayout(new BoxLayout(eventsGrid, BoxLayout.Y_AXIS));
        eventsGrid.setOpaque(false);

        Color accentColor = getManagerColor(managerName);

        // Add events based on manager
        if (managerName.equals("Binary Brains")) {
            eventsGrid.add(new EventCard("Treasure Hunt", "Solve puzzles and find hidden treasures in a thrilling adventure across the campus. Team up with friends and follow clues to uncover the grand prize!", 100, "2024-01-15", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Active Coding", "Participate in a fast-paced coding competition to test your algorithms and problem-solving skills against other talented coders. Prizes for top performers!", 150, "2024-01-20", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Battle of Ground VO", "Showcase your vocal talents in a dynamic voice-over battle. Impress the judges with your character impressions and storytelling abilities.", 80, "2024-01-25", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
        } else if (managerName.equals("LFA")) {
            eventsGrid.add(new EventCard("Hackathon", "A 24-hour intense coding challenge where teams develop innovative solutions to real-world problems. Bring your ideas to life!", 200, "2024-02-01", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Scavenger Hunt", "Embark on a digital scavenger hunt, decoding riddles and finding virtual clues to reach the final destination. Fun for all ages!", 120, "2024-02-05", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Scramble Words", "A challenging word puzzle competition where you unscramble letters to form words. Sharpen your vocabulary and quick thinking!", 90, "2024-02-10", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
        } else if (managerName.equals("LJSC")) {
            eventsGrid.add(new EventCard("Lumina Dance", "A vibrant dance performance competition showcasing various styles and choreographies. Express yourself through movement and rhythm!", 130, "2024-02-15", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Lumina Music", "A musical talent showcase featuring a diverse range of genres and instruments. Share your passion for music with the audience!", 140, "2024-02-20", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Lumina Drama", "A dramatic arts competition where participants perform short plays and skits. Bring characters to life with your acting prowess!", 110, "2024-02-25", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Mr & Ms LJ", "A prestigious beauty and talent pageant, celebrating charisma, intellect, and grace. Compete for the coveted titles!", 160, "2024-03-01", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Carpedium - Cricket", "A thrilling cricket tournament for all enthusiasts. Form your teams and compete for the championship trophy!", 180, "2024-03-05", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Carpedium - Football", "An exciting football championship for passionate players. Showcase your skills and teamwork on the field!", 170, "2024-03-10", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            // Add more events to force scrolling
            eventsGrid.add(new EventCard("Basketball Blitz", "Fast-paced basketball tournament. Shoot hoops and dominate the court!", 120, "2024-03-15", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Volleyball Royale", "Spike, set, and block your way to victory in this volleyball showdown.", 110, "2024-03-20", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Chess Challenge", "Outsmart your opponents in a strategic chess competition.", 90, "2024-03-25", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Debate Duel", "Engage in intellectual debates and hone your public speaking skills.", 100, "2024-03-30", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("Photography Contest", "Capture stunning moments and express your creativity through photography.", 130, "2024-04-05", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
            eventsGrid.add(new EventCard("E-Sports Extravaganza", "Compete in popular video games and prove your gaming prowess.", 170, "2024-04-10", accentColor));
            eventsGrid.add(Box.createVerticalStrut(25));
        }

        JScrollPane scrollPane = new JScrollPane(eventsGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Force vertical scrollbar to always show
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Ensure no horizontal scrollbar

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.CENTER); // header back button handles navigation
        // eventsPanel.add(topPanel, BorderLayout.NORTH); // Removed eventsPanel

        // eventsPanel.add(scrollPane, BorderLayout.CENTER); // Removed eventsPanel

        eventsContentPanel.add(topPanel, BorderLayout.NORTH);
        eventsContentPanel.add(scrollPane, BorderLayout.CENTER); // Directly add scrollPane to eventsContentPanel
        eventsContentPanel.revalidate();
        eventsContentPanel.repaint();
    }

    private void showEventManagersView() {
        currentView = "MANAGERS";
        currentManager = "";
        headerBackButton.setToolTipText("Go back to Home");
        eventsContentPanel.removeAll();
        eventsContentPanel.add(createEventManagersView());
        eventsContentPanel.revalidate();
        eventsContentPanel.repaint();
    }

    private Color getManagerColor(String managerName) {
        switch (managerName) {
            case "Binary Brains": return new Color(60, 140, 255);
            case "LFA": return new Color(120, 200, 255);
            case "LJSC": return new Color(100, 160, 255);
            default: return new Color(60, 140, 255);
        }
    }

    private JPanel createLeaderboardContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Search bar
        NeonPanel searchPanel = new NeonPanel(10);
        searchPanel.setLayout(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        searchPanel.setGlowEnabled(true);
        searchPanel.setGlowColor(new Color(255, 50, 150));

        String placeholder="Search by name or enrollment...";
        JTextField searchField = new JTextField(placeholder);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(Color.WHITE);
        searchField.setBackground(new Color(30, 30, 50));
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchField.setCaretColor(Color.WHITE);
        searchField.setCaretPosition(0);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(Color.WHITE); // normal typing color
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(Color.GRAY); // back to placeholder color
                    searchField.setCaretPosition(0);
                }
            }
        });


        searchPanel.add(searchField, BorderLayout.CENTER);

        // Leaderboard content
        JPanel leaderboardContent = new JPanel();
        leaderboardContent.setLayout(new BoxLayout(leaderboardContent, BoxLayout.Y_AXIS));
        leaderboardContent.setOpaque(false);

        // Try to load data from database, show error if fails
        try {
            loadLeaderboardData(leaderboardContent);
        } catch (Exception e) {
            showDatabaseError(leaderboardContent);
        }

        JScrollPane scrollPane = new JScrollPane(leaderboardContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private void loadLeaderboardData(JPanel container) throws Exception {
        try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS);

             PreparedStatement pst = con.prepareStatement("SELECT name, enrollment_no, " +
                     "(SELECT SUM(points_earned) FROM participants p WHERE p.enrollment_no = u.enrollment_no) AS total_points " +
                     "FROM users u WHERE EXISTS (SELECT 1 FROM participants p WHERE p.enrollment_no = u.enrollment_no)");
             ResultSet rs = pst.executeQuery()) {

            BST leaderboard = new BST();

            // Insert each student into BST with their total points
            while (rs.next()) {
                String name = rs.getString("name");
                String enrollment = rs.getString("enrollment_no");
                int points = rs.getInt("total_points");

                leaderboard.insert(name, enrollment, points);
            }

            // Get sorted data from BST (highest points first)
            java.util.List<BSTNode> sortedData = leaderboard.getSortedData();

            // Add leaderboard rows from actual database data (sorted by points)
            int rank = 1;
            for (BSTNode node : sortedData) {
                container.add(new LeaderboardRow(rank, node.name, node.enrollmentNo, node.totalPoints));
                container.add(Box.createVerticalStrut(10));
                rank++;
            }

            // If no data found, show a message
            if (sortedData.isEmpty()) {
                JLabel noDataLabel = new JLabel("No leaderboard data available", SwingConstants.CENTER);
                noDataLabel.setForeground(Color.WHITE);
                noDataLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                container.add(noDataLabel);
            }
        }
    }

    private void showDatabaseError(JPanel container) {
        NeonPanel errorPanel = new NeonPanel(15);
        errorPanel.setLayout(new BorderLayout());
        errorPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        errorPanel.setGlowEnabled(true);
        errorPanel.setGlowColor(new Color(255, 100, 100));

        JLabel errorLabel = new JLabel("⚠️ Database Connection Error", SwingConstants.CENTER);
        errorLabel.setForeground(Color.WHITE);
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        errorLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel messageLabel = new JLabel("Unable to connect to database. Please check your connection and try again.", SwingConstants.CENTER);
        messageLabel.setForeground(new Color(200, 200, 200));
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        errorPanel.add(errorLabel, BorderLayout.NORTH);
        errorPanel.add(messageLabel, BorderLayout.CENTER);

        container.add(errorPanel);
    }

    private void showEventsView() {
        currentView = "MANAGERS";
        currentManager = "";
        headerBackButton.setToolTipText("Go back to Home");
        cardLayout.show(eventsContentPanel.getParent(), "EVENTS");
    }

    private void showLeaderboardView() {
        currentView = "LEADERBOARD";
        currentManager = "";
        headerBackButton.setToolTipText("Go back to Home");
        cardLayout.show(leaderboardContentPanel.getParent(), "LEADERBOARD");
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(true);
        footer.setBackground(new Color(10, 12, 18));
        footer.setBorder(new EmptyBorder(6, 16, 6, 16));
        JLabel credits = new JLabel("© 2025 Gaming Leaderboard • v1.0");
        credits.setForeground(new Color(210, 215, 230));
        credits.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footer.add(credits, BorderLayout.EAST);
        return footer;
    }
}