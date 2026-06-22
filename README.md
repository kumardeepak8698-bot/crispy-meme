# Device Cloner Pro

Android application for cloning and installing apps with device spoofing capabilities.

## Quick Build

```bash
# Clone repository
git clone https://github.com/kumardeepak8698-bot/crispy-meme.git
cd crispy-meme

# Build Debug APK
./gradlew assembleDebug

# APK location
app/build/outputs/apk/debug/app-debug.apk
```

## Install

```bash
# Using ADB
adb install app/build/outputs/apk/debug/app-debug.apk

# Or Android Studio: Run button > Select device
```

## Features

✅ Lists all installed apps with icons
✅ Clone any app with randomized device identity
✅ Device spoofing via Frida script injection
✅ Automatic APK signing and installation
✅ Material Design UI
✅ Dark/Light theme support

## Project Structure

```
app/src/main/java/com/cloner/devicecloner/
├── MainActivity.kt
├── CloneManager.kt
├── DeviceRandomizer.kt
└── ui/
    ├── AppAdapter.kt
    └── AppInfo.kt
```

## Permissions

- QUERY_ALL_PACKAGES
- REQUEST_INSTALL_PACKAGES
- INTERNET
- READ_EXTERNAL_STORAGE
- READ_PHONE_STATE
- ACCESS_WIFI_STATE

## Requirements

- Android 8+ (API 26)
- Java 17+
- Gradle 8.10.2

## Build Types

### Debug
```bash
./gradlew assembleDebug
```

### Release
```bash
./gradlew assembleRelease
```

## Troubleshooting

**Build fails**: Ensure Java 17+ is installed
**Install fails**: Enable "Install from Unknown Sources" in settings
**Permission errors**: Grant all permissions when app starts

## License

MIT License
