# Stage 2 — Authentication & Security Audit

Branch: `fix/stage-2-auth-security`

This document records the pre-change security inventory. No authentication behavior was changed as part of this audit. The purpose is to avoid locking existing users out or silently breaking sync, teams, payments, and admin workflows.

## Executive result

**Current state is not safe for production.** The backend does not currently issue or validate access/refresh tokens. The existing OTP implementation is process-local and is not connected to a user/session lifecycle. Several write/read APIs trust client-supplied ownership identifiers.

## Authentication and session flow

### Backend
- `backend/auth.py` exposes only `POST /api/v1/auth/send-otp` and `POST /api/v1/auth/verify-otp`.
- OTPs are stored in a module-level Python dictionary, so they are lost on restart and are not shared across workers.
- OTP verification returns a success marker and phone number only; it does not create a server session or issue tokens.
- The Android client declares calls to `/auth/login` and `/auth/signup`, but those routes are absent from the inspected backend auth router.
- No refresh or logout endpoint exists.
- `backend/main.py` registers routers inside broad exception handlers; a router import failure can leave the service running with missing endpoints.

### Android
- `App.kt` contains a fixed OTP constant and a local password hash helper based on SHA-256.
- `App.onCreate()` calls local database methods that seed an admin and AI configuration.
- Local session persistence stores a user ID in preferences; it is not a server-issued credential.
- `isAdmin(phone)` determines admin UI visibility from a client-side phone-number comparison. This must become display-only and must never authorize API operations.
- `NetworkModule.kt` logs request/response bodies and emits request diagnostics. This can expose OTPs, credentials, profile data, and tokens.
- `AndroidManifest.xml` currently allows cleartext traffic.
- `app/build.gradle.kts` reads an OpenRouter key from local properties and embeds it in BuildConfig. Anything embedded in an APK must be considered public.

## Privileged and ownership-sensitive API inventory

### Admin
- `GET /api/v1/admin/users-stats`: no authentication or role dependency is present in the route.
- The admin router currently exposes a database-backed statistics endpoint without server-side authorization.

### Sync
- `POST /api/v1/sync/entries`
- `POST /api/v1/sync/people`
- `POST /api/v1/sync/slips`
- `POST /api/v1/sync/ai-messages`
- `POST /api/v1/sync/ai-models`
- `POST /api/v1/sync/support-messages`
- `POST /api/v1/sync/banners`
- `POST /api/v1/sync/announcements`
- `POST /api/v1/sync/export-logs`
- `POST /api/v1/sync/tokens`
- `GET /api/v1/sync/people/{user_id}`
- `GET /api/v1/sync/entries/{user_id}`
- `GET /api/v1/sync/support-messages/{user_id}`

No router-wide authentication dependency is present. Several write payloads accept `user_id` from the client, and updates are keyed by globally supplied record IDs. AI model configuration writes include an API-key field. Admin-only and user-owned data are mixed in the same router and require explicit permission separation.

### Teams
- `POST /api/v1/users/check`
- `POST /api/v1/teams`
- `GET /api/v1/teams/user/{user_id}`
- `GET /api/v1/teams/{team_id}`
- `POST /api/v1/teams/{team_id}/members`
- `DELETE /api/v1/teams/{team_id}/members/{user_id}`
- `PUT /api/v1/teams/{team_id}`
- `DELETE /api/v1/teams/{team_id}`
- `GET /api/v1/teams/{team_id}/messages`
- `POST /api/v1/teams/{team_id}/messages`

The router accepts identity hints such as `X-User-Id` and/or path/body user IDs. These are not proof of identity. Team membership and creator/owner checks must be performed against the authenticated principal on every operation.

### WebSocket
- `/ws/chat` currently accepts connections without authenticating a token and broadcasts received messages to all connected sockets. This is a cross-user data exposure risk and needs a deliberate per-conversation authorization model, not merely a token check.

## Password and administrator provisioning

- `backend/config.py` contains a fixed default administrator password and identity data.
- `backend/create_admin.py` and `backend/seed_admin.py` contain hard-coded administrator credentials and use salted SHA-256 rather than a password-hashing function designed for passwords.
- The Android local database startup path calls `ensureAdmin()`; a client-side seed must not create or grant server privileges.
- Existing `users` records contain legacy `salt` and `password_hash` columns. Migration must support safe password-hash upgrades without silently invalidating legitimate accounts.

## AI key exposure

- Android BuildConfig embeds an OpenRouter key loaded from local properties.
- Android API DTOs include user AI API key fields.
- Sync accepts AI model records containing an API key.
- Server-owned provider credentials must be stored only on the backend and must never be serialized into user-facing profile responses or Android sync payloads. User-supplied provider keys, if supported, need a separate encrypted-at-rest design and explicit ownership rules.

## Required implementation sequence

1. Freeze a compatibility contract for current Android login/signup/forgot-password screens and existing user records.
2. Add database-backed OTP challenges and revocation-capable sessions; use hashed OTP/session secrets, one-time consumption, expiration, per-phone/IP limits, and a pluggable SMS provider.
3. Implement password hashing with Argon2id, login/signup/password reset/change, access-token verification, refresh rotation, logout/revocation, and server-owned roles.
4. Add a shared `current_user` dependency and role/permission checks. Derive owner IDs from the authenticated principal; reject mismatched client IDs.
5. Split user sync from admin configuration operations. Keep payments and entitlement mutation server-controlled.
6. Authenticate WebSockets and authorize each subscription/message against conversation/team membership.
7. Remove Android OTP/password/admin trust shortcuts and migrate token storage to Android Keystore-backed encrypted storage. Disable body logging and cleartext traffic in release.
8. Add negative tests for forged, expired, replayed, revoked, cross-user, and non-admin access.

## Release blockers

- The SMS provider account, sender line, and approved OTP pattern must be configured as deployment secrets. No real secret belongs in this repository.
- Existing deployed user schema/data and current production login behavior must be verified before applying schema changes.
- A secure server-side role assignment policy is needed for the existing administrator. The phone number or client UI must not self-assign a role.
- The Android API contract must be migrated together with backend endpoints; shipping only one side would lock users out.
- The current Stage 1 Android CI build is failing on pre-existing unresolved references in `Admin.kt` and `MainActivity.kt`; those unrelated app features must be repaired in their owning stage before a green Android release build can be claimed.

## Audit decision

Do not deploy or merge an authentication refactor until the backend and Android clients have been migrated together and the negative security tests pass. This audit intentionally does not claim that any of the above risks have been fixed.
