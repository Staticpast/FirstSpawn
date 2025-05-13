# FirstSpawn

A Minecraft Spigot plugin that ensures new players spawn at a designated location when joining the server for the first time.

[![SpigotMC](https://img.shields.io/badge/SpigotMC-FirstSpawn-orange)](https://www.spigotmc.org/resources/firstspawn.122818/)
[![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg)](https://www.paypal.com/paypalme/mckenzio)

## Features

* 📍 Automatically teleports new players to a designated spawn location
* 🛏️ Sets the player's spawnpoint to the designated location
* 🎮 Simple admin commands to manage spawn location
* ⚙️ Configurable and persistent spawn location
* 🔄 Reload configuration without server restart
* 💡 Test teleport feature for administrators
* 🎛️ Enable/disable plugin functionality on the fly

## Installation

1. Download the latest release from [Spigot](https://www.spigotmc.org/resources/firstspawn.122818/) or [GitHub Releases](https://github.com/McKenzieJDan/FirstSpawn/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in the `config.yml` file

## Usage

The plugin automatically detects when a player joins the server for the first time and teleports them to the designated spawn location. No player action is required.

### Commands

* `/firstspawn set` - Set the first spawn location to your current position
* `/firstspawn status` - Show current plugin settings and spawn location
* `/firstspawn test` - Test teleport to the spawn location
* `/firstspawn toggle` - Enable/disable the plugin
* `/firstspawn reload` - Reload the configuration

### Permissions

* `firstspawn.admin` - Access to all FirstSpawn commands (default: op)

## Configuration

The plugin's configuration file (`config.yml`) is organized into logical sections:

```yaml
# Enable or disable the plugin
enabled: true

# First spawn location coordinates
firstSpawn:
  world: world
  x: -115
  y: 65
  z: -60
```

For detailed configuration options, see the comments in the generated config.yml file.

## Requirements

- Spigot/Paper 1.21.5
- Java 21+

## Used By

- [SuegoFaults](https://suegofaults.com) - A curated adult Minecraft community where this plugin ensures new players land in a welcoming, consistent starting location.

## Support

If you find this plugin helpful, consider [buying me a coffee](https://www.paypal.com/paypalme/mckenzio) ☕

## License

[MIT License](LICENSE)

Made with ❤️ by [McKenzieJDan](https://github.com/McKenzieJDan) 