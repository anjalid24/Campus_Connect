package com.ljuniversity.companion;

import com.ljuniversity.companion.ui.DesktopWorkspace;
import com.ljuniversity.companion.ui.LoginPageSwing;
import com.ljuniversity.companion.util.AppConfig;
import com.ljuniversity.companion.util.PlannerStore;
import javax.swing.*;
import java.util.Arrays;

/** Default launch is a genuine offline Swing workspace, not a web substitute. */
public final class Main {
    public static void main(String[] args) {
        boolean legacy = Arrays.asList(args).contains("--legacy-db");
        if (legacy) {
            try { AppConfig.requireDatabase(); }
            catch (IllegalStateException e) { System.err.println(e.getMessage()); return; }
        }
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
            catch (Exception ignored) { }
            if (legacy) new LoginPageSwing().setVisible(true);
            else new DesktopWorkspace(new PlannerStore(PlannerStore.defaultPath())).setVisible(true);
        });
    }
}
