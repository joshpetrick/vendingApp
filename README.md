# SnackLedger

SnackLedger is split into two independently deployable Java 17 applications:

* `snackledger-server` — Spring Boot system of record for users, balances, items, purchases, deposits, admin pages, the TV dashboard, and kiosk REST APIs.
* `snackledger-kiosk` — Swing desktop kiosk client that talks to the server only through secured REST APIs.

## Build

```bash
mvn clean verify
```

## Run server

```bash
mvn -pl snackledger-server spring-boot:run
```

The development seed data includes kiosk id `MAIN-OFFICE-KIOSK` with API key `dev-kiosk-key-change-me`.

## Run kiosk

```bash
mvn -pl snackledger-kiosk package
SNACKLEDGER_KIOSK_API_KEY=dev-kiosk-key-change-me java -jar snackledger-kiosk/target/snackledger-kiosk.jar
```

## Kiosk API

The versioned kiosk API is rooted at `/api/v1/kiosk` and supports health, configuration, badge lookup, user search, barcode lookup, item search, and idempotent purchase submission.
