> **Upgrading from v1.0.17 or earlier?** Uninstall the old app first, then install v1.0.18+. Android blocks installs when the signing key changes. You only need to do this once.

# BBC

[![Telegram](https://img.shields.io/badge/Telegram-bbciptvapp-2CA5E0?logo=telegram&logoColor=white)](https://t.me/bbciptvapp)

An open-source Android TV IPTV client. Add M3U playlists, browse live TV and VOD, and watch from your couch with full remote control support.

- M3U/M3U8 playlist support with automatic channel grouping
- Live TV and Movies & Series sections
- EPG / TV Guide via XMLTV
- ExoPlayer (Media3) — HLS, MPEG-TS, MP4
- Channel logos, favourites, recently watched
- Sleep timer, audio/subtitle track selector, aspect ratio toggle
- Stream info HUD, seek, channel number jump
- Picture-in-Picture
- Full D-pad navigation

**Requirements:** Android TV or Google TV, Android 5.0+

**Install:** grab the latest APK from [Releases](https://github.com/unfamiliardev/BBC/releases) and sideload via ADB or a file manager.

```sh
adb install bbc-debug.apk
```

**Build from source:**

```sh
git clone https://github.com/unfamiliardev/BBC.git
```

Open in Android Studio and run on a device or emulator.

**Player controls**

| Key | Action |
|---|---|
| OK / Play-Pause | Play / Pause (or retry on error) |
| Left / Right | Seek ±15s |
| MENU | Sleep timer, tracks, aspect ratio |
| INFO | Stream info HUD |
| Back | Exit player |

**Settings:** Manage Sources · EPG URL · Startup Section · Player Quality · Resume on Startup · Language · Clear Cache · Clear All Playlists

**License:** Apache 2.0 — not affiliated with the BBC (British Broadcasting Corporation).

---

## Star History

<a href="https://www.star-history.com/?repos=unfamiliardev%2Fbbc&type=date&legend=top-left">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/chart?repos=unfamiliardev/bbc&type=date&theme=dark&legend=top-left" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/chart?repos=unfamiliardev/bbc&type=date&legend=top-left" />
   <img alt="Star History Chart" src="https://api.star-history.com/chart?repos=unfamiliardev/bbc&type=date&legend=top-left" />
 </picture>
</a>
