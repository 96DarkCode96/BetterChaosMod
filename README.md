# Better Chaos Mod

Better Chaos Mod is a Fabric mod inspired by the chaos systems from GTA V and other classic chaos-style gameplay experiences. It adds unpredictable, entertaining events that can turn a normal Minecraft session into a wild and chaotic challenge.

## Features

- Twitch chat voting integration
- Kick chat voting integration
- Built-in OBS web overlay
- Event system with randomized chaos effects
- Local web endpoints for live poll and chat views

### Example events

- Deaf
- Drop Inventory
- Damage Items
- Force Sneak
- Inverted Controls
- and many more

More examples are listed in the project documentation and event pages maintained for the mod.

## OBS overlay

The mod includes a local web server on `localhost:9696` with endpoints such as:

- `/poll` — current poll voting view
- `/chat` — filtered chat view without voting spam

The overlay is easy to customize through CSS, JavaScript, and WebSocket hooks.

## Requirements

- Fabric Loader
- Fabric API
- Java 25+
- Minecraft version supported by the project's Fabric configuration

## Installation

1. Download the mod jar from a release or build artifact.
2. Place it in your Minecraft `mods` folder.
3. Ensure Fabric and required dependencies are installed.
4. Launch the game and enjoy the chaos.

## Development

To build the project locally:

```bash
./gradlew build
```

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening issues or pull requests.

## Support

If you want to support the project, you can visit the developer's Ko-fi page:

- https://ko-fi.com/96darkcode96

## License

This project is licensed under the CC0-1.0 license. See the [LICENSE](LICENSE) file for details.
