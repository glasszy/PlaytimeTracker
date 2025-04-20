# PlaytimeLimiter (Spigot Plugin)

A Minecraft Spigot plugin that limits each player's daily playtime. Once a player exceeds the configured limit, they are automatically kicked. This is great for managing server load or encouraging healthy play habits.

## Features

- Configurable daily playtime limit
- Support for **multiple reset times** (e.g., 00:00 and 12:00)
- Warns players 1 minute before they're kicked
- Bypass/Ignore system for privileged players
- Commands for OPs to manage playtime
- Simple and efficient, with no persistent file storage

## Installation

1. Download the latest `.jar` file and place it in your server's `plugins/` folder.
2. Restart or reload the server.
3. Modify `config.yml` to your desired settings (see below).

## Configuration

```yaml
max-playtime: 120 # Maximum daily playtime in minutes

reset-times:
  - "00:00"
  - "12:00" # Add as many reset times as you want

```

## Commands

```yaml
commands:
  playtimecheck:
    description: Check your current playtime
  playtimelist:
    description: View all online players' playtimes (OP only)
  setplaytime:
    description: Set a player's current playtime in minutes (OP only)
  ignoreplaytime:
    description: Toggle whether a player ignores the time limit (OP only)
  ignoredplayers:
    description: View list of players ignoring the playtime limit (OP only)
```
