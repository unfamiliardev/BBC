# BBC

An open-source Android TV IPTV client. Add M3U playlists, browse live TV and VOD, and watch from your couch with full remote control support.

> **Not affiliated with the BBC (British Broadcasting Corporation).**

---

## Features

- **M3U playlist support** — Add any number of M3U/M3U8 URLs; channels are fetched, parsed, and grouped automatically
- **Live TV + VOD** — Channels are split into Live TV and Movies & Series sections based on group tags
- **EPG / TV Guide** — XMLTV support for programme info and now-playing data
- **ExoPlayer playback** — Hardware-accelerated HLS, MPEG-TS, and MP4 via Media3
- **Channel logos** — Loaded from `tvg-logo` tags
- **Favourites & Recently Watched** — Pinned channels and quick-resume
- **Sleep timer** — Auto-stops playback after 15 / 30 / 60 / 90 min or 2 hours
- **Track selector** — Switch audio tracks and subtitles mid-stream
- **Aspect ratio toggle** — Fit / Fill / Zoom, cycled from the player menu
- **Stream info HUD** — Resolution, codec, bitrate, audio format, and URL overlay
- **Seek** — Skip ±15 seconds on VOD content
- **Channel number jump** — Type a number on your remote to jump directly to that channel
- **Picture-in-Picture** — Minimise the player while browsing other apps
- **D-pad navigation** — Every screen is fully remote-friendly
- **Dark red & black UI** — Custom vaporwave-themed interface

---

## Requirements

- Android TV or Google TV device running Android 5.0 (API 21) or higher
- An internet connection to fetch playlists

---

## Installation

### Sideload a release APK

Download the latest APK from the [Releases](https://github.com/unfamiliardev/BBC/releases) page.

**Via ADB:**
```sh
adb install bbc-debug.apk
```

**Via file manager on device:** Transfer the APK over USB or network and open it with a file manager that supports APK installs.

> ### Upgrading from v1.0.17 or earlier
>
> Version 1.0.18 introduced a stable signing key. If you have **v1.0.17 or older** installed, Android will block the update with "App not installed as package conflicts with an existing package."
>
> **Fix:** Uninstall the old version first, then install v1.0.18+. You only need to do this once — all future updates will install over each other without any issue.

### Build from source

```sh
git clone https://github.com/unfamiliardev/BBC.git
cd BBC
```

Open in Android Studio (Hedgehog or newer), then **Run → Run 'app'** on an Android TV device or emulator.

---

## Usage

1. Open the app and go to **Settings → Manage Sources**
2. Paste an M3U URL and tap **Add Source**
3. Navigate back — channels load automatically
4. Use the sidebar to switch between **Live TV**, **Movies & Series**, **TV Guide**, and **Recently Watched**

### Player controls

| Key | Action |
|---|---|
| OK / Play-Pause | Play / Pause |
| DPAD Left / Right | Seek ±15s (VOD only) |
| MENU | Player options (sleep timer, tracks, aspect ratio) |
| INFO | Toggle stream info HUD |
| Back | Exit player |

---

## Project structure

```
app/src/main/java/com/unfamiliardev/bbc/
  data/
    db/          Room database
    model/       Channel, Programme
    parser/      M3UParser, XmltvParser
    repository/  PlaylistRepository, EpgRepository
  ui/
    channels/    ChannelsFragment, ChannelListAdapter
    epg/         EpgFragment, EpgAdapter
    player/      PlayerActivity, PlayerMenuFragment, TrackSelectorFragment, SleepTimerFragment
    playlist/    PlaylistActivity (source management)
    settings/    MainSettingsFragment, LanguagePickerFragment
    browse/      BrowseViewModel, ChannelOptionsFragment
  util/
    AppSettings, FavouritesStore, RecentlyWatchedStore, LocaleHelper
```

---

## Settings

| Setting | Description |
|---|---|
| Manage Sources | Add or remove M3U playlist URLs |
| EPG Source URL | XMLTV URL for TV Guide data |
| Startup Section | Which section opens on launch |
| Player Quality | Auto / Best / Data Saver |
| Resume on startup | Auto-play the last watched channel |
| Language | App UI language |
| Clear Cache | Wipe EPG data and image cache |
| Clear All Playlists | Remove all saved sources |

---

## Forking

| What to change | Where |
|---|---|
| App name | `res/values/strings.xml` → `app_name` |
| Package name | `app/build.gradle` → `applicationId`, rename source tree |
| Accent color | `res/values/colors.xml` → `accent` |
| App icon | Replace drawables in `res/drawable-xhdpi/` |
| Developer credit | `CreditsActivity.kt` |
| Signing key | Replace `app/bbc.keystore` and update `build.gradle` credentials |

---

## Contributing

PRs welcome. Keep changes focused — one fix or feature per PR.

---

## License

Apache 2.0. See [LICENSE](LICENSE).

---

## Star History

<a href="https://www.star-history.com/?repos=unfamiliardev%2Fbbc&type=date&legend=top-left">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/chart?repos=unfamiliardev/bbc&type=date&theme=dark&legend=top-left" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/chart?repos=unfamiliardev/bbc&type=date&legend=top-left" />
   <img alt="Star History Chart" src="https://api.star-history.com/chart?repos=unfamiliardev/bbc&type=date&legend=top-left" />
 </picture>
</a>
