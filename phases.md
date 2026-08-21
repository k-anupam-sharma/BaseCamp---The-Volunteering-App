# Implementation Phases

## Phase 1: Foundation & The UI Engine
*   Initialize Android Studio project with Compose and configure Gradle (Hilt, Supabase SDK, Navigation).
*   Build the Neo-Brutalism Compose UI Engine: Define custom Modifiers for hard shadows, thick borders, and typography.
*   Create core reusable components: `BrutalistButton`, `BrutalistCard`, `BrutalistTextField`.

## Phase 2: Auth & Routing
*   Set up Supabase Auth.
*   Implement `LoginScreen` and `SignupScreen` using the Brutalist components.
*   Implement Role Selection logic (Volunteer vs. Organization).
*   Set up the Jetpack Navigation graph to route authenticated users to their respective dashboards.

## Phase 3: Core Event Engine (BaseCamp Feed)
*   Create Data classes and Repositories for "Events".
*   Build Volunteer UI for the Home Feed using `LazyColumn` and `BrutalistCard`.
*   Build Organization UI to Create events via a high-contrast brutalist form.
*   Implement RSVP functionality.

## Phase 4: Gamification & Profiles
*   Build the Volunteer Profile screen featuring large, bold typography for "Total Hours".
*   Implement a grid layout for vibrant, high-contrast badges.
*   Build the Organization Profile UI.

## Phase 5: Hardware & Polish
*   Integrate Compose Barcode Scanner for Orgs to verify RSVPs.
*   Implement Push Notifications.