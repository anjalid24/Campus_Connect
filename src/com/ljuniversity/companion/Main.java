//runs the application
package com.ljuniversity.companion;

import com.ljuniversity.companion.ui.LoginPageSwing;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Use invokeLater to ensure all UI operations run on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginPageSwing().setVisible(true);
        });
    }
}