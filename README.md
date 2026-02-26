
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
