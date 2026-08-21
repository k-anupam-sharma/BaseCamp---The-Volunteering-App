# AI Coding Guidelines & Rules (Android/Kotlin)

## General Rules
*   Use **Kotlin** and **Jetpack Compose** exclusively. 
*   Strictly follow the **MVVM** architecture pattern with unidirectional data flow (UDF).
*   Keep ViewModels independent of the Android Framework (no `Context` in ViewModels).

## UI/UX (Neo-Brutalism) Rules
*   **DO NOT** use default Material Design shadows (`elevation`) or soft rounded corners.
*   **ALWAYS** apply a thick `BorderStroke(2.dp, Color.Black)` to interactive components.
*   **ALWAYS** use hard, solid black drop shadows (offset x = 4.dp, y = 4.dp) via custom Compose Modifiers for buttons and cards.
*   Use high-contrast colors (Electric Yellow, Hot Pink) exclusively for primary actions and badges.

## Database & Async Rules
*   Use the **Supabase Kotlin SDK** (`supabase-kt`) for Auth and DB.
*   Use **Kotlin Coroutines** and `viewModelScope` for all asynchronous work. 
*   Wrap responses in a sealed `Result` class (`Success`, `Error`, `Loading`).