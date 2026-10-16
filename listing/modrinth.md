<center>

![ClientSort Paper](https://raw.githubusercontent.com/YONN2222/ClientSortPaper/main/assets/banner.png)

![Java 21+](https://img.shields.io/badge/Java-21+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Paper, Spigot, Folia](https://img.shields.io/badge/Paper%20%7C%20Spigot%20%7C%20Folia-2C2C2C?style=flat-square)
![Minecraft 1.21+](https://img.shields.io/badge/Minecraft-1.21+-DF8F40?style=flat-square)
![Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-3F55C9?style=flat-square)
[![Source code](https://img.shields.io/badge/Source%20code-GitHub-2C2C2C?style=flat-square&logo=github)](https://github.com/YONN2222/ClientSortPaper)
[![Documentation](https://img.shields.io/badge/Documentation-Docs-DF8F40?style=flat-square)](https://github.com/YONN2222/ClientSortPaper/tree/main/docs)
[![Issues](https://img.shields.io/badge/Issues-Report%20a%20bug-3F55C9?style=flat-square)](https://github.com/YONN2222/ClientSortPaper/issues)

</center>

**The server side of the [ClientSort mod](https://modrinth.com/project/K0AkAin6), made for Paper.**

ClientSort is a client mod for Fabric, NeoForge and Forge that sorts inventories and chests. It can also be installed on a server, where it makes sorting instant. That server part only exists as a mod, so a Paper server cannot use it. ClientSort Paper is that server part as a plugin: the same job, for Paper, Spigot, Purpur and Folia. Players keep the normal ClientSort mod, nothing changes for them.

Open source under Apache 2.0, no dependencies. This is an unofficial plugin and not affiliated with the ClientSort authors.

With the plugin on the server, the ClientSort mod sorts a chest or your inventory in one step instead of moving every item with a click. Sorting, fill stacks and move all are instant, also on busy servers.

## How it works

The mod works out the new order. The server checks the request and applies it. If anything looks wrong, for example a slot that does not exist or an item count that does not match afterwards, the server refuses or undoes it and the mod falls back to doing it itself, one click at a time. Nothing breaks, it is just slower.

## Features

- Works with the Fabric, NeoForge and Forge builds of ClientSort, no client setup
- Instant sort, fill stacks and move all, in your inventory and in containers
- Validation of every request, with a rollback if an item count does not match
- Choose which operations and inventory types are allowed, disable worlds, rate limit
- Shift + right click on an empty slot sorts for Java players without the mod, and `/sort` for everybody else
- Respects protection plugins: a cancelled click or right click event refuses the operation, and logging plugins see the item moves
- Menu for Bedrock players through Floodgate
- Cancellable event for other plugins
- 10 languages and every message is editable

## Commands

| Command | Permission |
|---|---|
| `/clientsort reload` | `clientsortpaper.reload` |
| `/clientsort status` | `clientsortpaper.status` |
| `/sort [inventory or container] [name or quantity]` | `clientsortpaper.use` |

`clientsortpaper.use` is on for everybody by default, the others for operators.

## Requirements

Minecraft 1.21 to 26.3 on Paper, Spigot, Purpur or Folia, and Java 21. Players need the [ClientSort mod](https://modrinth.com/project/K0AkAin6) installed.

## Links

- [Documentation](https://github.com/YONN2222/ClientSortPaper/tree/main/docs)
- [Source code](https://github.com/YONN2222/ClientSortPaper)
- [Issues](https://github.com/YONN2222/ClientSortPaper/issues)

ClientSort Paper sends anonymous usage statistics to bStats. Turn it off for all plugins in `plugins/bStats/config.yml`.
