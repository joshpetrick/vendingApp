package com.company.snackledger.kiosk;

import com.company.snackledger.kiosk.config.KioskConfig;
import com.company.snackledger.kiosk.ui.KioskFrame;
import javax.swing.SwingUtilities;

public class KioskApplication {
    public static void main(String[] args) throws Exception {
        var config = KioskConfig.load();
        SwingUtilities.invokeLater(() -> new KioskFrame(config).setVisible(true));
    }
}
