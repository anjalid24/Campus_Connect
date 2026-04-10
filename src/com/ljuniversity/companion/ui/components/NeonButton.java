package com.ljuniversity.companion.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Neon Glowing Button - Reusable UI Component.
 * Extracted from EventsLeaderboardUI.java.
 */
public class NeonButton extends JButton {
    // Blue gradient colors
    private Color color1 = new Color(50, 120, 255);
    private Color color2 = new Color(120, 200, 255);
    private boolean isHovered = false;
    private boolean isPressed = false;

    public NeonButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText(getText());

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                isPressed = true;
                repaint();
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                isPressed = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create gradient
        GradientPaint gradient = new GradientPaint(
                0, 0, isHovered ? color2 : color1,
                getWidth(), getHeight(), isHovered ? color1 : color2
        );
        g2d.setPaint(gradient);

        // Draw rounded rectangle
        int offset = isPressed ? 2 : 0;
        RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(offset, offset, getWidth() - offset * 2, getHeight() - offset * 2, 15, 15);
        g2d.fill(roundedRectangle);

        // Add inner glow
        if (isHovered) {
            g2d.setColor(new Color(255, 255, 255, 25));
            g2d.fill(roundedRectangle);
        }

        // Draw glowing text
        g2d.setColor(Color.WHITE);
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2 + offset;
        int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 + offset;

        // Add text glow
        if (isHovered) {
            g2d.setColor(new Color(200, 220, 255, 100));
            g2d.drawString(getText(), textX + 1, textY + 1);
        }
        g2d.setColor(Color.WHITE);
        g2d.drawString(getText(), textX, textY);

        g2d.dispose();
    }
}