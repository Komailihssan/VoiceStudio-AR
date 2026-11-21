# VoiceStudio v0.5.8 — Arabic TTS patch (Android)

This patch targets **phone-local text-to-speech only**. It does not translate the UI, change the app locale, or claim Arabic support for every voice/model.

## What was verified in the supplied v0.5.8 source

- Android's phone model catalogue (`mobile/contract/models.json`) includes `supertonic3-int8`, a local `sherpa-onnx` TTS model.
- That model declares the language code `ar` (Arabic) and a minimum RAM requirement of 3 GB.
- Its catalogue size is approximately 145.3 MB (`total_bytes`), downloaded separately from the APK.
- The same catalogue lists Whisper models with `ar` for Arabic speech recognition, but this patch is for speech generation, not transcription.
- The Android speech screen already exposes model and language pickers and passes the selected language to local inference.

## Changes in this patch

1. For a fresh phone-local TTS setup, prefer a TTS model whose declared `languages` contains `ar`; on the supplied v0.5.8 catalogue this selects Supertonic 3.
2. When the selected TTS model supports Arabic, preselect `ar` in the speech language picker.
3. Preserve a user's explicit saved model choice and fall back to other models if the Arabic model does not fit the device RAM.
4. Add unit tests for Arabic-first selection, explicit user override, and RAM fallback.

Files changed:
- `mobile/android/features/src/main/kotlin/com/voicestudio/mobile/features/speak/SpeakScreen.kt`
- `mobile/android/features/src/test/kotlin/com/voicestudio/mobile/features/SpeakModelChoiceTest.kt`

## Build an APK

The source archive does **not** contain a prebuilt APK. Build on a machine with:
- JDK 21
- Android SDK Platform 36 and Build Tools
- Android NDK `27.1.12297006`
- CMake 3.22.1
- Python 3 and Bash (for the pinned native runtime preparation)

From the repository root:

```bash
bash mobile/native/android/build.sh
cd mobile/android
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

The debug APK is expected at:

`mobile/android/app/build/outputs/apk/debug/app-debug.apk`

The native runtime preparation fetches pinned source archives and the sherpa AAR; model weights are not bundled into the APK. Install the app, open **Speak**, choose the phone/local execution path, accept the Supertonic 3 model licence, download the model, confirm **Arabic** is selected, and test a short Arabic sentence. Model download and licence acceptance are separate from APK installation.

## Validation status

Source-level inspection and the patch were completed. An APK build and on-device audio test were **not** completed in this environment because Android SDK/NDK are not installed here. Do not treat this source patch as a tested APK.

## Automated APK workflow added

`.github/workflows/android-arabic-apk.yml` now provides a manual GitHub Actions build (and reruns for relevant Android source changes). It installs JDK 21, Android Platform 36, NDK 27.1.12297006, and CMake 3.22.1; prepares the pinned native runtimes; runs Android unit tests; assembles a debug APK; and uploads it as a 14-day workflow artifact. See `BUILD-ANDROID-APK.md` for instructions.

This workflow has been added to the source archive but has **not** been executed from this environment. A successful remote workflow and a physical-device Arabic audio test are still required before calling the APK verified.
