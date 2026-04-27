package com.ljuniversity.companion.ui;

// --- Imports from model/util ---
import com.ljuniversity.companion.model.Task;
import com.ljuniversity.companion.model.StudentInfo;
import com.ljuniversity.companion.util.GradientPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.sql.*;

/**
 * TaskManagerUI - Modern, professional Swing Task Manager using JDBC.
 * (UI Logic)
 */
public class TaskManagerUI extends JFrame {
    // JDBC connection variables
    private static final String DB_URL = "jdbc:mysql://localhost:3306/campus-connect";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // UI Components
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private SearchPanel searchPanel;
    private TaskPanel taskPanel;
    private SignUpPanel signUpPanel;
    private JButton fullscreenBtn;
    private boolean isFullscreen = true;
    private Connection connection;

    // Color theme
    private static final Color BLUE1 = new Color(36, 123, 255);
    private static final Color BLUE2 = new Color(0, 212, 255);
    private static final Color WHITE = Color.WHITE;

    public TaskManagerUI() {
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(WHITE);

        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (Exception e) {
            showError("Database connection failed: " + e.getMessage());
            // In a production app, you might not exit here, but allow the UI to show an error state.
            // Since this is a critical component, we allow it to proceed to error message.
        }

        // Panels
        searchPanel = new SearchPanel();
        taskPanel = new TaskPanel();
        signUpPanel = new SignUpPanel();
        mainPanel.add(searchPanel, "search");
        mainPanel.add(taskPanel, "tasks");
        mainPanel.add(signUpPanel, "signup");
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Fullscreen toggle button
        fullscreenBtn = createGradientButton(isFullscreen ? "Windowed" : "Fullscreen");
        fullscreenBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fullscreenBtn.setFocusPainted(false);
        fullscreenBtn.addActionListener(e -> toggleFullscreen());

        // Add Back to Home button
        JButton backToHomeBtn = createGradientButton("Back to Home");
        backToHomeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        backToHomeBtn.setFocusPainted(false);
        backToHomeBtn.addActionListener(e -> {
            dispose();
            HomePageSwing.show();
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, BLUE1, getWidth(), 0, BLUE2));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setPreferredSize(new Dimension(0, 40));
        topBar.setOpaque(false);
        topBar.add(backToHomeBtn);
        topBar.add(fullscreenBtn);

        JButton closeBtn = createGradientButton("X");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(closeBtn);

        add(topBar, BorderLayout.NORTH);
        cardLayout.show(mainPanel, "search");
    }

    // Toggle fullscreen/windowed
    private void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        dispose();
        setUndecorated(isFullscreen);
        setExtendedState(isFullscreen ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
        setVisible(true);
        fullscreenBtn.setText(isFullscreen ? "Windowed" : "Fullscreen");
    }

    // Close database connection when window is disposed
    @Override
    public void dispose() {
        try {
            if (connection != null) connection.close();
        } catch (Exception ignored) {}
        super.dispose();
    }

    // Helper: Show error dialog
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Helper: Create a gradient button
    private JButton createGradientButton(String text) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, BLUE1, getWidth(), getHeight(), BLUE2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Panel for searching user by enrollment number
    class SearchPanel extends GradientPanel {
        private JTextField enrollmentField = new JTextField(18);
        public SearchPanel() {
            super(BLUE1, BLUE2);
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(80, 80, 80, 80));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(16, 10, 16, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;

            JLabel title = new JLabel("Task Manager");
            title.setFont(new Font("Segoe UI", Font.BOLD, 44));
            title.setForeground(WHITE);
            add(title, gbc);

            gbc.gridy++;
            JLabel enrollLabel = new JLabel("Enter Enrollment Number:");
            enrollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            enrollLabel.setForeground(WHITE);
            add(enrollLabel, gbc);

            gbc.gridy++;
            enrollmentField.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            add(enrollmentField, gbc);

            gbc.gridy++;
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnPanel.setOpaque(false);

            JButton searchBtn = createGradientButton("Search");
            searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            searchBtn.addActionListener(e -> {
                String enroll = enrollmentField.getText().trim();
                if (enroll.isEmpty()) {
                    showError("Please enter enrollment number.");
                    return;
                }
                if (!enroll.matches("\\d+")) {
                    showError("Numbers only");
                    return;
                }
                StudentInfo info = fetchStudentInfo(enroll);
                if (info == null) {
                    showError("Enrollment number not found.");
                } else {
                    taskPanel.setStudentInfo(info);
                    cardLayout.show(mainPanel, "tasks");
                }
            });

            JButton signUpBtn = createGradientButton("Sign Up New Student");
            signUpBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            signUpBtn.addActionListener(e -> {
                signUpPanel.clearFields();
                cardLayout.show(mainPanel, "signup");
            });

            btnPanel.add(searchBtn);
            btnPanel.add(signUpBtn);
            add(btnPanel, gbc);
        }
    }

    // Panel for displaying and managing tasks
    class TaskPanel extends GradientPanel {
        private JLabel nameLabel = new JLabel(), divLabel = new JLabel();
        private DefaultListModel<Task> taskListModel = new DefaultListModel<>();
        private JList<Task> taskList = new JList<>(taskListModel);
        private JTextArea taskDetailsArea = new JTextArea();
        private StudentInfo currentStudent;

        public TaskPanel() {
            super(BLUE2, BLUE1);
            setLayout(new BorderLayout(20, 10));
            setBorder(new EmptyBorder(30, 30, 30, 30));

            // Top: User info and back
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);

            JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            userInfoPanel.setOpaque(false);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
            nameLabel.setForeground(WHITE);
            divLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            divLabel.setForeground(WHITE);
            userInfoPanel.add(nameLabel);
            userInfoPanel.add(Box.createHorizontalStrut(20));
            userInfoPanel.add(divLabel);
            topPanel.add(userInfoPanel, BorderLayout.WEST);

            JButton backBtn = createGradientButton("Back");
            backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            backBtn.addActionListener(e -> cardLayout.show(mainPanel, "search"));
            topPanel.add(backBtn, BorderLayout.EAST);
            add(topPanel, BorderLayout.NORTH);

            // Center: Task list and details
            JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            centerPanel.setOpaque(false);

            // Task list
            JPanel listPanel = new JPanel(new BorderLayout());
            listPanel.setOpaque(false);
            JLabel listLabel = new JLabel("Tasks");
            listLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            listLabel.setForeground(WHITE);
            listPanel.add(listLabel, BorderLayout.NORTH);

            taskList.setCellRenderer(new TaskCellRenderer());
            taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane listScroll = new JScrollPane(taskList);
            listPanel.add(listScroll, BorderLayout.CENTER);
            centerPanel.add(listPanel);

            // Task details
            JPanel detailsPanel = new JPanel(new BorderLayout());
            detailsPanel.setOpaque(false);
            JLabel detailsLabel = new JLabel("Task Details");
            detailsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            detailsLabel.setForeground(WHITE);
            detailsPanel.add(detailsLabel, BorderLayout.NORTH);

            taskDetailsArea.setEditable(false);
            taskDetailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            taskDetailsArea.setLineWrap(true);
            taskDetailsArea.setWrapStyleWord(true);
            JScrollPane detailsScroll = new JScrollPane(taskDetailsArea);
            detailsPanel.add(detailsScroll, BorderLayout.CENTER);
            centerPanel.add(detailsPanel);
            add(centerPanel, BorderLayout.CENTER);

            // Bottom: Action buttons
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            bottomPanel.setOpaque(false);

            JButton addTaskBtn = createGradientButton("Add Task");
            JButton updateTaskBtn = createGradientButton("Update Task");
            JButton markCompletedBtn = createGradientButton("Mark as Completed");
            JButton showCompletedBtn = createGradientButton("Show Completed Tasks");

            bottomPanel.add(addTaskBtn);
            bottomPanel.add(updateTaskBtn);
            bottomPanel.add(markCompletedBtn);
            bottomPanel.add(showCompletedBtn);
            add(bottomPanel, BorderLayout.SOUTH);

            // Listeners
            addTaskBtn.addActionListener(e -> addOrUpdateTaskDialog(null));
            updateTaskBtn.addActionListener(e -> {
                Task selected = taskList.getSelectedValue();
                if (selected == null) { showError("Select a task to update."); return; }
                addOrUpdateTaskDialog(selected);
            });
            markCompletedBtn.addActionListener(e -> {
                Task selected = taskList.getSelectedValue();
                if (selected == null) { showError("Select a task to mark as completed."); return; }
                if ("Completed".equals(selected.status)) { showError("Task is already completed."); return; }
                updateTaskStatus(selected.id, "Completed");
            });
            showCompletedBtn.addActionListener(e -> showCompletedTasksDialog());
            taskList.addListSelectionListener((ListSelectionEvent e) -> {
                Task selected = taskList.getSelectedValue();
                if (selected != null) showTaskDetails(selected);
            });
        }

        public void setStudentInfo(StudentInfo info) {
            this.currentStudent = info;
            nameLabel.setText("Name: " + info.name);
            divLabel.setText("Div: " + info.div);
            loadTasks();
            taskDetailsArea.setText("");
        }

        private void loadTasks() {
            taskListModel.clear();
            if (currentStudent == null || connection == null) return;

            try (PreparedStatement pst = connection.prepareStatement("SELECT * FROM tasks WHERE enrollment_number=?")) {
                pst.setString(1, currentStudent.enrollment);
                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) taskListModel.addElement(new Task(rs));
                }
            } catch (SQLException e) { showError("Error loading tasks: " + e.getMessage()); }
        }

        private void showTaskDetails(Task t) {
            taskDetailsArea.setText(String.format("ID: %d\nTitle: %s\nDescription: %s\nDeadline: %s\nStatus: %s", t.id, t.title, t.description, t.deadline, t.status));
        }

        private void addOrUpdateTaskDialog(Task task) {
            JTextField titleField = new JTextField(task == null ? "" : task.title);
            JTextField descField = new JTextField(task == null ? "" : task.description);
            JTextField deadlineField = new JTextField(task == null ? "" : task.deadline);
            JComboBox<String> statusBox = new JComboBox<>(new String[]{"Incomplete", "Completed"});
            if (task != null) statusBox.setSelectedItem(task.status);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Title:")); panel.add(titleField);
            panel.add(new JLabel("Description:")); panel.add(descField);
            panel.add(new JLabel("Deadline (YYYY-MM-DD):")); panel.add(deadlineField);
            if (task != null) { panel.add(new JLabel("Status:")); panel.add(statusBox); }

            int result = JOptionPane.showConfirmDialog(this, panel, task == null ? "Add New Task" : "Update Task", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                String title = titleField.getText().trim(), desc = descField.getText().trim(), deadline = deadlineField.getText().trim(), status = (String) statusBox.getSelectedItem();

                if (title.isEmpty() || deadline.isEmpty()) { showError("Title and Deadline are required."); return; }

                try {
                    // Validation for Date format
                    Date.valueOf(deadline);

                    if (task == null) {
                        // Add Task
                        try (PreparedStatement pst = connection.prepareStatement("INSERT INTO tasks (enrollment_number, title, description, deadline, status) VALUES (?, ?, ?, ?, 'Incomplete')")) {
                            pst.setString(1, currentStudent.enrollment);
                            pst.setString(2, title);
                            pst.setString(3, desc);
                            pst.setDate(4, Date.valueOf(deadline));
                            pst.executeUpdate();
                        }
                        JOptionPane.showMessageDialog(this, "Task added.");
                    } else {
                        // Update Task
                        try (PreparedStatement pst = connection.prepareStatement("UPDATE tasks SET title=?, description=?, deadline=?, status=? WHERE id=?")) {
                            pst.setString(1, title);
                            pst.setString(2, desc);
                            pst.setDate(3, Date.valueOf(deadline));
                            pst.setString(4, status);
                            pst.setInt(5, task.id);
                            pst.executeUpdate();
                        }
                        JOptionPane.showMessageDialog(this, "Task updated.");
                    }
                    loadTasks();
                } catch (IllegalArgumentException e) {
                    showError("Invalid date format. Use YYYY-MM-DD.");
                } catch (SQLException e) {
                    showError("Error saving task: " + e.getMessage());
                }
            }
        }

        private void updateTaskStatus(int id, String status) {
            if (connection == null) return;
            try (PreparedStatement pst = connection.prepareStatement("UPDATE tasks SET status=? WHERE id=?")) {
                pst.setString(1, status);
                pst.setInt(2, id);
                pst.executeUpdate();
                loadTasks();
                JOptionPane.showMessageDialog(this, "Task marked as completed.");
            } catch (SQLException e) { showError("Error updating status: " + e.getMessage()); }
        }

        private void showCompletedTasksDialog() {
            if (currentStudent == null || connection == null) return;
            try (PreparedStatement pst = connection.prepareStatement("SELECT * FROM tasks WHERE enrollment_number=? AND status='Completed'")) {
                pst.setString(1, currentStudent.enrollment);
                try (ResultSet rs = pst.executeQuery()) {
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) sb.append(String.format("[ID: %d] %s — Completed\n", rs.getInt("id"), rs.getString("title")));
                    if (sb.length() == 0) sb.append("No completed tasks.");
                    JOptionPane.showMessageDialog(this, sb.toString(), "Completed Tasks", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) { showError("Error loading completed tasks: " + e.getMessage()); }
        }
    }

    // Panel for signing up new students
    class SignUpPanel extends GradientPanel {
        private JTextField enrollField = new JTextField(), nameField = new JTextField(), divField = new JTextField();

        public SignUpPanel() {
            super(BLUE1, BLUE2);
            setLayout(new GridBagLayout());
            setBorder(new EmptyBorder(80, 80, 80, 80));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(14, 10, 14, 10);
            gbc.gridx = 0;
            gbc.gridy = 0;

            JLabel title = new JLabel("Sign Up New Student");
            title.setFont(new Font("Segoe UI", Font.BOLD, 36));
            title.setForeground(WHITE);
            add(title, gbc);

            gbc.gridy++;
            add(labeledField("Enrollment Number:", enrollField), gbc);
            gbc.gridy++;
            add(labeledField("Name:", nameField), gbc);
            gbc.gridy++;
            add(labeledField("Division:", divField), gbc);

            gbc.gridy++;
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            btnPanel.setOpaque(false);

            JButton registerBtn = createGradientButton("Register");
            registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            registerBtn.addActionListener(e -> {
                String enroll = enrollField.getText().trim(), name = nameField.getText().trim(), div = divField.getText().trim();

                if (enroll.isEmpty() || name.isEmpty() || div.isEmpty()) { showError("All fields are required."); return; }

                if (connection == null) { showError("Database is not connected."); return; }

                try (PreparedStatement pst = connection.prepareStatement("INSERT INTO studentinfo (enrollment_number, name, `div`) VALUES (?, ?, ?)") ) {
                    pst.setString(1, enroll);
                    pst.setString(2, name);
                    pst.setString(3, div);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Student registered.");
                    clearFields();
                    cardLayout.show(mainPanel, "search");
                } catch (SQLException ex) {
                    if (ex.getMessage().contains("Duplicate entry")) {
                        showError("Error: Enrollment Number already exists.");
                    } else {
                        showError("Error registering student: " + ex.getMessage());
                    }
                }
            });

            JButton backBtn = createGradientButton("Back");
            backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            backBtn.addActionListener(e -> cardLayout.show(mainPanel, "search"));

            btnPanel.add(registerBtn);
            btnPanel.add(backBtn);
            add(btnPanel, gbc);
        }

        public void clearFields() { enrollField.setText(""); nameField.setText(""); divField.setText(""); }

        private JPanel labeledField(String label, JTextField field) {
            JPanel p = new JPanel(new BorderLayout());
            p.setOpaque(false);

            JLabel l = new JLabel(label);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            l.setForeground(WHITE);

            field.setFont(new Font("Segoe UI", Font.PLAIN, 20));

            p.add(l, BorderLayout.NORTH);
            p.add(field, BorderLayout.CENTER);
            return p;
        }
    }

    // Custom cell renderer for task list (shows checkbox for completed)
    static class TaskCellRenderer extends JCheckBox implements ListCellRenderer<Task> {
        public Component getListCellRendererComponent(JList<? extends Task> list, Task value, int index, boolean isSelected, boolean cellHasFocus) {
            setText("[ID: " + value.id + "] " + value.title);
            setSelected("Completed".equals(value.status));
            setEnabled(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 18));
            setBackground(isSelected ? new Color(220, 240, 255) : Color.WHITE);
            return this;
        }
    }

    // Helper: Fetch student info from DB
    private StudentInfo fetchStudentInfo(String enrollment) {
        if (connection == null) return null;
        try (PreparedStatement pst = connection.prepareStatement("SELECT enrollment_number, name, `div` FROM studentinfo WHERE enrollment_number=?")) {
            pst.setString(1, enrollment);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) return new StudentInfo(rs.getString("enrollment_number"), rs.getString("name"), rs.getString("div"));
            }
        } catch (SQLException e) { showError("Error fetching student info: " + e.getMessage()); }
        return null;
    }

    // Static method for external calls
    public static void openTaskManager() {
        SwingUtilities.invokeLater(() -> {
            TaskManagerUI taskManager = new TaskManagerUI();
            taskManager.setVisible(true);
        });
    }

    // Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TaskManagerUI().setVisible(true));
    }

}