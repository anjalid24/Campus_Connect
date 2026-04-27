//runs the application
package com.ljuniversity.companion;

import com.ljuniversity.companion.ui.HomePageSwing;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Directly launch homepage (bypassing login for testing)
        SwingUtilities.invokeLater(() -> {
            HomePageSwing.show();
        });
    }
}