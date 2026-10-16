<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/compat-dark.svg">
    <img src="../assets/icons/compat-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Compatibility</h1>

<p align="center">Platforms, versions, Geyser and the mod.</p>

<p align="center"><a href="README.md">All docs</a></p>

## Servers

| Platform | Status |
|---|---|
| Paper, Purpur and other Paper forks | Supported |
| Spigot, CraftBukkit | Supported. Only the Bukkit API is used, the container id check is skipped because it needs server internals |
| Folia | Marked as supported. Requests arrive on the thread that owns the player, so no scheduler is needed |
| Velocity, BungeeCord | Not applicable, install the plugin on the backend servers |

Minecraft 1.21 to 26.3 is supported.

## The mod

ClientSort Paper speaks the server protocol of the [ClientSort mod](https://modrinth.com/project/K0AkAin6) (Fabric, NeoForge and Forge). The channels are `clientsort:collect_c2s`, `sort_c2s`, `stack_fill_c2s` and `transfer_c2s`, answered on `..._result_s2c`. The mod only uses them when the server announces them, which the plugin does at join. A player with an old mod version keeps working, the mod just does the operations itself.

## Bedrock and Geyser

Bedrock players cannot install the mod and cannot use the click shortcut. They can use [/sort](commands.md#sort) and the Floodgate menu. Details: [Bedrock and Floodgate](bedrock.md).

## Protection and logging plugins

ClientSort Paper does not open anything by itself. A container has to be open already, which means the usual protection (WorldGuard, claim plugins, locks) already let the player in, or the plugin asks them with a right click event first. Every operation also fires an inventory click event that other plugins can cancel, which is what read only menus and protection plugins use. Logging plugins that watch click events, such as CoreProtect, see the item moves between inventories. A pure sort inside one container does not add or remove items, so there is nothing for them to log.

## Other plugins

Inventories of other plugins are left alone unless you allow them, see [Operations](operations.md#which-inventories). Anti cheat plugins that count inventory clicks see fewer clicks than without the plugin, because one request replaces dozens of clicks.
