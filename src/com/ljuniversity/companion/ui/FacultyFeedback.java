package com.ljuniversity.companion.ui; // Added Package

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class FacultyFeedback extends JFrame {
    private final String LOGO_PATH = "logo.png"; // Replace with your logo file if available
    private final String DB_URL = "jdbc:mysql://localhost:3306/campus-connect";
    private final String USER = "root";
    private final String PASS = ""; // Update if password is set
    private final String FEEDBACK_FILE_NAME = "feedback_log.txt";

    private final List<Map<String, String>> facultyList = Arrays.asList(
            Map.of("initials", "JAS", "name", "Jinal Zala"),
            Map.of("initials", "ARP", "name", "Ankur Patel"),
            Map.of("initials", "KGB", "name", "Kamaldeep Bhatia"),
            Map.of("initials", "RMP", "name", "Anuj Bhat"),
            Map.of("initials", "KPP", "name", "Kishan Pala")
    );

    public FacultyFeedback() {
        setTitle("Feedback Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        add(createMainPanel());
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createDescriptionsPanel(), BorderLayout.CENTER);
        mainPanel.add(createTablePanel(), BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 20, 60), getWidth(), 0, new Color(0, 120, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(1100, 90));
        JLabel titleLabel = new JLabel("Feedback Form");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        header.add(titleLabel, BorderLayout.WEST);
        try {
            ImageIcon logoIcon = new ImageIcon(LOGO_PATH);
            Image img = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(img));
            logoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 30));
            header.add(logoLabel, BorderLayout.EAST);
        } catch (Exception e) {}
        return header;
    }

    private JPanel createDescriptionsPanel() {
        JPanel descPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        descPanel.setBackground(Color.WHITE);
        descPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        JTextArea facultyDesc = new JTextArea(
                "Faculty Points:\n" +
                        "• Quality of teaching\n" +
                        "• Subjective knowledge\n" +
                        "• Punctuality for the class\n" +
                        "• Use the lecture hours effectively\n" +
                        "• Complete syllabus in time\n" +
                        "• General attitude towards students\n" +
                        "• Faculty seriously teaching even if 1 to 2 students are present in the class\n" +
                        "• They don't motivate the students to bunk the class directly or indirectly\n" +
                        "• Faculty solve students doubt within 8 hours"
        );
        facultyDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        facultyDesc.setEditable(false);
        facultyDesc.setBackground(new Color(230, 245, 255));
        facultyDesc.setForeground(new Color(10, 20, 60));
        facultyDesc.setBorder(BorderFactory.createTitledBorder("Faculty Points"));
        JTextArea hodDesc = new JTextArea(
                "HOD Points:\n" +
                        "• Activeness in WhatsApp group\n" +
                        "• Available in every problem and situation\n" +
                        "• Approach towards students\n" +
                        "• Stricken towards notorious students\n" +
                        "• Overall control"
        );
        hodDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        hodDesc.setEditable(false);
        hodDesc.setBackground(new Color(230, 245, 255));
        hodDesc.setForeground(new Color(10, 20, 60));
        hodDesc.setBorder(BorderFactory.createTitledBorder("HOD Points"));
        descPanel.add(facultyDesc);
        descPanel.add(hodDesc);
        return descPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 30, 30));

        String[] columns = {"ID", "Initials", "Name", "C1 Overall (1-10)", "C2 Language", "C3 Class Control"};
        Object[][] data = new Object[facultyList.size()][6];
        for (int i = 0; i < facultyList.size(); i++) {
            Map<String, String> f = facultyList.get(i);
            data[i][0] = i + 1;
            data[i][1] = f.get("initials");
            data[i][2] = f.get("name");
            data[i][3] = 1; // default for c1
            data[i][4] = "A (Always English)"; // default for c2
            data[i][5] = "A (Excellent)"; // default for c3
        }

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col >= 3; // Only c1, c2, c3 are editable
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return String.class;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.setBackground(new Color(240, 250, 255));
        table.setForeground(new Color(10, 20, 60));
        table.getTableHeader().setBackground(new Color(10, 120, 255));
        table.getTableHeader().setForeground(Color.WHITE);

        // Set up combo box editors for c1, c2, c3 columns
        JComboBox<Integer> c1Combo = new JComboBox<>();
        for (int i = 1; i <= 10; i++) c1Combo.addItem(i);
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(c1Combo));

        JComboBox<String> c2Combo = new JComboBox<>(new String[]{
                "A (Always English)", "B (Mostly English)", "C (Mix Hindi/English)", "D (Mostly Hindi/Casual)"
        });
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(c2Combo));

        JComboBox<String> c3Combo = new JComboBox<>(new String[]{
                "A (Excellent)", "B (Good)", "C (Often noisy)", "D (No control)"
        });
        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(c3Combo));

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(1000, Math.min(facultyList.size(), 5) * 40 + 30));
        tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        btnPanel.setBackground(Color.WHITE);
        JButton submitBtn = new JButton("Submit Feedback");
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        submitBtn.setBackground(new Color(10, 120, 255));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        backBtn.setBackground(new Color(200, 220, 255));
        backBtn.setForeground(new Color(10, 20, 60));
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPanel.add(submitBtn);
        btnPanel.add(backBtn);

        // Submit Action
        submitBtn.addActionListener(e -> {
            try (Connection con = DriverManager.getConnection(DB_URL, USER, PASS)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                for (int i = 0; i < facultyList.size(); i++) {
                    Map<String, String> faculty = facultyList.get(i);
                    int c1 = Integer.parseInt(model.getValueAt(i, 3).toString());
                    String c2 = model.getValueAt(i, 4).toString();
                    String c3 = model.getValueAt(i, 5).toString();
                    String insertQuery = "INSERT INTO feed_back (initials, name, c1_overall_points, c2_abcd_language, c3_class_control) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
                        pst.setString(1, faculty.get("initials"));
                        pst.setString(2, faculty.get("name"));
                        pst.setInt(3, c1);
                        pst.setString(4, c2.substring(0, 1));
                        pst.setString(5, c3.substring(0, 1));
                        pst.executeUpdate();
                        writeFeedbackToFile(faculty, c1, c2.substring(0, 1), c3.substring(0, 1));
                    }
                }
                JOptionPane.showMessageDialog(this, "Feedback submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error submitting feedback!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        // Back Action
        backBtn.addActionListener(e -> {
            this.dispose();
            com.ljuniversity.companion.ui.HomePageSwing.show();
        });

        // Layout
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(btnPanel, BorderLayout.SOUTH);
        return tablePanel;
    }

    private void writeFeedbackToFile(Map<String, String> faculty, int c1, String c2, String c3) {
        try (FileWriter fw = new FileWriter(FEEDBACK_FILE_NAME, true); // true for append mode
             PrintWriter pw = new PrintWriter(fw)) {
            String line = String.format("%s,%s,%d,%s,%s",
                    faculty.get("initials"),
                    faculty.get("name"),
                    c1,
                    c2,
                    c3);
            pw.println(line);
        } catch (IOException e) {
            System.err.println("Error writing feedback to file: " + e.getMessage());
            // Optionally show a JOptionPane error message to the user
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            new FacultyFeedback().setVisible(true);
        });
    }
}