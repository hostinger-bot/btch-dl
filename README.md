# Media Downloader

An open-source Android app for downloading media (video, audio, images) from 20+ social media platforms — powered by [btch-downloader](https://github.com/hostinger-bot/btch-downloader).

## Features

- **20+ platforms**: TikTok, Instagram, Twitter/X, YouTube, Facebook, CapCut, Google Drive, Pinterest, Xiaohongshu (Rednote), Douyin, SnackVideo, Cocofun, Spotify, SoundCloud, Threads, Kuaishou, MediaFire, and more
- **Auto-detect platform** — just paste a link
- **Rich metadata** — title, author, duration, views, thumbnails, etc.
- **Live download progress** — real-time progress bar (0–100%)
- **YouTube Search** — search videos and fetch MP3/MP4 download links
- **Dark mode**

## Installation

### Android App (APK)

**Option 1 — Download APK**
Grab the latest APK from [Releases](https://github.com/hostinger-bot/btch-dl/releases) and install it on your device.

**Option 2 — Build from source**

```bash
git clone https://github.com/hostinger-bot/btch-dl.git
cd btch-dl

# Build Release APK (automatically signed and installable)
./gradlew :app:assembleRelease
# APK is generated at app/build/outputs/apk/release/app-release.apk

# Build Debug APK
./gradlew :app:assembleDebug
# APK is generated at app/build/outputs/apk/debug/app-debug.apk
```
### Ktor Client Library (Kotlin/JVM)

Add to your `build.gradle.kts`:

```kotlin
repositories {
    mavenCentral()
}
dependencies {
    implementation("io.btch:btch-downloader-ktor:1.0.0")
}
```

## Quick Start

```kotlin
// Create a client
val api = BtchDownloader()

// Download TikTok
val tiktok = api.ttdl("https://www.tiktok.com/@user/video/123")
println(tiktok.title)

// Download YouTube
val youtube = api.youtube("https://youtu.be/abc123")
println(youtube.mp4)

// Search YouTube
val results = api.yts("cats")
println(results.all)

// Raw JSON response
val raw: String = api.raw("instagram", "https://www.instagram.com/p/abc123/")
```

### Custom Configuration

```kotlin
val api = BtchDownloader(
    BtchConfig(
        baseUrl = "https://backend1.tioo.eu.org",
        timeoutMs = 30_000,
        logLevel = LogLevel.HEADERS,
    )
)
```

## License

MIT
