# Manhunt Challenge Android

This repository wraps the published Manhunt Challenge website in a lightweight Android WebView app.

## Build
Pushing to `main` starts the **Build Android APK** GitHub Actions workflow. The workflow creates a debug APK and uploads it as an artifact named `manhunt-debug-apk`.

The app requires internet access to load the published website. It does not request contacts, microphone, or location permissions.

Website: https://manhunt-challenge.floot.app
