# Design System (Neo-Brutalism)

## Theme Concept
"Unapologetic Action" - Bold, raw, and high-energy. High contrast, distinct borders, and solid shadows to make volunteering feel active and gamified. 

## Compose Color Palette
*   **Primary:** Electric Yellow (`#FAFF00`) or Bright Cyan (`#00E5FF`) - For main action buttons (RSVP, Create).
*   **Secondary:** Hot Pink (`#FF007F`) - For badges, tags, and gamified alerts.
*   **Background:** Off-White/Beige (`#F4F4F0`) to make the colors pop.
*   **Surface:** Pure White (`#FFFFFF`) for cards.
*   **Borders/Text:** Pitch Black (`#000000`) - The anchor of the brutalist look.

## Typography
*   **Font Family:** Geometric and bold (e.g., Space Grotesk, Syne, or Roboto Black).
*   **Headlines:** Extra bold, ALL CAPS, pitch black, tight letter spacing.
*   **Body:** Medium weight, highly readable sans-serif, high contrast.

## UI Components
*   **Borders:** All buttons, cards, and text fields must have `BorderStroke(2.dp, Color.Black)`.
*   **Shadows:** Zero blur. Solid black offset shadows using a custom Compose modifier (e.g., `offset(4.dp, 4.dp)` layered behind the component).
*   **Corners:** Sharp `RectangleShape` or very slight `RoundedCornerShape(4.dp)`.