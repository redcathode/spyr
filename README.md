# I abandoned this project a while back.
I started working on this to teach myself Java, and overall I'm pretty proud of the results, but, in the end, it's not a serious app. It's a fun, educational project that I picked up mainly for fun. I don't know of anyone using it, and I've switched to [Pithos](https://pithos.github.io) for music streaming, so I don't see any reason to keep maintaining it. Anyway, here's the rest of the README:
  
  
------------
  
  
For a tutorial on how to set up, run, and use Spyr, click [here.](https://github.com/asolidtime/spyr/blob/main/TUTORIAL.md) Otherwise, continue scrolling.

# Spyr

Spyr (sounds like 'spire') is a music streaming client built in Java and Swing that was inspired by (and aims to be a replacement to) the music streaming bots popular on Discord. It's also an effort for me to teach myself more about Java.

**Alright, where can I get it?**

You can grab a stable release from [here.](https://github.com/asolidtime/spyr/releases) If you want the absolute latest version, click [this link.](https://nightly.link/asolidtime/spyr/workflows/github-actions/main/JAR%20package.zip) Note that both require [Java 8 or newer](https://adoptopenjdk.net/?variant=openjdk16&jvmVariant=hotspot) and [the latest version of VLC](https://www.videolan.org/vlc/) to be installed.

## Screenshots!
![Light Mode (with settings open)](https://raw.githubusercontent.com/asolidtime/spyr/main/images/lightmode.png)
![Dark Mode (as you can see, I really like Tally Hall](https://raw.githubusercontent.com/asolidtime/spyr/main/images/darkmode.png)

## Roadmap (in no particular order)

- [x] Basic audio playback from Youtube
- [ ] notify the user if VLC isn't installed, and possibly try to bundle libvlc with the jar
- [ ] An icon!
- [x] Support Youtube livestreams
- [x] Screenshots! (and just a bette readme in general)
- [ ] Search for songs (preferably without needing a rate-limited API)
- [x] Support tracks from other platforms
- [ ] Support playlists
- [x] Show frequently played songs (and playlists once that gets included)
- [ ] Show previous listening sessions
- [ ] Show track thumbnail/info
- [ ] Integrate with system media playback controls
- [ ] Support drag n' dropping links from outside apps
- [ ] Support hosting a 'listening party'
- [ ] Asynchronously add songs (or, at least, add them without pausing the entire app's rendering)
