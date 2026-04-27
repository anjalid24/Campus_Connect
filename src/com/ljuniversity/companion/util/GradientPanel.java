package com.ljuniversity.companion.util;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private final Color c1, c2;

    public GradientPanel(Color c1, Color c2) {
        this.c1 = c1;
        this.c2 = c2;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}