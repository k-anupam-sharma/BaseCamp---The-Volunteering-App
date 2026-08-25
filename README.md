# BaseCamp: The Volunteering App ⛰️

BaseCamp is a high-energy, modern platform designed to bridge the gap between passionate **Volunteers** and community-driven **Organizations**. 

Built entirely with a stunning **Dynamic Glassmorphic** design language and inspired by industry-leading layout structures (like Meetup), BaseCamp combines immersive UI components layered over an animated hardware-accelerated space video background. With gamified profiles, instant QR-code ticketing, and real-time hardware scanning, BaseCamp makes community service engaging and frictionless.

---

## 🛠️ Why did we make it?

Currently in India, there is no dedicated platform for companies or organizations to find volunteers—nor is there a reliable platform for individuals to find local events where they can volunteer. 

The existing ecosystem relies heavily on:
1. **Word-of-mouth** and personal referrals.
2. **WhatsApp channels** that send out irregular, disorganized, and vague updates regarding volunteering events.

BaseCamp was created to fix this fragmentation. We provide a centralized, common platform for both parties. By making the onboarding process incredibly easy, fun, and highly engaging through our Glassmorphic design, BaseCamp aims to solve the volunteering disconnect in India—while building a scalable foundation for future monetization.

---

## 🚀 Key Features

### For Volunteers 🙋
1. **Discover**: Browse a real-time, auto-refreshing feed of local events categorized by cause using clean, horizontal scrolling pills (All Events, My RSVPs).
2. **Secure Ticketing**: RSVP with a single tap. The app generates a **Secure QR Code Ticket** that is blurred by default. Tapping it starts a 15-second interactive reveal timer.
3. **Two-Step Check-In**: Show your ticket to be scanned twice (Login and Logout). The app actively polls the server and instantly updates your status and displays your **total logged duration**.
4. **Interactive Profile**: Upload a profile photo directly to Supabase Storage, watch your "Total Hours" climb, and unlock achievement badges on your Volunteer Dossier.
5. **Interactive Chat**: Engage with other volunteers and organizers in the real-time event chat.

### For Organizations 🏢
1. **Event Management**: Create, edit details, delete, and dynamically increase capacity for community events using a premium, Meetup-inspired card layout.
2. **Hardware Scanning**: Access the built-in CameraX scanner to scan volunteer QR tickets at the door.
3. **Real-time Live Sync**: The "Volunteers" tab auto-refreshes every 10 seconds. When you scan a ticket, it instantly registers the volunteer's check-in/check-out time and automatically calculates their volunteering duration.
4. **Organizer Badges**: Chat directly with volunteers in the event thread—your messages are highlighted with an exclusive "ORGANIZER" badge.

---

## 💻 Tech Stack

BaseCamp is built using a modern, scalable Android architecture:

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Custom Glassmorphism Modifiers)
*   **Media**: [ExoPlayer (Media3)](https://developer.android.com/media/media3) for high-performance, hardware-accelerated looping video backgrounds.
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/)
*   **Routing**: Jetpack Navigation Compose
*   **Backend as a Service (BaaS)**: [Supabase](https://supabase.com/) (PostgreSQL, Authentication, and Storage) via the official Kotlin SDK.
*   **Hardware Integration**: [Android CameraX](https://developer.android.com/training/camerax) & [Zxing Core](https://github.com/zxing/zxing) for real-time QR generation and decoding.
*   **Push Notifications**: Firebase Cloud Messaging (FCM)

---

## 📊 System Flowcharts

### 1. High-Level Architecture
`mermaid
graph TD
    UI[Jetpack Compose UI] --> VM[ViewModels]
    VM --> Repo[Repository Layer]
    Repo --> Hilt[Hilt DI Container]
    
    Repo --> Supabase[(Supabase Backend)]
    Repo --> FCM[Firebase Cloud Messaging]
    
    UI --> CameraX[CameraX / Zxing]
    CameraX --> VM
`

### 2. User Authentication Flow
`mermaid
sequenceDiagram
    participant User
    participant App
    participant Supabase Auth
    participant Database

    User->>App: Submits Signup (Role: Volunteer/Org)
    App->>Supabase Auth: Create User
    Supabase Auth-->>App: Auth Token
    App->>Database: Save FCM Device Token
    App-->>User: Navigate to Dashboard based on Role
`

### 3. The 2-Step Ticketing & Hardware Scanning Flow
`mermaid
graph TD
    A[Volunteer unblurs QR] --> B[App starts 15s timer & 3s Polling]
    C[Org opens ScanTicketScreen] --> D[CameraX intercepts QR]
    D --> E[OrgViewModel pushes timestamp to Supabase]
    E --> F{1st or 2nd Scan?}
    F -->|1st Scan| G[Ticket Status: 'Checked In']
    F -->|2nd Scan| H[Ticket Status: 'Attended' + Duration Calculated]
    G -.->|Auto-Refresh| B
    H -.->|Auto-Refresh| B
`

---

## ⚙️ Local Setup & Installation

If you want to clone this repository and run BaseCamp locally, follow these steps to set up your environment and database!

### 1. Supabase Backend Setup
You will need a free [Supabase](https://supabase.com/) project to act as the backend.
1. Create a new project in Supabase.
2. Go to the **SQL Editor** and run the following queries to build your tables and set up security:

`sql
-- 1. Create Users Table
create table public.users (
  id uuid primary key references auth.users(id),
  created_at timestamp with time zone default now(),
  name text not null,
  role text not null check (role in ('Volunteer', 'Organization')),
  email text not null,
  phone text,
  website text
);

-- 2. Create Events Table
create table public.events (
  id uuid primary key default gen_random_uuid(),
  created_at timestamp with time zone default now(),
  org_id uuid not null references public.users(id),
  title text not null,
  description text,
  cause text not null,
  location text not null,
  date text not null,
  org_name text not null,
  max_volunteers integer default 0
);

-- 3. Create Tickets Table
create table public.tickets (
  id uuid primary key default gen_random_uuid(),
  created_at timestamp with time zone default now(),
  event_id uuid not null references public.events(id) on delete cascade,
  volunteer_id uuid not null references public.users(id),
  status text default 'Pending',
  check_in_time text,
  check_out_time text
);

-- 4. Create Comments Table
create table public.comments (
  id uuid primary key default gen_random_uuid(),
  created_at timestamp with time zone default now(),
  event_id uuid not null references public.events(id) on delete cascade,
  user_id uuid not null references public.users(id),
  parent_id uuid references public.comments(id) on delete cascade,
  text text not null
);

-- 5. Create Comment Likes Table
create table public.comment_likes (
  id uuid primary key default gen_random_uuid(),
  created_at timestamp with time zone default now(),
  comment_id uuid not null references public.comments(id) on delete cascade,
  user_id uuid not null references public.users(id),
  unique(comment_id, user_id)
);
`

### 2. Configure Local API Keys
For security, Supabase keys are not checked into GitHub. You must add them to a local.properties file.

1. Open the Android Studio project.
2. In the root directory of the project, open (or create) the local.properties file.
3. Go to your Supabase Project Settings -> **API**.
4. Add your **Project URL** and **anon public key** to local.properties like this:

`properties
SUPABASE_URL="https://YOUR_PROJECT_ID.supabase.co"
SUPABASE_KEY="YOUR_ANON_KEY"
`

*Note: Android Studio will automatically generate BuildConfig fields from these properties.*

### 3. Gradle Distribution Note
The gradle/wrapper/gradle-wrapper.properties has been updated to use a standard internet distribution url (https://services.gradle.org/distributions/gradle-8.9-bin.zip). If you were using a local ile:/// distribution URL, Android Studio should automatically download the standard version upon the first Gradle sync, ensuring the project builds cleanly for any contributor on GitHub!