# Build VoiceStudio Arabic Android APK

This source tree includes an Android-only Arabic TTS default-selection patch for v0.5.8.

## Automated build on GitHub

1. Create a GitHub repository and upload/push this source tree.
2. Open **Actions** → **Build VoiceStudio Arabic Android APK**.
3. Select **Run workflow**.
4. Wait for the native runtime build, Android unit tests, and APK assembly to finish.
5. Download the artifact named `VoiceStudio-0.5.8-Arabic-Android-debug` from the successful workflow run.

The workflow builds a debug-signed APK. It is suitable for direct installation, but it is not a Play Store release-signed APK. The Supertonic 3 model weights are downloaded separately inside the app; they are not bundled into the APK. On first use, open Speak, choose phone/local execution, accept the model licence, download Supertonic 3, and select Arabic.

## Local build requirements

- JDK 21
- Android SDK Platform 36 and Build Tools 36.0.0
- Android NDK 27.1.12297006
- CMake 3.22.1 and Ninja
- Python 3 and Bash

From the repository root:

```bash
bash mobile/native/android/build.sh
cd mobile/android
bash ./gradlew :features:testDebugUnitTest :app:testDebugUnitTest --no-daemon
bash ./gradlew :app:assembleDebug --no-daemon
```

Expected APK:

`mobile/android/app/build/outputs/apk/debug/app-debug.apk`

A successful build does not replace an on-device Arabic synthesis test. Verify generated speech, audio export, and offline inference on the target device.
