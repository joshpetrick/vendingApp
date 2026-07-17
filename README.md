# SnackLedger

SnackLedger is a Spring Boot application for office snack accounts. The server is the system of record for users,
balances, items, purchases, deposits, administrative pages, and display-only kiosk/TV views.

The kiosk is **not** a separate desktop application. A kiosk operator signs in to the server as the read-only `kiosk`
user and leaves the `/kiosk` page running on the display. The page periodically refreshes and scrolls through current
account balances and item prices.

## Build

```bash
mvn clean verify
```

## Run server

```bash
mvn -pl snackledger-server spring-boot:run
```

Development users:

* `admin` / `admin-change-me` — administrative role.
* `kiosk` / `kiosk-change-me` — read-only kiosk display role.

Change these credentials before production use.

## Read-only kiosk display

After starting the server, open:

```text
http://localhost:8080/kiosk
```

Log in with the kiosk user. The display is non-interactive, refreshes every 60 seconds, lists user account balances,
and scrolls through current item prices.

## Kiosk API

The legacy versioned kiosk API remains rooted at `/api/v1/kiosk` for future integrations, but purchase/scanner behavior
is not exposed through a separate desktop client in this iteration.
