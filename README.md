# DeepAgent
AI‑Powered Android Launcher with military‑grade security

Overview

DeepAgent — Android launcher + autonomous agent platform. Designed to be an extensible, privacy‑first launcher with on‑device ML, voice control, screen understanding and integrations to external APIs. This repository will contain the launcher skeleton, AI integration points, an invite‑code access system, and modules for dropshipping/monetization.

Key goals (initial)

- APK distribution only (no Google Play). Owner controls access with invite codes.
- Connect to arbitrary external APIs from device (secure storage of keys).
- Privacy: local encryption (Android Keystore), optional federated learning, differential privacy for updates.
- Basic dropshipping module (product discovery, profit estimation, optional order automation connectors like Shopify/AliExpress/WooCommerce) as a monetization channel.
- Multilingual support (14 languages; Ukrainian priority).

Invite / Access codes (initial design)

- Reusable-with-expiry JWT tokens signed by owner (RS256). Device validates using embedded public key.
- Short expiry recommended (7-30 days). Revocation later via optional server.

Quick start

1. To build APK locally, open the project in Android Studio (Kotlin + Gradle). Use the `bootstrap` branch.
2. To generate invite tokens locally, see `invite_token_generator.py` (requires Python, PyJWT, cryptography).

MVP scope

- Launcher skeleton (home screen, app drawer)
- Invite activation flow (local JWT validation)
- Secure vault for API keys (Android Keystore + EncryptedSharedPreferences)
- API adapter layer (OpenAI, HuggingFace, Binance, Bybit, OKX, HTX, Kraken, Upwork)
- Dropshipping POC (AliExpress search + Shopify/WooCommerce connectors)
- CI: build debug APK

Security notes

- Keys stored in Android Keystore. Sensitive operations encrypted via AES‑GCM.
- Biometric / PIN optional unlock.
- TLS1.3 mandatory for network calls; certificate pinning option for critical endpoints.

## Project Status

✅ **COMPLETE** - Full Android Launcher implementation with:
- Complete Kotlin/Gradle project structure
- Launcher functionality (home screen, app drawer, search)
- Invite code activation system (JWT validation)
- Secure API key storage (Android Keystore + EncryptedSharedPreferences)
- Settings and API key management UI
- Multilingual support (English + Ukrainian)
- GitHub Actions CI/CD for automatic APK builds

## Download APK

APK files are automatically built by GitHub Actions on every push:

1. Go to [Actions](../../actions) tab
2. Click on the latest successful workflow run
3. Download the APK from artifacts:
   - `deepagent-debug.apk` - Debug version (recommended for testing)
   - `deepagent-release-unsigned.apk` - Release version (needs signing)

Or check [Releases](../../releases) for tagged versions.

## Installation

1. Download the APK file
2. Enable "Install from Unknown Sources" on your Android device
3. Install the APK
4. Generate an invite code (see below)
5. Launch DeepAgent and enter the invite code

## Generating Invite Codes

Use the included Python script:

```bash
python3 invite_token_generator.py
```

This generates a JWT token valid for 30 days. You can modify the expiry in the script.

## Building Locally

Requirements:
- JDK 17+
- Android SDK (API 34)

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# APK location
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release-unsigned.apk
```

## Features Implemented

### Core Launcher
- ✅ Home screen with app grid
- ✅ App search functionality
- ✅ Launch installed apps
- ✅ Material Design UI

### Security
- ✅ Invite code activation (JWT with RSA256)
- ✅ Secure storage (Android Keystore)
- ✅ Encrypted SharedPreferences
- ✅ Biometric lock option

### API Integration
- ✅ Secure API key storage
- ✅ Support for multiple services (OpenAI, HuggingFace, Binance, etc.)
- ✅ Easy key management UI

### Localization
- ✅ English (default)
- ✅ Ukrainian (повна підтримка)

## Architecture

```
app/src/main/java/com/deepagent/launcher/
├── DeepAgentApplication.kt       # Application class
├── ui/                           # UI layer
│   ├── MainActivity.kt           # Main launcher screen
│   ├── InviteActivationActivity.kt
│   ├── SettingsActivity.kt
│   ├── ApiKeysActivity.kt
│   └── Adapters...
├── security/                     # Security layer
│   ├── SecureStorage.kt          # Encrypted storage
│   └── InviteValidator.kt        # JWT validation
├── data/                         # Data models
│   └── AppInfo.kt
└── utils/                        # Utilities
    └── AppManager.kt             # App management
```

## Next Steps

To extend functionality:
1. Add AI integration (OpenAI, HuggingFace APIs)
2. Implement voice control
3. Add screen understanding (OCR, ML)
4. Implement dropshipping module
5. Add more API connectors