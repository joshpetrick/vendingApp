# SnackLedger Kiosk API

All kiosk endpoints are under `/api/v1/kiosk`. Authenticated endpoints require `X-Kiosk-Id` and `X-Kiosk-Api-Key` headers. Purchase requests are idempotent by `(kioskId, requestId)`.
