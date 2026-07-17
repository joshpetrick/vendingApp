# SnackLedger

SnackLedger is a Spring Boot application for office snack accounts. The server is the system of record for users,
balances, items, purchases, deposits, administrative pages, and display-only kiosk/TV views.

The kiosk is **not** a separate desktop application. A kiosk operator signs in to the server as the read-only `kiosk`
user and leaves the `/kiosk` page running on the display. The page rotates through current account balances and item prices in two stacked six-card grids.

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

Log in with the kiosk user. The display is non-interactive. The top balance container shows six users at a time, and the snack container shows six snacks at a time. Each container rotates independently using configurable timers.

## Kiosk API

The legacy versioned kiosk API remains rooted at `/api/v1/kiosk` for future integrations, but purchase/scanner behavior
is not exposed through a separate desktop client in this iteration.


## Demo seed data

By default local development seeds 50 test users and demo snacks. Test users use the same username and password, such as `alex` / `alex`, and balances are generated in $0.25 increments with both positive and negative examples.

Seed options and kiosk display timers can be changed in configuration:

```yaml
snackledger:
  seed:
    test-users: true
    demo-snacks: true
  kiosk-display:
    cards-per-page: 6
    balance-page-seconds: 5
    snack-page-seconds: 5
```

Demo soda is priced at $0.75, energy drinks at $2.25, and chips, granola bars, breakfast sandwiches, pizza rolls, and similar snacks at $0.75.
