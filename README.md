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

# Ubicomp Motivation / Personal Reflection
I have worked at The Pridwin Hotel for the past four years, and during that time I have seen firsthand how deeply weather influences every aspect of operations. The Pridwin operates in the Hamptons, where peak season runs from Memorial Day through Labor Day. Unlike many traditional restaurants, our environment is highly dependent on external conditions. Because the property includes a beach and a pool in addition to the main dining room, staffing needs expand and contract based almost entirely on the weather forecast.

When the weather is warm and sunny, the beach and pool become major revenue drivers. We need additional servers, support staff, and management oversight in those areas. On cloudy or rainy days, those same spaces may be nearly empty, and staffing demand shifts back toward indoor dining. These fluctuations have a direct impact on employee earnings, fairness in shift distribution, and overall guest experience.

All of our servers are cross-trained and rotate between beach, pool, and dining room shifts to maintain fairness. However, in practice, scheduling decisions are often made without fully considering weather projections. Managers may rely on intuition or short-term forecasts, and employees themselves are often unaware of how upcoming weather conditions might affect their shifts and earning potential. As a result, schedules may not align optimally with environmental conditions.

This real-world friction became the inspiration for Pridwin Shift Weather. As I prepare to step into a managerial role this summer, I began thinking about how contextual awareness could support smarter scheduling decisions. Instead of treating weather as something external that we react to at the last minute, I wanted to design a system that integrates environmental context directly into operational awareness. This project became an opportunity to build a bridge between digital tools and real-world management decisions.

From a Ubiquitous Computing perspective, this project required shifting my mindset away from “feature building” and toward “context design.” Traditional apps assume a stationary user focused on a screen. In hospitality, that assumption is unrealistic. Staff are constantly moving, multitasking, and responding to guests. Any tool designed for this environment must remain peripheral—it must assist without distracting.

This is why sensor integration became central to the design. The light sensor allows the system to detect environmental brightness automatically. While this may seem simple, it reinforces a broader design philosophy: the system should gather environmental information implicitly, rather than requiring explicit input. Similarly, using GPS to determine location removes the need for manual city selection and ensures weather data reflects the user’s real surroundings. The Weather API functions as a virtual sensor, extending environmental awareness beyond what hardware alone can capture.

The most meaningful technical shift in this project was implementing background tasks through WorkManager. A context-aware system should not require the user to open the app to receive assistance. By enabling background execution, the app becomes more aligned with Weiser’s vision of invisible technology. It can notify a manager or staff member about relevant environmental conditions without demanding active engagement.

In addition, this is Alina and my second service-industry-oriented application. In B-Term 2025, for our Introduction to AI course, we developed SmartShelf AI—an inventory forecasting tool for liquor management. That project analyzed historical CSV sales data and allowed users to select a month and liquor category (such as RTDs, or Ready-to-Drink cocktails). Based on previous data trends, it generated recommended order quantities and projected costs. SmartShelf AI focused on predictive modeling and optimization using historical datasets.

Pridwin Shift Weather builds upon that foundation but shifts the focus from historical prediction to real-time environmental context. Instead of asking, “What should we order based on past trends?” this project asks, “How can we respond intelligently to the environment right now?” Both applications address operational inefficiencies in hospitality, but this project required a deeper consideration of hardware constraints, permissions, background execution limits, and the social implications of always-on technology.

One of the most valuable lessons from this project was understanding how small environmental signals can meaningfully influence decision-making. Weather is not just informational—it directly affects staffing, morale, earnings, and guest satisfaction. By designing a system that integrates weather context, user roles, and background processing, we move toward a model where digital systems augment human judgment rather than replace it.

This project also reinforced the importance of privacy and restraint. A context-aware system has the potential to become intrusive if poorly designed. By clearly explaining permissions, storing data locally using DataStore, and limiting background behavior to meaningful notifications, we aimed to design responsibly.

Overall, Pridwin Shift Weather represents a personal and professional intersection for me. It is rooted in real-world experience, inspired by operational challenges I have witnessed for years, and motivated by my transition into a management role. More importantly, it demonstrates a shift in how I think about software—not as isolated screens, but as systems embedded within physical environments. Designing for context, rather than just interaction, fundamentally changes what it means to build an application.

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
- Dashboard shows current weather. I mean the dashboard is just the WeatherHomeScreen it's not a dashboard, it's the main screen where someone can select a role.
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
It's not called Dashboard, it's called Home, but it is the dashboard screen where you can select roles and then show the 5 day forecast schedule. 
   Primary UI for sensor visualization and the main “live” experience:
   - Current weather
   - Light sensor readout (lux and bucket)
   - Quick access to forecast and ambient screens (depending on implementation)

3. **Context Logic**  
   A technical “How it works” screen describing:
   - What sensors are used
   - How sensor fusion / interpretation works
   - How background logic is triggered
   - What rules the app uses to assist the user

4. **Privacy by Design**  
   A dedicated screen explaining:
   - Every permission requested
   - Why the permission is needed
   - How data is handled (stored locally, not shared)
   - What happens if permission is denied

5. **Settings**  
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
The app includes a custom theme (colors/fonts) defined in the theme files. Visual identity is consistent across screens. We also customized the application icon to be the Pridwin Hotel & Cottages Logo. We also used the logo to get the color theme for the project which helped us pick a theme. It's simple. 

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
