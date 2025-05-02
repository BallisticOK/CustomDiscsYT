

# 🎶 Custom Discs v4.4 for Paper 1.21.5

> ⚠️ **Note:** This is an enhanced fork of [henkelmax's Audio Player](https://github.com/henkelmax/audio-player), featuring a new custom downloader system, improved UX, and ongoing support. Maintained by [Athar42](https://github.com/Athar42).


### ✅ Compatibility

* ✅ **Paper 1.21.5** (Recommended)
* ✅ **Paper 1.21.4** (Tested, works as well)
* ✅ **Paper 1.21.1** (Tested, works as well)

### 🎧 Features

* 🎵 Play custom music discs using the Simple Voice Chat API.
* 💽 Use `/customdisc` or `/cd` to view and manage your custom discs.
* 📁 Drop music files into `plugins/CustomDiscs/musicdata/`
* 📂 Supported formats: `.wav`, `.flac`, `.mp3`

---

### ⬇️ Downloading Music Files

To download and register a new song:

* Use the command: ❱ `/cd download <url> <filename.extension>`

💡 The URL must be a **direct download** link — the file should begin downloading immediately.

🔗 Helpful Link for Google Drive direct download formatting: [https://lonedev6.github.io/gddl/](https://lonedev6.github.io/gddl/)

💡 Example: ❱ `/cd download https://example.com/mysong mysong.mp3`

---

### 📡 Set Playback Range

Adjust how far your music disc can be heard:

* ❱ `/cd range <range>`

🧠 The value must be between 1 and the `music-disc-max-distance` (default: 256).

💡 Example: ❱ `/cd range 100`

---

### 🛡️ Permissions

You must have the following permission nodes to run certain commands:

* 🔹 `customdiscs.create` — Create discs
* 🔹 `customdiscs.download` — Download music
* 🔹 `customdiscs.range` — Set playback range

🎵 Note: Playing discs does **not** require a permission.

---

### 📦 Dependencies

You’ll need the following plugins installed:

* ✅ [ProtocolLib (latest for 1.21)](https://www.spigotmc.org/resources/protocollib.1997/)
* ✅ [SimpleVoiceChatBukkit v2.5.16](https://modrinth.com/plugin/simple-voice-chat)

---

### 🎥 Demo
https://user-images.githubusercontent.com/64107368/178426026-c454ac66-5133-4f3a-9af9-7f674e022423.mp4
---

### ⚙️ Default Config.yml

```yaml
# [Music Disc Config]

# The distance from which music discs can be heard in blocks.
music-disc-distance: 16

# The max distance from which music discs can be heard in blocks.
music-disc-max-distance: 256

# The master volume of music discs from 0-1. (You can set values like 0.5 for 50% volume).
music-disc-volume: 1

# The maximum download size in megabytes.
max-download-size: 50

#Custom Discs Help Page
help:
  - "&8-[&6CustomDiscs Help Page&8]-"
  - "&aAuthor&7: &6Navoei"
  - "&aContributors&7: &6alfw / &6Athar42"
  - "&fGit&0Hub&7: &9&ohttps://github.com/Navoei/CustomDiscs"
```

---

### 🈯 Default Lang.yml

```yaml
prefix: "&8[&6CustomDiscs&8]&r"
no-permission: "&cYou do not have permission to execute this command."
invalid-filename: "&cThis is an invalid filename!"
no-disc-name-provided: "&cYou must provide a name for your disc."
invalid-format: "&cFile must be in wav, flac, or mp3 format!"
file-not-found: "&cFile not found!"
invalid-arguments: "&cInvalid arguments. &7(&a%command_syntax%&7)"
not-holding-disc: "&cYou must hold a disc in your main hand."
create-filename: "&7Your filename is: &a\"%filename%\"."
create-custom-name: "&7Your custom name is: &a\"%custom_name%\"."
downloading-file: "&7Downloading file..."
file-too-large: "&cThe file is larger than %max_download_size%MB."
successful-download: "&aFile successfully downloaded to &7%file_path%&a."
create-disc: "&aCreate a disc by doing &7/cd create %filename% \"Custom Lore\"&a."
download-error: "&cAn error has occurred while downloading."
now-playing: "&6Now playing: %song_name%"
disc-converted: "&aConverted disc to new format! &fThis is due to changes in newer Minecraft versions which introduced &7JukeboxPlayableComponent&f."
invalid-range: "&cYou need to choose a range between 1 and %range_value%"
create-custom-range: "&7Your range is set to: &a\"%custom_range%\"."
```

---

### 🤝 Credits

* 🔧 Plugin Maintainer: [Athar42](https://github.com/Athar42)
* 🎨 Original Developer: [Henkelmax](https://github.com/henkelmax)
* 💖 Additional Fixes by: Navoei, alfw

---
