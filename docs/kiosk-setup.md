# Kiosk Display Setup

SnackLedger uses a server-hosted read-only kiosk display instead of a separate kiosk desktop application.

1. Start `snackledger-server`.
2. Open `/kiosk` in a browser on the display machine.
3. Log in as the `kiosk` user.
4. Leave the page running full screen.

The page refreshes every 60 seconds and scrolls through account balances and item prices. The kiosk user has read-only
access to display pages and should not be used for administrative work.


## Rotation settings

The kiosk display uses two stacked containers. The balance container and snack container each show six cards per page by default. Admins can change the timers with:

```yaml
snackledger:
  kiosk-display:
    cards-per-page: 6
    balance-page-seconds: 5
    snack-page-seconds: 5
```


Snack cards can include an image. Demo snacks use bundled SVG images, and snack image URLs are capped at 500 characters while the kiosk display constrains rendered images to a maximum on-screen size.
