# System Architecture

## Tech Stack
*   **IDE:** Android Studio
*   **Language:** Kotlin
*   **UI Toolkit:** Jetpack Compose (Modern declarative UI)
*   **Design System:** Custom Neo-Brutalism Framework (bypassing standard Material 3 shadows/borders)
*   **Architecture Pattern:** MVVM (Model-View-ViewModel) with Clean Architecture principles
*   **Backend / Database:** Supabase (Kotlin SDK) for PostgreSQL, Auth, and Storage
*   **Dependency Injection:** Hilt (Dagger)
*   **Asynchronous Programming:** Kotlin Coroutines & Flow (StateFlow for UI state)
*   **Navigation:** Jetpack Navigation Compose

## App Flow
1. **Splash Screen:** Bold logo and high-contrast loading state.
2. **Auth Flow:** Login / Sign Up -> Role Selection (Volunteer vs. Organization).
3. **Volunteer Flow:** Home (Event Feed) -> Search/Filter -> Event Details -> RSVP -> Profile (Badges/Hours).
4. **Organization Flow:** Dashboard (Active Events) -> Create Event -> Manage Attendees -> Scan QR.

## Folder Structure
/app/src/main/java/com/basecamp/app
  /data              # Repositories, Supabase clients
  /domain            # Core business logic
  /presentation      # UI layer
    /theme           # BrutalistColors, Typography, custom Modifiers
    /components      # BrutalistButton, BrutalistCard, etc.
    /screens         # Full screens (Auth, Home, Profile)
    /navigation      # NavHost and Routes
  /di                # Hilt modules