package com.ljuniversity.companion.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class LoginPageSwing extends JFrame { // Changed class access to public

    // --- Backend Logic ---
    private static final String URL = "jdbc:mysql://localhost:3306/campus-connect";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private Connection conn;

    public LoginPageSwing() { // Changed constructor access to public
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found. Please add the Connector/J JAR.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        connectToDatabase(); // Connect to DB on startup

        setTitle("Student Companion - Login/Signup");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeConnection();
                dispose();
                System.exit(0);
            }
        });
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Main panel with background image
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                //ImageIcon bgImage = new ImageIcon("src/bg4.jpg"); // Path commented out in original
                //g.drawImage(bgImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        setContentPane(mainPanel);
        mainPanel.setLayout(new GridBagLayout());

        // Header panel for the logo and university name
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // University Name Label
        JLabel universityLabel = new JLabel("LJ University");
        universityLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        universityLabel.setForeground(new Color(0, 102, 204));
        universityLabel.setBorder(new EmptyBorder(10, 20, 10, 10));
        headerPanel.add(universityLabel, BorderLayout.WEST);

        // Logo - Loading from classpath (assuming img.png is in /resources)
        ImageIcon scaledLogoIcon = null;
        try {
            ImageIcon originalLogoIcon = new ImageIcon(getClass().getResource("/img.png"));
            Image originalLogoImage = originalLogoIcon.getImage();
            Image scaledLogoImage = originalLogoImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            scaledLogoIcon = new ImageIcon(scaledLogoImage);
        } catch (Exception e) {
            System.err.println("Login Page: Error loading logo /img.png from classpath.");
            scaledLogoIcon = null;
        }

        JLabel logoLabel = new JLabel(scaledLogoIcon);
        logoLabel.setBorder(new EmptyBorder(10, 10, 10, 20));
        headerPanel.add(logoLabel, BorderLayout.EAST);

        // Add header panel to the top
        GridBagConstraints headerGbc = new GridBagConstraints();
        headerGbc.gridx = 0;
        headerGbc.gridy = 0;
        headerGbc.weightx = 1.0;
        headerGbc.anchor = GridBagConstraints.NORTH;
        headerGbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(headerPanel, headerGbc);

        // Center with CardLayout
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);
        mainContent.setOpaque(false);

        mainContent.add(createChoicePanel(), "Choice");
        mainContent.add(createLoginPanel(), "Login");
        mainContent.add(createSignupPanel(), "Signup");

        // Add login panel to the center
        GridBagConstraints loginGbc = new GridBagConstraints();
        loginGbc.gridx = 0;
        loginGbc.gridy = 1;
        loginGbc.weighty = 1.0;
        loginGbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(mainContent, loginGbc);

        // Show choice first
        cardLayout.show(mainContent, "Choice");

        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" Database connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            // Don't exit, allow user to view the UI, but logins/signups will fail
        }
    }

    private void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Signup logic (using transaction)
    private boolean signup(String username, String erno, String pass) {
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database is not connected. Cannot sign up.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            conn.setAutoCommit(false); // Step 1: Disable auto-commit to start a transaction

            String query = "INSERT INTO loginPage (enrollment_no, username, password) VALUES (?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, erno);
                ps.setString(2, username);
                ps.setString(3, pass);

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "Enrollment No already exists. Try logging in.", "Signup Failed", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    // Login logic (using simple query)
    private boolean login(String erno, String pass) {
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database is not connected. Cannot log in.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (erno == null || erno.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
                return false;
            }

            String query = "SELECT password FROM loginPage WHERE enrollment_no = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, erno.trim());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPass = rs.getString("password");
                return dbPass.equals(pass);
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // --- UI Components ---
    private JTextField usernameField, ernoField, ernoLoginField;
    private JPasswordField passwordField, confirmPasswordField, loginPasswordField;
    private JButton loginButton, signupButton;
    private JPanel mainContent;
    private CardLayout cardLayout;
    private JLabel loginErrorLabel;
    private JLabel userError, ernoError, passError, confirmError;

    // Choice Panel
    private JPanel createChoicePanel() {
        JPanel inner = new JPanel(new GridBagLayout());
        inner.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("Welcome to Student Companion");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 102, 204));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inner.add(title, gbc);

        gbc.gridy = 1;
        JButton btnLogin = new JButton("Login");
        styleMainButton(btnLogin);
        inner.add(btnLogin, gbc);

        gbc.gridy = 2;
        JButton btnSignup = new JButton("Signup");
        styleMainButton(btnSignup);
        inner.add(btnSignup, gbc);

        btnLogin.addActionListener(e -> cardLayout.show(mainContent, "Login"));
        btnSignup.addActionListener(e -> cardLayout.show(mainContent, "Signup"));

        return wrapInCard(inner);
    }

    // Login Panel
    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(255, 255, 255, 200));
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        loginPanel.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel titleLabel = new JLabel("Student Companion");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 102, 204));
        loginPanel.add(titleLabel, gbc);

        // Subtitle
        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Welcome Back!");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.DARK_GRAY);
        loginPanel.add(subtitleLabel, gbc);

        // Enrollment No
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel usernameLabel = new JLabel("Enrollment No:");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        usernameLabel.setForeground(new Color(0, 102, 204));
        loginPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        ernoLoginField = createStyledTextField();
        loginPanel.add(ernoLoginField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passwordLabel.setForeground(new Color(0, 102, 204));
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        loginPasswordField = createStyledPasswordField();
        loginPanel.add(loginPasswordField, gbc);

        // Error Label
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginErrorLabel = new JLabel(" ");
        loginErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loginErrorLabel.setForeground(Color.RED);
        loginErrorLabel.setPreferredSize(new Dimension(300, 20));
        loginPanel.add(loginErrorLabel, gbc);

        // Login Button
        gbc.gridy++;
        loginButton = new JButton("Login");
        styleMainButton(loginButton);
        loginPanel.add(loginButton, gbc);

        // Back Button
        gbc.gridy++;
        JButton backBtn = new JButton("Back");
        styleBackButton(backBtn);
        loginPanel.add(backBtn, gbc);

        // Enter key handling
        ernoLoginField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    loginPasswordField.requestFocus();
                }
            }
        });

        loginPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });

        // Login logic
        loginButton.addActionListener(e -> {
            String erno = ernoLoginField.getText().trim();
            String pass = new String(loginPasswordField.getPassword());

            loginErrorLabel.setText(" ");

            if (erno.isEmpty()) {
                loginErrorLabel.setText("Enrollment No is required");
                ernoLoginField.requestFocus();
                return;
            }

            if (pass.isEmpty()) {
                loginErrorLabel.setText("Password is required");
                loginPasswordField.requestFocus();
                return;
            }

            if (login(erno, pass)) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                dispose();
                HomePageSwing.show();
            } else {
                loginErrorLabel.setText("Invalid Enrollment No or Password.");
                loginPasswordField.setText("");
                loginPasswordField.requestFocus();
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainContent, "Choice"));

        return loginPanel;
    }


    // Signup Panel
    private JPanel createSignupPanel() {
        JPanel inner = new JPanel(new GridBagLayout());
        inner.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel title = new JLabel("Signup");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(0, 102, 204));
        inner.add(title, gbc);

        gbc.gridy++;
        JLabel subtitle = new JLabel("Create your account");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(Color.DARK_GRAY);
        inner.add(subtitle, gbc);

        // Username
        gbc.gridy++;
        gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        userLabel.setForeground(new Color(0, 102, 204));
        inner.add(userLabel, gbc);

        gbc.gridx = 1;
        usernameField = createStyledTextField();
        inner.add(usernameField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        userError = new JLabel(" ");
        userError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userError.setForeground(Color.RED);
        userError.setPreferredSize(new Dimension(200, 16));
        inner.add(userError, gbc);

        // Enrollment No
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel ernoLabel = new JLabel("Enrollment No:");
        ernoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        ernoLabel.setForeground(new Color(0, 102, 204));
        inner.add(ernoLabel, gbc);

        gbc.gridx = 1;
        ernoField = createStyledTextField();
        inner.add(ernoField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        ernoError = new JLabel(" ");
        ernoError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ernoError.setForeground(Color.RED);
        ernoError.setPreferredSize(new Dimension(200, 16));
        inner.add(ernoError, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        passLabel.setForeground(new Color(0, 102, 204));
        inner.add(passLabel, gbc);

        gbc.gridx = 1;
        passwordField = createStyledPasswordField();
        inner.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        passError = new JLabel(" ");
        passError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passError.setForeground(Color.RED);
        passError.setPreferredSize(new Dimension(200, 16));
        inner.add(passError, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy++;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        confirmLabel.setForeground(new Color(0, 102, 204));
        inner.add(confirmLabel, gbc);

        gbc.gridx = 1;
        confirmPasswordField = createStyledPasswordField();
        inner.add(confirmPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        confirmError = new JLabel(" ");
        confirmError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmError.setForeground(Color.RED);
        confirmError.setPreferredSize(new Dimension(200, 16));
        inner.add(confirmError, gbc);

        // Signup Button
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        signupButton = new JButton("Signup");
        styleMainButton(signupButton);
        inner.add(signupButton, gbc);

        // Back button
        gbc.gridy++;
        JButton backBtn = new JButton("Back");
        styleBackButton(backBtn);
        inner.add(backBtn, gbc);

        signupButton.addActionListener(e -> {
            // Clear all previous errors
            userError.setText(" ");
            ernoError.setText(" ");
            passError.setText(" ");
            confirmError.setText(" ");

            String user = usernameField.getText().trim();
            String erno = ernoField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());

            boolean valid = true;

            if (user.isEmpty()) {
                userError.setText("Username is required");
                valid = false;
            }
            if (erno.isEmpty()) {
                ernoError.setText("Enrollment No is required");
                valid = false;
            } else if (!erno.matches("\\d+")) {
                ernoError.setText("Enrollment No must contain only digits");
                valid = false;
            } else if (erno.length() != 14) {
                ernoError.setText("Enrollment No must be exactly 14 digits");
                valid = false;
            }
            if (pass.isEmpty()) {
                passError.setText("Password is required");
                valid = false;
            } else if (pass.length() < 6) {
                passError.setText("Password must be at least 6 characters");
                valid = false;
            }
            if (confirm.isEmpty()) {
                confirmError.setText("Confirm Password is required");
                valid = false;
            } else if (!pass.equals(confirm)) {
                confirmError.setText("Passwords do not match");
                valid = false;
            }

            if (valid) {
                if (signup(user, erno, pass)) {
                    JOptionPane.showMessageDialog(this,
                            "Signup Successful! Please login now.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainContent, "Login");

                    // Clear all fields after successful signup
                    usernameField.setText("");
                    ernoField.setText("");
                    passwordField.setText("");
                    confirmPasswordField.setText("");

                    // Clear all error labels
                    userError.setText(" ");
                    ernoError.setText(" ");
                    passError.setText(" ");
                    confirmError.setText(" ");
                }
            }
        });

        backBtn.addActionListener(e -> cardLayout.show(mainContent, "Choice"));

        return wrapInCard(inner);
    }

    // Card Wrapper with rounded semi-transparent bg
    private JPanel wrapInCard(JPanel innerPanel) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.add(innerPanel);
        return card;
    }

    // Styled Field
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 102, 204)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 102, 204)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    // Button Styles
    private void styleMainButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 40, 10, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                btn.setBackground(new Color(0, 81, 163));
            }
            public void mouseExited(MouseEvent evt) {
                btn.setBackground(new Color(0, 102, 204));
            }
        });
    }

    private void styleBackButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(new Color(180, 180, 180));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }}

