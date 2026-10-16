<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="assets/clientsortpaper-lockup-dark.svg">
    <img src="assets/clientsortpaper-lockup-light.svg" alt="ClientSort Paper" height="90">
  </picture>
</p>

<p align="center"><b>Instant server side sorting for the ClientSort mod.</b></p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21+">
  <img src="https://img.shields.io/badge/Paper%20%7C%20Spigot%20%7C%20Folia-2C2C2C?style=for-the-badge&logoColor=white" alt="Paper, Spigot, Folia">
  <img src="https://img.shields.io/badge/Minecraft-1.21+-DF8F40?style=for-the-badge&logoColor=white" alt="Minecraft 1.21+">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-3F55C9?style=for-the-badge" alt="Apache 2.0"></a>
</p>

<p align="center">
  <a href="https://modrinth.com/plugin/clientsort-paper">Modrinth</a>
  <a href="https://github.com/YONN2222/ClientSortPaper/releases">GitHub Releases</a>
</p>

> ClientSort Paper is the server side of the [ClientSort mod](https://modrinth.com/project/K0AkAin6), made for Paper. One jar runs on Paper, Spigot, Purpur and Folia. Open source, no dependencies.

ClientSort is a client mod for Fabric, NeoForge and Forge that sorts inventories and chests. It can also be installed on a server, where it makes sorting instant, but that server part only exists as a mod. This plugin is that server part for plugin servers. Players keep the normal ClientSort mod, nothing changes for them.

Without it the mod sorts by moving items one click at a time, which is slow and visible on a server. With the plugin the mod sends one request and the server rearranges everything at once. Sorting, filling stacks and moving everything between two inventories become instant.

This is an unofficial plugin. It is not made by or affiliated with the ClientSort authors, see [NOTICE](NOTICE).

## Features

- Works with the Fabric, NeoForge and Forge builds of the ClientSort mod, no setup on the client
- Instant sort, fill stacks and move all, in your own inventory and in containers
- The server checks every request: slots, container, permutation, shulker nesting, item counts
- Everything is undone if the item count before and after does not match
- Switch single operations off, pick allowed inventories, disable worlds, rate limit
- Shift + right click on an empty slot sorts for Java players without the mod, and `/sort` for everybody else
- Respects protection plugins: a cancelled click or right click event refuses the operation, and plugins like CoreProtect see the item moves
- Menu for Bedrock players through Floodgate
- Cancellable event for other plugins
- 10 languages and every message is editable
- One jar for Paper, Spigot, Purpur and Folia, Minecraft 1.21 to 26.3

## Requirements

- A Bukkit based server on Minecraft 1.21 or newer: Paper, Spigot, Purpur, Folia and forks of them
- Java 21 or newer
- Players need the [ClientSort mod](https://modrinth.com/project/K0AkAin6) to use the instant operations

## Quick start

1. Put the jar into `plugins` and start the server.
2. Join with the ClientSort mod and sort a chest.
3. Run `/clientsort status` to see who has the mod.

Everybody may use it by default. See [Installation](docs/installation.md) for details.

## Documentation

| | Topic | About |
|---|---|---|
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/install-dark.svg"><img src="assets/icons/install-light.svg" alt="" width="40"></picture> | [Installation](docs/installation.md) | Requirements and first steps |
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/commands-dark.svg"><img src="assets/icons/commands-light.svg" alt="" width="40"></picture> | [Commands and permissions](docs/commands.md) | /clientsort, /sort, permissions |
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/operations-dark.svg"><img src="assets/icons/operations-light.svg" alt="" width="40"></picture> | [Operations](docs/operations.md) | What the mod asks and what the server checks |
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/config-dark.svg"><img src="assets/icons/config-light.svg" alt="" width="40"></picture> | [Configuration reference](docs/configuration.md) | Every option and its default |
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/compat-dark.svg"><img src="assets/icons/compat-light.svg" alt="" width="40"></picture> | [Compatibility](docs/compatibility.md) | Platforms, versions, Geyser, the mod |
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/languages-dark.svg"><img src="assets/icons/languages-light.svg" alt="" width="40"></picture> | [Languages](docs/languages.md) | Ten languages, custom texts, placeholders |
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/help-dark.svg"><img src="assets/icons/help-light.svg" alt="" width="40"></picture> | [Troubleshooting](docs/troubleshooting.md) | Common problems |
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/bedrock-dark.svg"><img src="assets/icons/bedrock-light.svg" alt="" width="40"></picture> | [Bedrock and Floodgate](docs/bedrock.md) | Sorting menu for Geyser players |
| <picture><source media="(prefers-color-scheme: dark)" srcset="assets/icons/api-dark.svg"><img src="assets/icons/api-light.svg" alt="" width="40"></picture> | [Developer API](docs/api.md) | The cancellable operation event |

## Building

```
./gradlew build
```

The jar ends up in `build/libs`. Java 25 is used to build, the plugin itself targets Java 21.

## Privacy

ClientSort Paper reports anonymous usage statistics to [bStats](https://bstats.org/plugin/bukkit/ClientSortPaper/34731). Turn it off for all plugins in `plugins/bStats/config.yml`. Nothing else leaves your server.

## License and credits

Apache License 2.0, see [LICENSE](LICENSE) and [NOTICE](NOTICE). The plugin speaks the network protocol of the [ClientSort mod](https://github.com/TerminalMC/ClientSort) by TerminalMC, which is also Apache 2.0 licensed. It contains none of the mod's code.
