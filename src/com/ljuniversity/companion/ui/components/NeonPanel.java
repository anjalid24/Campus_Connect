package com.ljuniversity.companion.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Neon Glowing Panel - Reusable UI Component.
 * Extracted from EventsLeaderboardUI.java.
 */
public class NeonPanel extends JPanel {
    private int radius = 15;
    private Color backgroundColor = new Color(15, 20, 30, 200);
    private boolean hasGlow = false;
    private Color glowColor = new Color(60, 140, 255); // electric blue
    private boolean isHovered = false;

    public NeonPanel() {
        setOpaque(false);
    }

    public NeonPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    public void setGlowEnabled(boolean enabled) {
        this.hasGlow = enabled;
    }

    public void setGlowColor(Color color) {
        this.glowColor = color;
    }

    public void setHovered(boolean hovered) {
        this.isHovered = hovered;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);

        // Add glow effect
        if (hasGlow || isHovered) {
            Color glow = isHovered ? new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 80) :
                    new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 40);
            g2d.setColor(glow);
            g2d.fill(new RoundRectangle2D.Float(-3, -3, getWidth() + 6, getHeight() + 6, radius + 3, radius + 3));
        }

        // Fill background
        g2d.setColor(backgroundColor);
        g2d.fill(roundedRectangle);

        // Add neon border
        if (hasGlow || isHovered) {
            GradientPaint borderGradient = new GradientPaint(
                    0, 0, new Color(60, 140, 255),
                    getWidth(), getHeight(), new Color(140, 200, 255)
            );
            g2d.setPaint(borderGradient);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(roundedRectangle);
        }

        g2d.dispose();
        super.paintComponent(g);
    }
}