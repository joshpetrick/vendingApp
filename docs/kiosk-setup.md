# Kiosk Display Setup

SnackLedger uses a server-hosted read-only kiosk display instead of a separate kiosk desktop application.

1. Start `snackledger-server`.
2. Open `/kiosk` in a browser on the display machine.
3. Log in as the `kiosk` user.
4. Leave the page running full screen.

The page refreshes every 60 seconds and scrolls through account balances and item prices. The kiosk user has read-only
access to display pages and should not be used for administrative work.
