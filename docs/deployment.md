# Deployment

Deploy `snackledger-server` to a network-accessible host and configure HTTPS for production.

Display machines should use a browser pointed at `/kiosk` and authenticate as the read-only kiosk user. Change the
development passwords before production use.


## Demo seed options

Local/demo deployments can seed 50 test users and a demo snack catalog. Disable this for production by setting:

```yaml
snackledger:
  seed:
    test-users: false
    demo-snacks: false
```

## Snack images

Snack records can reference an image URL with a 500-character maximum. The kiosk display constrains rendered snack images
so oversized admin-provided images do not dominate the display. Demo data uses bundled SVG images under
`/images/snacks/`.
