# BBC — British Broadcasting Corporation

An open-source Android TV IPTV player built for big screens. Add M3U playlists and browse channels from your couch.

## Features

- **M3U playlist support** — Add one or more M3U URLs; channels are fetched, parsed, and grouped automatically
- **ExoPlayer playback** — Hardware-accelerated HLS/MPEG-TS/MP4 via Media3
- **Channel grouping** — Categories parsed from `group-title` tags in M3U
- **Channel logos** — Loaded from `tvg-logo` tags via Glide
- **Persistent playlists** — Stored in Room DB; survives reboots
- **D-pad navigation** — All screens are fully remote-friendly
- **Dark leanback UI** — Standard Android TV browse layout with header categories

## Requirements

- Android TV or Google TV device running Android 5.0 (API 21) or higher
- Internet connection to fetch playlists

## Setup

### Build from source

1. Clone the repo:
   ```sh
   git clone https://github.com/unfamiliardev/BBC.git
   cd BBC
   ```

2. Open in Android Studio (Hedgehog or newer recommended).

3. Build and run on an Android TV emulator or device:
   ```
   Run > Run 'app'
   ```

4. On first launch, press the **search icon** (top-left of the browse screen) to open playlist management. Enter any valid M3U URL and tap **Add**. Navigate back to the main screen and wait for channels to load.

### Sideload a release APK

Download the latest APK from the [Releases](https://github.com/unfamiliardev/BBC/releases) page and install via ADB:

```sh
adb install bbc-release.apk
```

## Project structure

```
app/src/main/java/com/unfamiliardev/bbc/
  data/
    db/          Room database (PlaylistEntity, PlaylistDao, AppDatabase)
    model/       Data classes (Channel)
    parser/      M3UParser — pure Kotlin, no dependencies
    repository/  PlaylistRepository — single source of truth
  ui/
    browse/      BrowseFragment + BrowseViewModel + card presenters
    player/      PlayerActivity + PlayerViewModel (ExoPlayer)
    playlist/    PlaylistActivity + PlaylistViewModel (M3U management)
    credits/     CreditsActivity
  util/
    KonamiCodeDetector.kt
```

## Forking guide

| What to change | Where |
|---|---|
| App name | `res/values/strings.xml` → `app_name` |
| Package name | `app/build.gradle` → `applicationId`, then rename the `com/unfamiliardev/bbc` source tree |
| Accent color | `res/values/colors.xml` → `accent` |
| Launcher banner | Replace `res/drawable/app_banner.xml` with a `320×180dp` PNG in `res/drawable-xhdpi/` |
| Developer credit | `CreditsActivity.kt` + `AndroidManifest.xml` metadata |
| License | `LICENSE` |

The M3U parser (`M3UParser.kt`) is self-contained — no network calls, no Android dependencies. Easy to unit-test or swap out.

## Contributing

PRs welcome. Please keep changes focused — one feature or fix per PR.

## License

Apache 2.0. See [LICENSE](LICENSE).

---

*BBC is not affiliated with the BBC (British Broadcasting Corporation).*
