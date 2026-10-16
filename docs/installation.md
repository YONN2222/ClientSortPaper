<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/install-dark.svg">
    <img src="../assets/icons/install-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Installation</h1>

<p align="center">Get ClientSort Paper running in a minute.</p>

<p align="center"><a href="README.md">All docs</a></p>

## Requirements

- A Bukkit based server on Minecraft 1.21 or newer: Paper, Spigot, Purpur, Folia and forks of them
- Java 21 or newer
- The players need the [ClientSort mod](https://modrinth.com/project/K0AkAin6) on their side (Fabric, NeoForge or Forge). Without the mod there is nothing that talks to the plugin, see [/sort](commands.md#sort) for players without it.

The same jar runs everywhere. The plugin only uses the Bukkit API, no Paper only code. Proxies like Velocity or BungeeCord and mod loaders like Fabric or Forge on the server side are not supported.

## Steps

1. Download the jar from [Modrinth](https://modrinth.com/plugin/clientsort-paper) or the [GitHub releases](https://github.com/YONN2222/ClientSortPaper/releases).
2. Put it into the `plugins` folder.
3. Start the server. The plugin creates `plugins/ClientSortPaper/config.yml` and a `lang` folder.
4. Join with the ClientSort mod installed and sort a chest. The server now does the work in one step.

## Checking that it works

Run this in game or on the console:

```
/clientsort status
```

In game it tells you whether your client announced the ClientSort mod. On the console it shows how many players have it. The server log also prints `Listening for the ClientSort mod on 4 channels` at startup.

## Updating

Replace the jar and restart the server. New options are not added to an existing `config.yml` automatically, compare it with the [configuration reference](configuration.md) after an update.
