# Auth

## At a glance

| | |
|---|---|
| **Status** | ✅ Implemented |
| **Platforms** | Android, iOS, Desktop, Web |
| **Entry point** | From Splash when there is no valid session; after logout |
| **Purpose** | Let an invited employee sign in with Google, or let anyone explore the question bank as a guest |

## Product value

Auth is where a person chooses how they enter Headway. Two doors:

* **Sign in (Google).** Headway is a corporate app, so the primary path is Google sign-in for an **invited Innowise employee**. This unlocks the full experience: home, progress tracking, and preparation sessions with a mentor/manager.
* **Learn As A Guest.** Anyone — including people outside the company or not yet invited — can browse interview questions for a technology to self-prepare, **without** the registered features (no mentor sessions, no progress tracking).

The screen also sets expectations up front: a hint shows which email domains are allowed, so a user understands *why* sign-in might be refused.

## Experience & states

![Auth — mobile](assets/auth-mobile.png)

_(The middle "Select your department" frame is struck through in the design — it is **not** part of the current product.)_

**Welcome (mobile)** _(left frame)_ — full-bleed cream canvas with a subtle wave pattern:

* The brown four-dot **Headway logo** mark at the top.
* Title **"Welcome to Headway!"** where **"Headway!"** is color-highlighted; it fades in. Subtitle **"Your pocket assistant for interview preparation 🍃"** slides in after the title.
* A friendly character illustration (green blob mascot surrounded by small skill/feature icons) fills the middle on phones tall enough to fit it.
* Two stacked actions at the bottom:
  * **"Learn As A Guest"** — outlined/secondary button (transparent container, colored border + label).
  * **"Sign In"** — solid brown primary button with the Google icon, and a small supporting line under it: a warning icon + **"innowise.com only"**, signaling the allowed domain.

**Wide (Desktop / Web) layout:**

![Auth — wide](assets/auth-wide.png)

_(Left frame.)_ The welcome screen switches to a **side-by-side** layout: the character illustration occupies the left column; the right column centers the logo, the "Welcome to Headway!" title, subtitle, and the two action buttons. Same copy and behavior; only the arrangement adapts.

**States & behavior:**

* **Busy.** While sign-in or guest entry is in progress, both buttons are disabled (`isBusy`) to prevent double submission.
* **Error.** On a non-fatal failure (Google token couldn't be obtained, or login failed for a reason other than "not invited"), the screen shows the shared **error/network stub** with **"Try Again"** (see [Error & network stubs](error-network-stubs.md)); retry dismisses the error and returns to the form.
* **Routing on success:**
  * Successful Google sign-in → **Home** (splash replaced; no back to Auth).
  * Successful guest entry → **Learn questions**.
  * Google sign-in rejected because the user is **not invited** → **[No access](no-access.md)** screen.

## Functional requirements

* **FR-AUTH-1**: The screen MUST offer exactly two entry choices: **Sign In** (Google) and **Learn As A Guest**.
* **FR-AUTH-2**: Sign-in MUST authenticate via Google and, on success for an eligible invited user, route to **Home** and persist the session securely on the current device.
* **FR-AUTH-3**: Guest entry MUST start a guest session and route to **Learn questions**, with guest navigation restricted to guest-allowed experiences.
* **FR-AUTH-4**: If Google sign-in succeeds but the user is **not invited**, the app MUST route to the **No access** screen (not the generic error stub).
* **FR-AUTH-5**: Other failures (token unavailable/cancelled, network, dependency, validation, conflict, unexpected) MUST surface a recoverable failure state without exposing protected content.
* **FR-AUTH-6**: While an attempt is in flight, both actions MUST be disabled.
* **FR-AUTH-7**: The screen MUST display an **allowed-domain hint** near the Sign In action (today: "innowise.com only").
* **FR-AUTH-8**: The screen MUST provide an adaptive **wide** layout (side-by-side) distinct from the narrow stacked layout.
* **FR-AUTH-9**: Sign-in MUST be functional on all four targets (Android, iOS, Desktop, Web).

## Non-functional requirements

* **NFR-AUTH-1**: Tokens only (Freud); all copy in feature string resources.
* **NFR-AUTH-2**: Layout choice is driven by window width (no parallel boolean flag); illustration hides on very narrow heights/widths to avoid crowding.
* **NFR-AUTH-3**: Expected failures are modeled with typed `Outcome` + `SessionDomainError`, never raw exceptions.
* **NFR-AUTH-4**: Per-platform Google sign-in is wired via `expect/actual`; web/desktop use the same Web OAuth client id as the mobile `id_token` audience (see server auth notes / lessons-learned).

## Data & entities

Registered sign-in is validated against Supabase Postgres (server-owned schema). Relevant entities:

* **`public.users`** — one row per person known to the system. Key columns: `id` (UUID), `auth_user_id` (nullable), `google_subject` (nullable), `email` (unique), `full_name` (nullable), `department` (`ANDROID` | `IOS` | `CROSS_PLATFORM`), `role` (`DEVELOPER` | `MANAGER` | `MENTOR` | `EMPLOYEE` | `GUEST`), `status` (`INVITED` | `ACTIVE` | `REVOKED`), `avatar_url`, `is_active`, `created_at`, `updated_at`. Sign-in matches/creates the user by Google identity and requires an eligible (invited/active, active) account.
* **Email-domain allowlist** — enforced **in Postgres** (a `assert_allowed_domain_for_invites()`-style rule), not in app config. Disallowed domains are rejected as a validation error. Currently allowed: `innowise.com` (target) and `gmail.com` (testing). The long-term intent is **innowise.com only**.
* **Refresh sessions** — registered sessions are restorable; access tokens can be refreshed (server `refresh_sessions` table; client stores access + refresh tokens in the local session record).
* **`public.guest_session`** — guest sessions (`id`, `created_at`, `revoked_at`). Guest entry creates a guest session; the client stores a guest access token with an expiry.

**Environments.** Two Supabase projects exist — **dev** (`ymqpiogjzymkkwzutqds`) and **prod** (`lhdkfjlltgnxrttrqfla`); see [Supabase and Postgres](../../server/supabase.md). Schema is identical; data and allowlist contents differ per environment.

**Client local session record** (`LocalSessionRecord`, secure storage):

* `Registered` — `accessToken`, `refreshToken`, `userId`, `userEmail`, `userName`.
* `Guest` — `accessToken`, `expiresAtEpochMs`.

## Technical implementation

* **Module:** `client/feature/auth/{api,di,internal}` (the No access screen lives here too).
* **Composable:** `AuthScreen` → `AuthScreenContent` (`.../ui/screen/auth/AuthScreen.kt`). Adaptive layout via `BoxWithConstraints` + `rememberWindowSizeClass()` + `shouldUseAuthSideBySideLayout(...)`; narrow = `AuthScreenStackedContent`, wide = `AuthScreenWideContent`. Background `FreudBackgroundPattern.Waves`. Content wrapped in `FreudFallback(isError = state.hasError, onRetry = onDismissError)`.
* **State / logic:** `AuthStore` + `AuthStoreFactory` (MVIKotlin). `State(isBusy, hasError)`. Intents: `SignInWithGoogle`, `ContinueAsGuest`, `DismissError`.
  * `SignInWithGoogle` → `ObtainGoogleIdTokenUseCase` → `LoginWithGoogleUseCase`. Success → `ReplaceTopBy(HomeScreenKey)`. Failure `SessionDomainError.UserNotInvited` → `NavigateTo(AuthNoAccessScreenKey)`; other failures → `hasError = true`.
  * `ContinueAsGuest` → `LoginAsGuestUseCase`. Success → `ReplaceTopBy(LearnQuestionsScreenKey)`; failure → `hasError = true`.
* **Session domain:** `core/session` use cases + `AuthRepository`; errors modeled by `SessionDomainError` (`NetworkUnavailable`, `Unexpected`, `RegisteredSessionInvalid`, `GuestSessionInvalid`, `UserNotInvited`, `AuthorizationDenied`, `ValidationFailed`, `Conflict`, `DependencyUnavailable`, `LocalPersistenceFailed`, `GoogleSignInCancelled`, `GoogleSignInUnavailable`).
* **Server path:** client → **gateway** GraphQL `loginWithGoogle` → **auth** service (verifies Google id token, `aud` must equal the Web client id) → **database** service (`users`, allowlist, sessions).
* **Theme:** `AuthTheme` (background, greeting colors/highlight, Google button colors, guest button color, under-button warning color).
* **Copy:** `auth_greeting_text` "Welcome to ", `auth_substring_for_colorized_header` "Headway!", `auth_greeting_subtext`, `auth_learn_as_guest_button` "Learn As A Guest", `auth_sing_in_google_button` "Sign In", `auth_under_button_text` "innowise.com only".
* **Related spec:** `.ai/specs/86-client-auth-flow/` (full auth flow spec/plan/tasks).

## Open questions & planned work

* **Domain policy:** when does the allowlist tighten to **innowise.com only** (drop `gmail.com`)? The "innowise.com only" hint already implies the final state.
* Whether the allowed-domain hint should be **data-driven** (read from the backend allowlist) instead of a hard-coded string, so it stays accurate per environment.
* The struck-out **"Select your department"** step is explicitly **out of scope**; department is determined by the invite, not chosen at sign-in.
