HerCare — Your Health, Our Care

<p align="center">
  <img src="app/src/main/res/drawable/new_hercare_logo.jpg" alt="HerCare logo" width="180" />
</p>

<p align="center">
  A bilingual Android app for menstrual cycle tracking, daily health logs, medicine reminders, and period supplies.
</p>

<p align="center">
  <a href="https://github.com/dm-asif/HerCare/raw/refs/heads/main/public/HerCare.apk"><strong>Download the latest committed APK</strong></a>
  · Android 10+ · Kotlin · Jetpack Compose
</p>

## About

HerCare helps users keep period and daily health records in one place. It calculates cycle estimates from recorded periods, offers reminders, and keeps a local history for reports. The interface is available in English and Bangla.

Cycle dates, fertile-window estimates, and health notices are informational estimates based on user-entered records; they are not a diagnosis or a substitute for advice from a healthcare professional.

## Features

- **Cycle tracking:** record period dates, review cycle history, and see estimated cycle day, phase, next period, ovulation, and fertile window.
- **Daily health logs:** record flow, pain, symptoms, discharge type, pad use, and notes.
- **Cycle observations:** calculate average cycle and period lengths and show rule-based notices for patterns such as short or long cycles and prolonged bleeding.
- **Medicine routine:** add medicines, doses, schedules, and course dates; mark scheduled doses as taken and receive local reminders.
- **Pad stock:** track available pads, log usage, restock, and receive a low-stock reminder near the predicted period.
- **Health report:** review logged cycle metrics and symptom summaries, with an option to share a text summary.
- **Discharge guide:** browse the built-in educational guide.
- **Language and privacy controls:** use the app in English or Bangla, store records in the on-device Room database, and optionally enable biometric app lock.
- **Local notifications:** schedule cycle and medicine reminders; the app includes a receiver to restore relevant reminders after a device restart.

## Download and install

The repository includes a debug APK at [`public/HerCare.apk`](public/HerCare.apk) (about 28 MB).

[**Download HerCare.apk**](https://github.com/dm-asif/HerCare/raw/refs/heads/main/public/HerCare.apk)

The APK targets Android 10 (API 29) and later. After downloading it on an Android device, open the APK and follow the system prompts to install it. Android may ask you to allow installation from the app used to download the file.

## Screenshots and artwork

There are no full in-app screenshots in the repository yet. The screenshot test captures only the app name, not a representative screen, so it is not presented as a product screenshot here. The project does include these genuine app artwork assets:

<p align="center">
  <img src="app/src/main/res/drawable/new_hercare_logo.jpg" alt="HerCare logo artwork" width="220" />
  &nbsp;&nbsp;
  <img src="app/src/main/res/drawable/img_cycle_hero.jpg" alt="Cycle illustration used in the app" width="220" />
</p>

*These are brand and in-app illustration assets, not screenshots of the interface.*

## Technology

| Area | Technology |
| --- | --- |
| Language and UI | Kotlin, Jetpack Compose, Material 3 |
| Local persistence | Room |
| App architecture | ViewModel, Kotlin Coroutines, Navigation Compose |
| Device features | AndroidX Biometric, AlarmManager, local notifications |
| Build | Gradle Kotlin DSL, Android Gradle Plugin |
| Automation | GitHub Actions workflow for Android debug APK builds |

## Requirements

- Android Studio with Android SDK 36 installed
- JDK 21
- An Android device or emulator running Android 10 (API 29) or newer to run the app

The Gradle configuration uses `minSdk = 29`, `targetSdk = 36`, and Java 11 source/target compatibility. The CI workflow uses JDK 21 to build the project.

## Build from source

1. Clone the repository and enter the project directory:

   ```bash
   git clone https://github.com/dm-asif/HerCare.git
   cd HerCare
   ```

2. Open the project in Android Studio and allow Gradle to sync, or build from a terminal:

   ```bash
   ./gradlew assembleDebug
   ```

   On Windows, use `gradlew.bat assembleDebug`.

3. Find the generated debug APK here:

   ```text
   app/build/outputs/apk/debug/app-debug.apk
   ```

The repository also provides `./build_apk.sh`, which prepares the local build environment and runs the same debug build.

## Automated builds

The workflow in [`.github/workflows/android_build.yml`](.github/workflows/android_build.yml) runs for pushes, pull requests, and manual dispatches. It builds a debug APK and uploads it as the `HerCare-Android-APK` Actions artifact. For pushes and manual runs, the workflow also attempts to publish the APK in a GitHub Release.

## Medical notice

HerCare provides personal tracking and educational information. Its cycle predictions and rule-based notices can be inaccurate, especially when cycles vary. They do not diagnose or treat a medical condition. Consult a qualified healthcare professional about symptoms or health concerns.
