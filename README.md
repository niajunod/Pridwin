# Pridwin Shift Weather  
## PA4: Designing for the World, Not Just the Screen

**Students:**  
- Nia Junod  
- Alina Tarasevich  

**Course:** CS4518  
**Assignment:** PA4 – Designing for the World, Not Just the Screen  
**Due:** March 6, 2026 @ 7:00 PM  

---

## Table of Contents
- [Project Overview](#project-overview)
- [Ubicomp Motivation](#ubicomp-motivation)
- [How the App Assists the User (Context-Aware Behavior)](#how-the-app-assists-the-user-context-aware-behavior)
- [Sensors & Context Sources](#sensors--context-sources)
  - [Hardware Sensor 1: Light Sensor](#hardware-sensor-1-light-sensor)
  - [Hardware Sensor 2: GPS / Location](#hardware-sensor-2-gps--location)
  - [Virtual Sensor: Weather API](#virtual-sensor-weather-api)
- [Background Task](#background-task)
- [Persistence](#persistence)
- [Architecture (MVVM)](#architecture-mvvm)
  - [Model Layer](#model-layer)
  - [ViewModel Layer](#viewmodel-layer)
  - [View Layer](#view-layer)
- [Navigation Requirements](#navigation-requirements)
- [Adaptive Layout, Theme, and Light/Dark Mode](#adaptive-layout-theme-and-lightdark-mode)
- [Privacy by Design](#privacy-by-design)
- [How to Run / How to Test (Grader-Friendly)](#how-to-run--how-to-test-grader-friendly)
- [Known Limitations / Assumptions](#known-limitations--assumptions)
- [References / Resources Used](#references--resources-used)

---

# Project Overview

Pridwin Shift Weather is a context-aware Android application designed to support hospitality staff at The Pridwin Hotel by reacting intelligently to real-world environmental conditions. The core design goal is to move beyond a “desktop” mindset and build an experience that works while a person is moving, working, and managing tasks in the real world.

Instead of requiring constant explicit interaction (buttons, menus, repeated manual refresh), the app uses sensor input and contextual signals to:
- infer environmental state (bright vs dim lighting, current location, weather conditions),
- adapt presentation (day/night visuals, light/dark mode support),
- and provide background assistance (WorkManager notifications that run even when the app is closed).

The app is structured using MVVM to keep logic, data, and UI responsibilities clearly separated.

---

# Ubicomp Motivation

Mark Weiser’s Ubicomp vision emphasizes systems that “disappear” into daily life by responding to context rather than demanding attention. This project applies that philosophy in three ways:

1. **From “user” to human in context**  
   Staff are not sitting at a desk staring at a screen—they are moving between spaces, working different roles, and making time-sensitive decisions. The app supports this by using sensors and background processing so the user does not have to repeatedly input context.

2. **Invisible input via sensors**  
   The system uses hardware sensors (light + location) and a virtual sensor (weather API) to interpret the environment and assist the user with minimal manual input.

3. **Peripheral interaction**  
   Notifications and adaptive UI changes provide information without requiring sustained attention. The goal is to inform and assist while staying out of the way.

---

# How the App Assists the User (Context-Aware Behavior)

The application provides intelligent assistance through a combination of sensor readings, virtual context signals, and user preferences.

## Examples of “assistive” logic:
- **Day vs night adaptation**: The UI adapts its visuals and contrast based on the day/night context derived from weather/time data.
- **Ambient light awareness**: The app monitors lux values from the hardware light sensor and classifies the environment (very dark → direct sun). This supports context understanding and can be used to justify adaptive UI choices.
- **Role-based context**: The user can select their role (e.g., pool-related vs dining-related). Role is persisted and used for contextual messaging.
- **Commute preference context**: The user can set a commute mode (e.g., bike vs walking). This preference can be used to drive reminders or warnings.
- **Background context updates**: WorkManager runs a background task and posts a contextual notification when appropriate, even if the app is not open.

These behaviors align with the assignment’s intent to design for context and reduce reliance on explicit user input.

---

# Sensors & Context Sources

This project uses **two hardware sensors** and **one virtual sensor** as required.

## Hardware Sensor 1: Light Sensor

**Source:** Android `SensorManager` / `Sensor.TYPE_LIGHT`  
**Purpose:** Detect ambient brightness (lux) to interpret environmental lighting.

### What is captured:
- Lux value (float) from light sensor events.

### How it is interpreted:
Lux values are categorized into a human-readable environment “bucket,” such as:
- Very dark  
- Dim  
- Indoor  
- Bright  
- Direct sun  

### How it helps gather context:
- Enables the app to understand whether the user is in a dark indoor environment versus bright daylight.
- Supports context-driven UI decisions (contrast, theme, display emphasis) and demonstrates invisible input.

### Where it appears in the app:
- The “Dashboard” / sensor visualization shows current lux and bucket.
- The “Context Logic” screen explains how lux is interpreted.

---

## Hardware Sensor 2: GPS / Location

**Source:** FusedLocationProvider (Google Play Services) and Android location permissions  
**Purpose:** Acquire current location so weather context matches the user’s real environment.

### What is captured:
- Latitude / Longitude (approximate current location).

### How it helps gather context:
- Weather changes significantly by location; using GPS ensures the app is contextually accurate without requiring the user to type a city.
- Enables a real-world experience that adapts as the user moves.

### Where it appears in the app:
- Dashboard uses location to fetch and display current weather at the user’s location.
- Location permissions and usage are documented in the Privacy screen.

---

## Virtual Sensor: Weather API

**Source:** OpenWeather API (network-based context source)  
**Purpose:** Provide “environmental context” not available directly from hardware sensors.

### Data used:
- Temperature
- Weather condition (e.g., clear, rain)
- Day/night timing (based on timestamps or sunrise/sunset logic)

### How it helps gather context:
- Enables reasoning about conditions relevant to shift work (e.g., pool conditions, commute).
- Supports adaptive UI presentation (day/night appearance) and messaging.

### Where it appears in the app:
- Dashboard shows current weather.
- Forecast screen shows multi-day weather outlook.

---

# Background Task

**Requirement:** WorkManager or ForegroundService that runs when the app is not active/open.

This project uses **WorkManager**.

## What it does:
- A background worker executes periodically (based on your WorkManager schedule).
- The worker reads persisted user preferences (role, commute mode, notifications enabled).
- The worker posts a notification with contextual information.

## Why this satisfies Ubicomp design:
- The app provides assistance in the periphery—information arrives without requiring the user to open the app.
- Background execution demonstrates “technology that never sleeps,” while still respecting user control (notifications toggle) and permissions.

## Notes:
- Android 13+ requires POST_NOTIFICATIONS permission; the worker respects this and will not crash if permission is not granted.

---

# Persistence

**Requirement:** Use Room or DataStore to store historical sensor data or user settings.

This project uses **DataStore Preferences**.

## What is persisted:
- Selected role
- Selected commute mode
- Notifications enabled/disabled

## Why persistence matters for Ubicomp:
- Context-aware systems should not require the user to repeatedly re-enter preferences.
- Persisted preferences allow background tasks to remain helpful and personalized without manual setup every time.

---

# Architecture (MVVM)

The app follows **Model–View–ViewModel** to keep responsibilities separated and maintainable.

## Model Layer
The Model layer is responsible for data storage and retrieval only. Examples include:
- Data models representing weather and forecast information
- DataStore persistence (`UserProfileStore`)
- DTO-to-model mapping for API responses
- Any repository/service classes that fetch data

**Key rule followed:** The Model layer does not contain UI rendering logic.

---

## ViewModel Layer
The ViewModel layer is responsible for “logic,” including:
- Interpreting sensor readings (light → lux bucket)
- Managing permission state
- Triggering refresh actions (weather fetch)
- Holding UI state as flows (StateFlow/Flow)
- Providing data to views in a UI-friendly form

Examples:
- `WeatherViewModel` handles weather retrieval, UI state, and permission state.
- `LightViewModel` processes sensor events and produces categorized lighting context.

**Key rule followed:** ViewModels do not render UI. They expose state and logic.

---

## View Layer
The View layer is built in Jetpack Compose and is responsible only for:
- Rendering UI based on the current ViewModel state
- Displaying loading/error/ready states
- Providing navigation controls (buttons / bottom nav)
- Collecting state flows and showing them

**Key rule followed:** The View does not directly implement data fetching or sensor logic.

---

# Navigation Requirements

The assignment requires a Bottom Navigation Bar with four destinations:

1. **Dashboard**  
   Primary UI for sensor visualization and the main “live” experience:
   - Current weather
   - Light sensor readout (lux and bucket)
   - Quick access to forecast and ambient screens (depending on implementation)

2. **Context Logic**  
   A technical “How it works” screen describing:
   - What sensors are used
   - How sensor fusion / interpretation works
   - How background logic is triggered
   - What rules the app uses to assist the user

3. **Privacy by Design**  
   A dedicated screen explaining:
   - Every permission requested
   - Why the permission is needed
   - How data is handled (stored locally, not shared)
   - What happens if permission is denied

4. **Settings**  
   Toggles and preferences including:
   - Role selection
   - Commute mode
   - Light/dark mode behavior
   - Notification preference
   - Any persistent preferences

---

# Adaptive Layout, Theme, and Light/Dark Mode

## Adaptive Layout
The UI uses responsive Compose layouts (e.g., flexible columns/rows, scrolling content) so it remains usable on different device sizes and orientations.

## Custom Theme
The app includes a custom theme (colors/fonts) defined in the theme files. Visual identity is consistent across screens.

## Light/Dark Mode
The app supports both light and dark mode.  
A user-accessible setting exists to toggle or control this behavior (meeting the requirement that it can be changed via Settings).

---

# Privacy by Design

This app requests only permissions needed for context sensing and assistance.

## Permissions Requested
- **ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION**  
  Used to retrieve current location so weather matches the user’s environment.  
  Location is used for weather lookup and is not shared externally beyond the weather request.

- **POST_NOTIFICATIONS (Android 13+)**  
  Used to show background contextual notifications through WorkManager.

## Data handling
- Preferences are stored locally using DataStore.
- The app does not store personal identity information.
- No user data is sold or shared.
- If permissions are denied, the app should still run and present appropriate UI guidance (error/permission states).

---

# How to Run / How to Test (Grader-Friendly)

## Setup
1. Clone/open the Android Studio project.
2. Add your OpenWeather API key to `local.properties` (or wherever your project expects it):


3. Sync Gradle and run the app on an emulator or device.

## Testing Checklist (what graders can do)
1. **Launch app** → Dashboard should load.
2. **Grant location permission** → Weather should load for current location.
3. **Navigate using bottom nav** → Dashboard / Context Logic / Privacy / Settings.
4. **Open Ambient Controls / Sensor view** → Lux value and bucket update (on a device/emulator supporting sensor simulation).
5. **Change Settings** → Role/commute/notifications persist after app restart.
6. **Background task** → WorkManager posts contextual notification (depending on schedule).  
   - Ensure notifications are enabled and permission granted (Android 13+).

---

# Known Limitations / Assumptions

- Sensor availability depends on emulator/device hardware support.
- Weather API calls require an active internet connection.
- Background task execution timing can vary depending on OS scheduling and battery optimization.

---

# References / Resources Used

- Android Developers Documentation (Jetpack Compose, Sensors, Permissions)
- WorkManager Documentation
- DataStore Preferences Documentation
- OpenWeather API Documentation
- Kotlin Coroutines / Flow Documentation
- Course lecture materials and examples
