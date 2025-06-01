# 🕹️ Endless Runner Game (Android)

An Android-based **endless runner game** where the player moves between 5 lanes, avoids randomly falling obstacles, collects coins, and tries to reach the highest score. Designed for both **touch** and **sensor** control.

## 📱 Features

- 🎮 **5-lane endless runner** with increasing difficulty over time.
- 📲 Choose between **touch** or **accelerometer** control.
- 🧱 **Randomized obstacle generation** every second.
- 🪙 **Animated coin collection system** using `coins.gif`.
- 🏆 **Local high score saving** with score, timestamp, and optional **GPS coordinates**.
- 🌍 **Google Maps integration** to view high scores by location.
- ⚙️ **Game speed selection** (normal / fast) at start screen.
- 🔊 **Vibration feedback** on collision (optional).
- 🎨 **GIF-based animation** using Glide.
- 📦 **Modular architecture** (separate classes for game logic, UI, utilities).
- 🧩 **SharedPreferences** used to persist local scores.
- 📍 Integrated with **FusedLocationProviderClient** for device location.
- 💾 **High score screen** displaying table and map of records.
- 🔊 **Sound effects & background music** support.
- 🎭 **Customizable player skins** (switch between different characters).
- 💥 **In-game powerups** (speed boosts, shields, etc).
- ☁️ **Firebase integration** for cloud score sync and leaderboard.
- ⚙️ **Settings screen** to toggle sensor mode, game speed, sound, etc.
- 
📸 Screenshots
<img width="62" alt="Screenshot 2025-06-01 at 10 56 34" src="https://github.com/user-attachments/assets/e84751e4-3457-4cb9-b1e4-68630bf02522" />
<img width="280" alt="Screenshot 2025-06-01 at 10 56 45" src="https://github.com/user-attachments/assets/33b1803b-20e7-4906-9f37-0c5d18dee9d4" />
<img width="286" alt="Screenshot 2025-06-01 at 10 57 00" src="https://github.com/user-attachments/assets/25b17dcd-3383-408c-b54f-59492d85c69a" />
<img width="284" alt="Screenshot 2025-06-01 at 10 57 16" src="https://github.com/user-attachments/assets/d14a645f-10c8-4471-a9a1-3d2091763e23" />
<img width="304" alt="Screenshot 2025-06-01 at 10 57 28" src="https://github.com/user-attachments/assets/47ba6097-6360-4c71-b459-af3f1ed24aaf" />
## 🧱 Project Structure

```plaintext
project1_game/
├── activities/
│   ├── MainActivity.kt
│   ├── StartActivity.kt
│   ├── GameOverActivity.kt
│   ├── HighScoresActivity.kt
│   └── MapActivity.kt
├── manager/
│   ├── GameManager.kt
│   ├── HighScore.kt
│   └── GameState.kt
├── logic/
│   └── GameLogic.kt
├── utils/
│   └── VibrationUtils.kt
├── res/
│   ├── layout/
│   ├── drawable/
│   └── values/
```

## 🛠️ Technologies Used

- Kotlin
- Android SDK
- Google Play Services (Location, Maps)
- Glide (GIF rendering)
- SharedPreferences
- XML Layouts with GridLayout/RelativeLayout

## 🚀 Getting Started

1. Clone the repository:
   ```bash
  git clone [https://github.com/DvirSsiksik/project1_game.git](https://github.com/DvirSiksik/project1-game)
  
2.	Open in Android Studio.
3.	Build and run the project on:
	•	A real Android device OR
	•	An emulator with Google Play Services enabled.

🌐 Google Maps API Setup
	1.	Visit Google Cloud Console.
	2.	Create/select a project.
	3.	Enable:
	•	Maps SDK for Android
	•	Fused Location Provider API
	4.	Get an API key.
	5.	Add to AndroidManifest.xml:

<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_API_KEY_HERE" />


📦 Permissions

Make sure the following permissions are in AndroidManifest.xml:

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>


  👨‍💻 Author
  Dvir Siksik









