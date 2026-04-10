package com.ljuniversity.companion.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.RadialGradientPaint;



/**
 * Futuristic Neon Gradient Panel - Reusable UI Component for background.
 * Extracted from EventsLeaderboardUI.java.
 */
public class NeonGradientPanel extends JPanel {
    // Blue-black theme
    private Color color1 = new Color(8, 10, 18);   // near black
    private Color color2 = new Color(15, 20, 35);  // deep navy
    private Color color3 = new Color(25, 35, 55);  // dark blue

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Create deep navy to blue gradient background
        GradientPaint gradient1 = new GradientPaint(
                0, 0, color1,
                getWidth(), getHeight(), color2
        );
        g2d.setPaint(gradient1);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add blue glow effects
        RadialGradientPaint blueGlow1 = new RadialGradientPaint(
                getWidth() * 0.25f, getHeight() * 0.2f, getWidth() * 0.7f,
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(40, 120, 255, 60), new Color(40, 120, 255, 0)}
        );
        g2d.setPaint(blueGlow1);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        RadialGradientPaint blueGlow2 = new RadialGradientPaint(
                getWidth() * 0.75f, getHeight() * 0.8f, getWidth() * 0.6f,
                new float[]{0.0f, 1.0f},
                new Color[]{new Color(80, 180, 255, 40), new Color(80, 180, 255, 0)}
        );
        g2d.setPaint(blueGlow2);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Add subtle grid pattern for futuristic feel
        g2d.setColor(new Color(255, 255, 255, 8));
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < getWidth(); i += 30) {
            g2d.drawLine(i, 0, i, getHeight());
        }
        for (int j = 0; j < getHeight(); j += 30) {
            g2d.drawLine(0, j, getWidth(), j);
        }

        g2d.dispose();
    }
}