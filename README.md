# musique-core

A headless Java/Kotlin audio playback library extracted from the musique project.

This library provides a clean, GUI-free API for audio decoding and playback,
suitable for embedding in applications, servers, or custom players.

## Supported Formats

- MP3 (via JLayer)
- OGG Vorbis
- FLAC
- WavPack
- Monkey's Audio (APE)
- WAV, AU, AIFF
- MP4/AAC/ALAC
- TrueAudio (TTA)
- CUE sheets

## Requirements

- Java 25+

## Building

```bash
./gradlew :musique-core:build
```

Or to just compile:
```bash
./gradlew :musique-core:compileKotlin compileJava
```

## Usage

### Kotlin

```kotlin
import com.tulskiy.musique.api.AudioPlayer
import com.tulskiy.musique.api.AudioPlayerListener
import com.tulskiy.musique.api.MediaItem
import com.tulskiy.musique.api.PlaybackState
import java.io.File

val player = AudioPlayer()

player.addListener(object : AudioPlayerListener {
    override fun onStateChanged(state: PlaybackState) {
        println("State: $state")
    }
})

val item = MediaItem.fromFile(File("/path/to/music.mp3"))
player.open(item)
player.play()

player.setVolume(0.8f)
player.pause()
player.play()
player.stop()

player.close()
```

### Java

```java
import com.tulskiy.musique.api.AudioPlayer;
import com.tulskiy.musique.api.MediaItem;
import java.io.File;

AudioPlayer player = new AudioPlayer();
MediaItem item = MediaItem.fromFile(new File("/path/to/music.mp3"));
player.open(item);
player.play();
Thread.sleep(5000);
player.stop();
player.close();
```

## Architecture

```
musique-core
├── com.tulskiy.musique.api          ← Public Kotlin API (AudioPlayer, MediaItem, PlaybackState)
├── com.tulskiy.musique.audio        ← Audio decoding infrastructure
│   ├── formats/                     ← Format decoders (MP3, FLAC, OGG, etc.)
│   └── player/                      ← Playback engine (buffering, output)
├── com.tulskiy.musique.playlist     ← Track, Playlist, PlaybackOrder
├── com.tulskiy.musique.system       ← Codecs registry, TrackIO
└── com.tulskiy.musique.util         ← Utilities
```

## License

LGPL 3.0 — see [Copying](Copying)
