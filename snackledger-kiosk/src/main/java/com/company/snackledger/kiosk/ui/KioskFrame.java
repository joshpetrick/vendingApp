package com.company.snackledger.kiosk.ui;

import com.company.snackledger.kiosk.api.Dto.PurchaseItem;
import com.company.snackledger.kiosk.api.Dto.PurchaseRequest;
import com.company.snackledger.kiosk.api.Dto.UserDto;
import com.company.snackledger.kiosk.api.KioskApiClient;
import com.company.snackledger.kiosk.config.KioskConfig;
import java.awt.BorderLayout;
import java.awt.Font;
import java.net.http.HttpTimeoutException;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class KioskFrame extends JFrame {
    private final KioskApiClient api;
    private final KioskConfig config;
    private final Cart cart = new Cart();
    private final JTextField scanner = new JTextField();
    private final JTextArea status = new JTextArea();
    private UserDto user;

    public KioskFrame(KioskConfig config) {
        super("SnackLedger Kiosk");
        this.config = config;
        this.api = new KioskApiClient(config);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        add(new JLabel("Scan Badge or Select Your Name", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JScrollPane(status), BorderLayout.CENTER);
        add(scanner, BorderLayout.SOUTH);
        add(createButtonPanel(), BorderLayout.EAST);

        scanner.addActionListener(event -> scan(scanner.getText().trim()));
        if (config.fullscreen()) {
            setExtendedState(MAXIMIZED_BOTH);
        }

        health();
        focusScanner();
    }

    private JPanel createButtonPanel() {
        var buttons = new JPanel();
        for (String label : List.of("Select User", "Finish Purchase", "Clear Cart", "Cancel", "Retry Connection")) {
            var button = new JButton(label);
            button.setFont(button.getFont().deriveFont(Font.PLAIN, 22f));
            buttons.add(button);
            wireButton(label, button);
        }
        return buttons;
    }

    private void wireButton(String label, JButton button) {
        switch (label) {
            case "Finish Purchase" -> button.addActionListener(event -> finish());
            case "Clear Cart" -> button.addActionListener(event -> {
                cart.clearForNewPurchase();
                refresh("Cart cleared");
            });
            case "Cancel" -> button.addActionListener(event -> reset());
            case "Retry Connection" -> button.addActionListener(event -> health());
            case "Select User" -> button.addActionListener(event -> manualUser());
            default -> throw new IllegalArgumentException("Unsupported kiosk button: " + label);
        }
    }

    private void focusScanner() {
        scanner.setText("");
        scanner.requestFocusInWindow();
    }

    private void health() {
        try {
            refresh("Server " + api.health().status());
        } catch (Exception exception) {
            refresh("Unable to connect to server: " + exception.getMessage());
        }
    }

    private void scan(String value) {
        try {
            if (user == null) {
                user = api.badge(value);
                refresh("Selected " + user.displayName() + " balance " + user.balance());
            } else {
                cart.add(api.barcode(value));
                refresh("Added item. Total: " + cart.total());
            }
        } catch (Exception exception) {
            refresh("Scan error: " + exception.getMessage());
        } finally {
            focusScanner();
        }
    }

    private void manualUser() {
        String query = JOptionPane.showInputDialog(this, "Search user");
        if (query == null || query.isBlank()) {
            return;
        }

        try {
            var matches = api.users(query);
            if (matches.isEmpty()) {
                refresh("No users found for: " + query);
                return;
            }
            user = matches.get(0);
            refresh("Selected " + user.displayName());
        } catch (Exception exception) {
            refresh("User search failed: " + exception.getMessage());
        }
    }

    private void finish() {
        if (user == null || cart.lines().isEmpty()) {
            refresh("Select a user and scan items first");
            return;
        }

        try {
            var request = new PurchaseRequest(
                    cart.requestId(),
                    config.kioskId(),
                    user.id(),
                    cart.lines().stream()
                            .map(line -> new PurchaseItem(line.item().id(), line.quantity()))
                            .toList());
            var response = api.purchase(request);
            refresh("Purchase completed. Total " + response.purchaseTotal()
                    + " previous " + response.previousBalance()
                    + " new " + response.newBalance());
            var resetTimer = new Timer(config.resetSeconds() * 1000, event -> reset());
            resetTimer.setRepeats(false);
            resetTimer.start();
        } catch (HttpTimeoutException exception) {
            refresh("Purchase status unknown. Keep cart and retry with same request ID: " + cart.requestId());
        } catch (Exception exception) {
            refresh("Purchase failed: " + exception.getMessage());
        }
    }

    private void reset() {
        user = null;
        cart.clearForNewPurchase();
        refresh("Ready for next user");
        focusScanner();
    }

    private void refresh(String message) {
        status.append(new Date() + " - " + message + System.lineSeparator());
    }
}
