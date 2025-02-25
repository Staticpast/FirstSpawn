# FirstSpawn

A Minecraft Spigot plugin that ensures new players spawn at a designated location when joining the server for the first time.

[![SpigotMC](https://img.shields.io/badge/SpigotMC-FirstSpawn-orange)](https://www.spigotmc.org/resources/firstspawn.122818/)
[![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg)](https://www.paypal.com/paypalme/mckenzio)

## Features

- 📍 Automatically teleports new players to a designated spawn location
- 🛏️ Sets the player's spawnpoint to the designated location
- 🎮 Simple admin commands to manage spawn location
- ⚙️ Configurable and persistent spawn location
- 🔄 Reload configuration without server restart
- 💡 Test teleport feature for administrators
- 🎛️ Enable/disable plugin functionality on the fly

## Commands

- `/firstspawn set` - Set the first spawn location to your current position
- `/firstspawn status` - Show current plugin settings and spawn location
- `/firstspawn test` - Test teleport to the spawn location
- `/firstspawn toggle` - Enable/disable the plugin
- `/firstspawn reload` - Reload the configuration

## Installation

1. Download from [Spigot](https://www.spigotmc.org/resources/firstspawn.122818/) or [GitHub Releases](https://github.com/McKenzieJDan/FirstSpawn/releases)
2. Place the .jar in your server's `plugins` folder
3. Restart your server
4. Use `/firstspawn set` to set the spawn location for new players

## Requirements

- Spigot/Paper 1.21.4
- Java 17+

## Configuration

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

## Development
To build the plugin yourself:

1. Clone the repository
2. Run `mvn clean package`
3. Find the built jar in the `target` folder

## Support
If you find this plugin helpful, consider [buying me a coffee](https://www.paypal.com/paypalme/mckenzio) ☕

## License

[MIT License](LICENSE)

Made with ❤️ by [McKenzieJDan](https://github.com/McKenzieJDan) 