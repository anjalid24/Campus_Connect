package com.ljuniversity.companion.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

/**
 * HomePageSwing.java
 * ------------------
 * Improved home page UI for the LJ University Student Companion app.
 * Features:
 *  - Animated gradient header with logo & live clock
 *  - Modern card-style feature tiles with icons + descriptions
 *  - Consistent navigation (all modules disposed/shown correctly)
 *  - DB-powered welcome greeting (fetches logged-in student name)
 *  - Footer with status indicator
 */
public final class HomePageSwing {

    /* ──────────────── COLOURS ──────────────── */
    private static final Color BLUE_DARK   = new Color(25,  118, 210);
    private static final Color BLUE_MID    = new Color(21,  101, 192);
    private static final Color BLUE_LIGHT  = new Color(66,  165, 245);
    private static final Color BLUE_ACCENT = new Color(3,   169, 244);
    private static final Color WHITE       = Color.WHITE;
    private static final Color BG          = new Color(240, 245, 250);
    private static final Color CARD_BG     = new Color(255, 255, 255);
    private static final Color CARD_HOVER  = new Color(232, 244, 253);
    private static final Color TEXT_DARK   = new Color(30,  30,  50);
    private static final Color TEXT_MUTED  = new Color(100, 120, 150);
    private static final Color SHADOW_CLR  = new Color(0,   0,   0, 22);
    private static final Color SUCCESS     = new Color(46,  160, 67);
    private static final Color WARN        = new Color(230, 81,  0);

    /* ──────────────── DB ──────────────── */
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/campus-connect";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    /* ──────────────── ENTRY POINT ──────────────── */
    public static void show() {
        SwingUtilities.invokeLater(() -> buildAndShow(null));
    }

    public static void show(String enrollmentNo) {
        SwingUtilities.invokeLater(() -> buildAndShow(enrollmentNo));
    }

    /* ──────────────── BUILD UI ──────────────── */
    private static void buildAndShow(String enrollmentNo) {
        JFrame frame = new JFrame("LJ University – Student Companion");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout(0, 0));

        // ── Background ──────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, new Color(224, 238, 250),
                        getWidth(), getHeight(), new Color(206, 229, 248)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setOpaque(false);
        frame.setContentPane(root);

        // ── Header ──────────────────────────────────────────────────────────
        root.add(buildHeader(frame, enrollmentNo), BorderLayout.NORTH);

        // ── Feature Grid ────────────────────────────────────────────────────
        JPanel gridWrapper = new JPanel(new GridBagLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.setBorder(new EmptyBorder(30, 60, 10, 60));

        JPanel grid = buildFeatureGrid(frame);
        grid.setOpaque(false);
        gridWrapper.add(grid, new GridBagConstraints());

        JScrollPane scroll = new JScrollPane(gridWrapper);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        // ── Footer ──────────────────────────────────────────────────────────
        root.add(buildFooter(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /* ══════════════ HEADER ══════════════ */
    private static JPanel buildHeader(JFrame frame, String enrollmentNo) {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, BLUE_MID, getWidth(), 0, BLUE_ACCENT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 90));
        header.setBorder(new EmptyBorder(0, 30, 0, 20));

        // Left: University logo placeholder + title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        left.setOpaque(false);

        // Logo circle placeholder (replace with actual ImageIcon if available)
        JLabel logoIcon = new JLabel("🎓") {
            @Override public Dimension getPreferredSize() { return new Dimension(52, 52); }
        };
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        logoIcon.setHorizontalAlignment(SwingConstants.CENTER);
        left.add(logoIcon);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 0));
        titleBox.setOpaque(false);
        JLabel titleLbl = new JLabel("LJ University");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLbl.setForeground(WHITE);
        JLabel subtitleLbl = new JLabel("Student Companion");
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLbl.setForeground(new Color(200, 225, 255));
        titleBox.add(titleLbl);
        titleBox.add(subtitleLbl);
        left.add(titleBox);

        header.add(left, BorderLayout.WEST);

        // Center: Greeting
        JLabel greetLbl = new JLabel("", SwingConstants.CENTER);
        greetLbl.setFont(new Font("Segoe UI", Font.ITALIC, 17));
        greetLbl.setForeground(new Color(200, 230, 255));
        // Load name in background
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() {
                return fetchStudentName(enrollmentNo);
            }
            @Override protected void done() {
                try {
                    String name = get();
                    if (name != null && !name.isEmpty()) {
                        greetLbl.setText("Welcome back, " + name + "  👋");
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
        header.add(greetLbl, BorderLayout.CENTER);

        // Right: Clock + Exit button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        JLabel clockLbl = new JLabel();
        clockLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        clockLbl.setForeground(WHITE);
        updateClock(clockLbl);
        new Timer(1000, e -> updateClock(clockLbl)).start();
        right.add(clockLbl);

        JButton exitBtn = headerBtn("✕  Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        right.add(exitBtn);

        header.add(right, BorderLayout.EAST);
        return header;
    }

    private static void updateClock(JLabel lbl) {
        java.time.LocalTime t = java.time.LocalTime.now();
        lbl.setText(String.format("%02d:%02d:%02d", t.getHour(), t.getMinute(), t.getSecond()));
    }

    private static String fetchStudentName(String enrollmentNo) {
        if (enrollmentNo == null || enrollmentNo.isBlank()) return null;
        try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement ps = c.prepareStatement(
                     "SELECT name FROM studentinfo WHERE enrollment_number = ?")) {
            ps.setString(1, enrollmentNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (Exception ignored) {}
        return null;
    }

    /* ══════════════ FEATURE GRID ══════════════ */

    /**
     * Feature tile data: { title, emoji, description, colour accent }
     */
    private static final Object[][] FEATURES = {
        { "Task Manager",        "📋", "Add, view & manage your personal tasks and deadlines.",   new Color(33, 150, 243) },
        { "Career Guidance",     "📈", "Explore tech career paths, growth charts & AI mentoring.", new Color(0, 188, 212)  },
        { "Gaming & Leaderboard","🏆", "Track campus gaming events and compete on the leaderboard.",new Color(255, 152, 0) },
        { "Complaint Box",       "📬", "Submit anonymous complaints or suggestions safely.",        new Color(156, 39, 176)},
        { "Feedback Form",       "📝", "Rate faculty performance and share your learning experience.",new Color(76, 175, 80)},
        { "Academic Resources",  "📚", "Access shared notes, slides & study material.",            new Color(244, 67, 54) },
    };

    private static JPanel buildFeatureGrid(JFrame frame) {
        // 3-column grid
        JPanel grid = new JPanel(new GridLayout(2, 3, 24, 24));
        grid.setOpaque(false);

        for (Object[] f : FEATURES) {
            grid.add(buildFeatureCard(frame, (String) f[0], (String) f[1],
                    (String) f[2], (Color) f[3]));
        }
        return grid;
    }

    private static JPanel buildFeatureCard(JFrame frame, String title,
                                           String emoji, String desc, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            private boolean hovered = false;

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Drop shadow
                g2.setColor(SHADOW_CLR);
                g2.fillRoundRect(4, 6, getWidth() - 4, getHeight() - 4, 22, 22);

                // Card body
                g2.setColor(hovered ? CARD_HOVER : CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 22, 22);

                // Left accent stripe
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 6, getHeight() - 4, 6, 6);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? accent.brighter() : new Color(220, 230, 240));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 22, 22);
                g2.dispose();
            }

            { // init block — hover listener
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
                    @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); setCursor(Cursor.getDefaultCursor()); }
                    @Override public void mousePressed(MouseEvent e) { handleFeatureClick(frame, title); }
                });
            }
        };
        card.setPreferredSize(new Dimension(280, 170));
        card.setBorder(new EmptyBorder(20, 22, 18, 18));

        // Emoji icon
        JLabel iconLbl = new JLabel(emoji);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));

        // Title
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(TEXT_DARK);

        // Description
        JLabel descLbl = new JLabel("<html><body style='width:220px'>" + desc + "</body></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(TEXT_MUTED);

        // Accent "Open →" label
        JLabel openLbl = new JLabel("Open →");
        openLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        openLbl.setForeground(accent);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        top.setOpaque(false);
        top.add(iconLbl);
        top.add(titleLbl);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(descLbl, BorderLayout.CENTER);
        bottom.add(openLbl, BorderLayout.SOUTH);

        card.add(top,    BorderLayout.NORTH);
        card.add(bottom, BorderLayout.CENTER);
        return card;
    }

    /* ══════════════ NAVIGATION (all fixed) ══════════════ */
    private static void handleFeatureClick(JFrame frame, String feature) {
        try {
            switch (feature) {
                case "Task Manager":
                    frame.setVisible(false);
                    TaskManagerUI.openTaskManager();
                    frame.dispose();
                    break;

                case "Career Guidance":
                    frame.setVisible(false);
                    CareerGuidance.main(new String[]{});
                    frame.dispose();
                    break;

                case "Gaming & Leaderboard":
                    frame.setVisible(false);
                    new EventsLeaderboardUI().createAndShowGUI();
                    frame.dispose();
                    break;

                case "Complaint Box":
                    frame.setVisible(false);
                    SubmissionBoxUI.openSubmissionBox();
                    frame.dispose();
                    break;

                case "Feedback Form":
                    frame.setVisible(false);
                    new FacultyFeedback().setVisible(true);
                    frame.dispose();
                    break;

                case "Academic Resources":
                    frame.setVisible(false);
                    new AcademicResourcesUI().setVisible(true);
                    frame.dispose();
                    break;

                default:
                    JOptionPane.showMessageDialog(frame,
                            "Feature \"" + feature + "\" is not yet available.",
                            "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            // If child window crashes, bring homepage back and show error
            ex.printStackTrace();
            frame.setVisible(true);
            JOptionPane.showMessageDialog(frame,
                    "Could not open \"" + feature + "\":\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ══════════════ FOOTER ══════════════ */
    private static JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(213, 229, 245));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(0, 38));
        footer.setBorder(new EmptyBorder(4, 20, 4, 20));

        // DB status indicator (check connection async)
        JLabel statusDot  = new JLabel("●");
        statusDot.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusDot.setForeground(TEXT_MUTED);

        JLabel statusText = new JLabel("Checking database…");
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusText.setForeground(TEXT_MUTED);

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                try (Connection c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    return c.isValid(2);
                } catch (Exception e) { return false; }
            }
            @Override protected void done() {
                try {
                    boolean ok = get();
                    statusDot.setForeground(ok ? SUCCESS : WARN);
                    statusText.setText(ok ? "Database connected" : "Database offline – some features may not work");
                } catch (Exception ignored) {}
            }
        }.execute();

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);
        left.add(statusDot);
        left.add(statusText);

        JLabel copy = new JLabel("© 2025 LJ University  ·  Student Companion");
        copy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copy.setForeground(TEXT_MUTED);

        footer.add(left, BorderLayout.WEST);
        footer.add(copy, BorderLayout.EAST);
        return footer;
    }

    /* ══════════════ HELPERS ══════════════ */
    private static JButton headerBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(6, 16, 6, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /* ══════════════ MAIN ══════════════ */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginPageSwing().setVisible(true));
    }
}
