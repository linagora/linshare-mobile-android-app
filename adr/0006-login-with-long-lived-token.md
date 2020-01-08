# 6. Login with long lived token

Date: 2019-12-17

## Status

Accepted

## Context

- With mobile application, interaction with server base using REST API, in order for REST request to be accepted by LinShare server, they need to be authenticated.
- One of the most common questions we get here - when talking about token authentication for mobile devices, is about token expiration. But how long should I allow my access tokens to exist before expiring them? I don’t want to force my users to re-authenticate every hour.
- In order to supply a convenient user experience, we need to store a way to authenticate requests to the LinShare server.
- Using directly authentication (and thus storing directly credentials) is discouraged, because of possible disclosure, and vulnerability to isolate a given device.
- LinShare allow the use of `Long lived Token`. They can be revoked without changing credentials. They allows finger per device permission setting, and per-device activity review. Credential don't need to be stored (only the token does) which means user credentials cannot be leaked.

## Decision

- For all that reason we using approach to keep the token and renew when need, that is the way to use long lived token authentication

## Consequences

- Long lived token have to be stored.
- The are still have leak the token if the device is lost or taken by hacking issue, but for current situation we chosen the long lived token to avoid leak credential information. This will be concern and improve later. Maybe 2 layers security verification or one time password is better.