package com.ljuniversity.companion.ui; // Added Package

import com.ljuniversity.companion.model.Entry; // New Import
import com.ljuniversity.companion.util.GeminiClient; // New Import

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;
import java.net.URI;

/**
 * Tech-Growth Dashboard v3 (UI Logic)
 * Uses Entry (Model) and GeminiClient (Utility).
 */
class CareerGuidance extends JPanel {

    // ❌ REMOVED: Entry static class (Now imported from com.ljuniversity.companion.model.Entry)

    /* ======= COLORS ======= */
    private static final Color BG = new Color(25, 25, 40);
    private static final Color BAR_BG = new Color(50, 50, 70);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Color ACCENT = new Color(0, 180, 216);

    // bar and Pie chart colors
    private static final  Color[] colors = {
            new Color(30, 144, 255),  // Dodger Blue
            new Color(0,  180, 216),   // Blue Green
            new Color(72, 202, 228), // Sky Blue
            new Color(144,224, 239), // Light Blue
            new Color(173,232, 244), // Very Light Blue
            new Color(202,240, 248), // Pale Blue
            new Color(144,224, 239), // Light Blue (repeated)
            new Color(72, 202, 228),  // Sky Blue (repeated)
            new Color(0,  180, 216)    // Blue Green (repeated)
    };


    /* ======= FIELDS ======= */
    private final java.util.List<Entry> data = new ArrayList<>();
    private final int[] barLength;
    private int frame = 0;
    private static final int FRAMES = 30;
    private Timer animator;
    private boolean showPieChart = false;
    private float pieAnimationProgress = 0f;
    private Timer pieAnimator;

    /* ======= DIMENSIONS ======= */
    private static final int BAR_H = 28;
    private static final int GAP = 14;
    private static final int LEFT_W = 260;
    private static final int RIGHT_W = 60;

    /* ======= CONSTRUCTOR ======= */
    public CareerGuidance() {
        loadData();
        data.sort((a, b) -> b.percent - a.percent);
        barLength = new int[data.size()];
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ToolTipManager.sharedInstance().setInitialDelay(0);

        // Bar chart animation
        animator = new Timer(20, e -> {
            frame++;
            repaint();
            if (frame >= FRAMES) animator.stop();
        });
        animator.start();

        // Pie chart animation
        pieAnimator = new Timer(20, e -> {
            pieAnimationProgress += 0.05f;
            if (pieAnimationProgress >= 1f) {
                pieAnimationProgress = 1f;
                pieAnimator.stop();
            }
            repaint();
        });
    }

    /* ======= LOAD DATA ======= */
    private void loadData() {
        data.add(new Entry("AI & ML", 36, "Python, TensorFlow, PyTorch, Keras, MLOps"));
        data.add(new Entry("Data Science", 36, "Python, R, SQL, Spark, Tableau"));
        data.add(new Entry("Cloud Computing", 25, "AWS, Azure, GCP, Docker, K8s, Terraform"));
        data.add(new Entry("Cybersecurity", 25, "SIEM, Firewalls, IAM, 3.5 M jobs"));
        data.add(new Entry("Software & DevOps", 22, "React, Node, Java, Docker, Jenkins"));
        data.add(new Entry("Blockchain", 15, "Ethereum, Solidity, Hyperledger"));
        data.add(new Entry("IoT", 15, "MQTT, AWS IoT, Arduino, Raspberry Pi"));
        data.add(new Entry("AR/VR", 15, "Unity, Unreal, ARKit, ARCore"));
        data.add(new Entry("Quantum Computing", 10, "Qiskit, Cirq, Q#, Azure Quantum"));
    }

    /* ======= DRAW ======= */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (showPieChart) {
            drawPieChart(g2);
        } else {
            drawBarChart(g2);
        }
        g2.dispose();
    }

    private void drawBarChart(Graphics2D g2) {
        int w = getWidth();
        int usableW = w - LEFT_W - RIGHT_W;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);
        Font toolsFont = new Font("Segoe UI", Font.PLAIN, 13);
        g2.setFont(labelFont);
        FontMetrics fm = g2.getFontMetrics();

        int y = 0;
        for (int i = 0; i < data.size(); i++) {
            Entry e = data.get(i);

            // Label
            g2.setColor(TEXT);
            g2.drawString(e.field, 0, y + BAR_H / 2 + fm.getAscent() / 2 - 2);

            // Background bar
            g2.setColor(BAR_BG);
            g2.fillRect(LEFT_W, y, usableW, BAR_H);

            // Animated bar fill
            int len = (int) (usableW * e.percent / 100.0 *
                    (1 - Math.pow(1 - (double) frame / FRAMES, 3)));
            g2.setColor(colors[i % colors.length]) ;

            g2.fillRect(LEFT_W, y, len, BAR_H);

            // Percentage text
            g2.setColor(TEXT);
            g2.drawString(e.percent + "%", LEFT_W + len + 10, y + BAR_H / 2 + fm.getAscent() / 2 - 2);

            // Tools text
            g2.setFont(toolsFont);
            g2.drawString(e.tools, LEFT_W, y + BAR_H + 15);
            g2.setFont(labelFont);

            y += BAR_H + GAP + 20;
        }
    }

    private void drawPieChart(Graphics2D g2) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 3;



        // Calculate total percentage (should be 100% but we'll normalize just in case)
        double total = data.stream().mapToDouble(e -> e.percent).sum();

        // Draw pie slices with animation
        double startAngle = 90;
        for (int i = 0; i < data.size(); i++) {
            Entry e = data.get(i);
            double extent = 360 * (e.percent / total) * pieAnimationProgress;
            if (i == data.size() - 1) {
                extent = 360 * pieAnimationProgress - startAngle + 90;
            }

            g2.setColor(colors[i % colors.length]);
            g2.fillArc(centerX - radius, centerY - radius,
                    radius * 2, radius * 2,
                    (int) startAngle, (int) extent);

            // Draw label (all slices get labels now)
            double midAngle = Math.toRadians(startAngle + extent / 2);
            int labelRadius = radius + 30; // Position labels outside the pie
            int labelX = centerX + (int) (labelRadius * Math.cos(midAngle));
            int labelY = centerY + (int) (labelRadius * Math.sin(midAngle));

            g2.setColor(TEXT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String label = e.field + " (" + e.percent + "%)";
            FontMetrics fm = g2.getFontMetrics();

            // Adjust label position based on angle for better readability
            if (midAngle > Math.PI/2 && midAngle < 3*Math.PI/2) {
                labelX -= fm.stringWidth(label);
            }
            if (midAngle > Math.PI) {
                labelY += fm.getAscent();
            }

            g2.drawString(label, labelX, labelY);

            startAngle += extent;
        }

        // Draw center circle to make it a donut chart
        g2.setColor(BG);
        g2.fillOval(centerX - radius/2, centerY - radius/2, radius, radius);

        // Add title
        g2.setColor(TEXT);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String title = "Tech Growth Distribution";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, centerX - fm.stringWidth(title)/2, centerY - radius - 80);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        if (showPieChart) return null;

        int idx = e.getY() / (BAR_H + GAP + 20);
        if (idx >= 0 && idx < data.size()) {
            Entry en = data.get(idx);
            return "<html><b>" + en.field + "</b><br>" +
                    en.percent + "% growth<br>" +
                    en.tools + "</html>";
        }
        return null;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,
                data.size() * (BAR_H + GAP + 20) + 100);
    }

    /* ==============  MAIN DASHBOARD BUILDER  ============== */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Tech Growth Dashboard 2025");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            /* === ROOT PANEL === */
            JPanel root = new JPanel(new BorderLayout(20, 20));
            root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

            root.setBackground(BG);
            root.setBorder(new EmptyBorder(20, 10, 20, 20));

            // Add back button at the top left
            JButton backButton = new JButton("← Back to Home");
            backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            backButton.setForeground(TEXT);
            backButton.setBackground(new Color(40, 40, 55));
            backButton.setFocusPainted(false);
            backButton.setBorderPainted(false);
            backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            backButton.addActionListener(e -> {
                f.dispose();
                com.ljuniversity.companion.ui.HomePageSwing.show();
            });

            JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
            topBar.setOpaque(false);
            topBar.add(backButton);
            root.add(topBar);
            root.add(Box.createVerticalStrut(10));

            /* === 1. CHART TOGGLE BUTTON === */
            JPanel togglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            togglePanel.setOpaque(false);

            CareerGuidance chartPanel = new CareerGuidance();
            JToggleButton toggleButton = new JToggleButton("Switch to Pie Chart");
            toggleButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            toggleButton.setForeground(TEXT);
            toggleButton.setBackground(new Color(40, 40, 55));
            toggleButton.setFocusPainted(false);
            toggleButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT, 1),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));

            toggleButton.addActionListener(e -> {
                chartPanel.showPieChart = toggleButton.isSelected();
                if (toggleButton.isSelected()) {
                    toggleButton.setText("Switch to Bar Chart");
                    chartPanel.pieAnimationProgress = 0f;
                    chartPanel.pieAnimator.start();
                } else {
                    toggleButton.setText("Switch to Pie Chart");
                    chartPanel.frame = 0;
                    chartPanel.animator.start();
                }
                chartPanel.repaint();
            });

            togglePanel.add(toggleButton);
            root.add(togglePanel);
            root.add(Box.createVerticalStrut(10));

            /* === 2. CHART PANEL === */
            chartPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE,
                    chartPanel.getPreferredSize().height));   // allow vertical stretch only
            root.add(chartPanel);
            root.add(Box.createVerticalStrut(24));

            /* === 3. CHATBOT PANEL === */
            JPanel chatPanel = buildChatbotPanel();
            chatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            root.add(chatPanel);
            root.add(Box.createVerticalStrut(40));

            /* === 4. SOFT-SKILLS SECTION === */
            JPanel softSkillsPanel = buildSoftSkillsPanel();
            softSkillsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            root.add(softSkillsPanel);
            root.add(Box.createVerticalStrut(40));

            /* === 5. MENTORS SECTION === */
            JPanel mentorsPanel = buildMentorsPanel();
            mentorsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            root.add(mentorsPanel);

            /* === SINGLE SCROLLABLE VIEWPORT === */
            JScrollPane masterScroll = new JScrollPane(root,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);   // ← no horizontal bar
            masterScroll.getVerticalScrollBar().setUnitIncrement(16);
            masterScroll.setBorder(null);

            f.setContentPane(masterScroll);
            // f.setSize(1000, 800);
            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }

    /* =======================================================
       HELPER METHODS FOR THE NEW SECTIONS
       ======================================================= */

    private static JPanel buildSoftSkillsPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));   // vertical list
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1, true),  // Reduced from 2 to 1
                "  Essential Soft Skills  ",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 20),  // Reduced from 22 to 20
                ACCENT));

        String[] skills = {
                "Communication", "Presentation", "Teamwork", "Problem Solving",
                "Adaptability", "Time Management"
        };

        for (String s : skills) {
            JLabel row = new JLabel("  •  " + s);
            row.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Reduced from 18 to 16
            row.setForeground(TEXT);
            row.setOpaque(true);
            row.setBackground(new Color(40, 40, 55));
            row.setBorder(new EmptyBorder(10, 20, 10, 20));  // Reduced padding
            row.setAlignmentX(Component.CENTER_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
            p.add(row);
            p.add(Box.createVerticalStrut(6));  // Reduced gap from 8 to 6
        }
        return p;
    }

    private static JPanel buildChatbotPanel() {
        /* ======== inner classes copied from your console code ======== */
        // ❌ REMOVED: Message record and GeminiClient class (Now imported)

        /* ========= Swing panel ========= */
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1, true),  // Reduced from 2 to 1
                "  AI Mentor Chat  ",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 20), ACCENT));  // Reduced from 22 to 20

        /* chat history */
        // DS/DBMS data structures
        HashMap<String, String> faqMap = new HashMap<>(); // HashMap for quick replies
        faqMap.put("hello", "Hi there! How can I help you?");
        faqMap.put("bye", "Goodbye! Have a great day!");
        faqMap.put("what is ds", "Data Structures organize data for efficient access.");
        faqMap.put("what is dbms", "DBMS manages and stores structured data safely.");

        JTextArea chatArea = new JTextArea("Hi dear learner! I'm your AI mentor. How can I help?");
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setRows(10);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        chatArea.setBackground(new Color(40, 40, 55));
        chatArea.setForeground(TEXT);
        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Added inner padding
        p.add(scroll, BorderLayout.CENTER);

        /* bottom input bar */
        JTextField input = new JTextField("Type here…");
        JButton sendBtn = new JButton("Send");
        input.setForeground(Color.BLACK);
        input.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (input.getText().equals("Type here…")) input.setText("");
                input.setForeground(Color.BLACK);
            }
            public void focusLost(FocusEvent e) {
                if (input.getText().isEmpty()) {
                    input.setText("Type here…");
                    input.setForeground(Color.GRAY);
                }
            }
        });

        /* Gemini instance */
        GeminiClient gemini = new GeminiClient(); // Uses the imported client

        /* send action */
        Runnable send = () -> {
            String user = input.getText().trim();
            if (user.isEmpty() || user.equals("Type here…")) return;

            // disable UI while working
            input.setEnabled(false);
            sendBtn.setEnabled(false);

            // append user message
            chatArea.append("\nYou: " + user);

            /* heavy work off the EDT */
            new SwingWorker<String, Void>() {
                @Override protected String doInBackground() {
                    return gemini.sendMessage(user);
                }
                @Override protected void done() {
                    try {
                        String reply = get();
                        chatArea.append("\nMentee: " + reply);
                        chatArea.setCaretPosition(chatArea.getDocument().getLength());

                    } catch (Exception ex) {
                        chatArea.append("\nMentee: Error - " + ex.getMessage());
                    } finally {
                        input.setEnabled(true);
                        sendBtn.setEnabled(true);
                        input.setText("");
                        input.requestFocusInWindow();
                    }
                }
            }.execute();
        };

        sendBtn.addActionListener(e -> send.run());
        input.addActionListener(e -> send.run());   // ENTER key

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));  // Added top padding
        south.add(input, BorderLayout.CENTER);
        south.add(sendBtn, BorderLayout.EAST);
        p.add(south, BorderLayout.SOUTH);
        //p.setMaximumSize(new Dimension(1450, Integer.MAX_VALUE));


        return p;
    }

    private static JPanel buildMentorsPanel() {
        JPanel p = new JPanel(new GridLayout(0, 3, 20, 20));  // Reduced gaps from 20 to 15
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ACCENT, 1, true),  // Reduced from 2 to 1
                "  Meet Our Mentors  ",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 20),  // Reduced from 22 to 20
                ACCENT));

        class Mentor { // Kept as nested class as it's small data specific to this panel
            String name, role, url;
            Mentor(String name, String role, String url) {
                this.name = name; this.role = role; this.url=url;
            }
        }
        Mentor[] mentors = {
                new Mentor("Ankur Patel", "Java developer", "https://www.linkedin.com/in/ankur-patel-6b2340275/"),
                new Mentor("Hardik shah", "AI specialist", "https://www.linkedin.com/in/erhardikshah/"),
                new Mentor("Mosam Pandya","AI/ML","https://www.linkedin.com/in/mosampandya/"),
                new Mentor("Jinal Zala","DSA java","https://www.linkedin.com/in/jinal-zala-a1479a1a5/"),
                new Mentor("Dr.Kamaldeep bhatia","Internet of Things","https://www.linkedin.com/in/dr-kamaldeep-bhatia-05482930b/"),
                new Mentor("Nimesh Das","python developer","https://www.linkedin.com/in/nimish-das-23489375/")

        };


        for (Mentor m : mentors) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(new Color(40, 40, 55));
            card.setBorder(new EmptyBorder(15, 15, 15, 15));  // Reduced from 15


            // Use text-based icon instead of missing image file
            ImageIcon icon = new ImageIcon(CareerGuidance.class.getResource("/resources/link.jpeg"));
            Image scaled = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaled);

            JLabel name = new JLabel( m.name,scaledIcon, JLabel.CENTER);


            name.setFont(new Font("Segoe UI", Font.BOLD, 16));
            name.setForeground(ACCENT);
            card.add(name, BorderLayout.CENTER);

            JLabel role = new JLabel(m.role, SwingConstants.CENTER);
            role.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            role.setForeground(TEXT);
            card.add(role, BorderLayout.SOUTH);

            card.setMaximumSize(new Dimension(200, 180));  // Reduced from 200,180
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    card.setBackground(new Color(55, 55, 75));
                }
                public void mouseExited(MouseEvent e) {
                    card.setBackground(new Color(40, 40, 55));
                }
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI(m.url));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            });
            p.add(card);
        }
        return p;
    }
}