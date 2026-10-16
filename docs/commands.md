<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/commands-dark.svg">
    <img src="../assets/icons/commands-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Commands and permissions</h1>

<p align="center">What you can type and who may do it.</p>

<p align="center"><a href="README.md">All docs</a></p>

Most of the work happens without any command: the ClientSort mod sends its requests by itself. The commands are for administration and for players without the mod.

## /clientsort

Alias: `/csp`

| Command | What it does | Permission |
|---|---|---|
| `/clientsort reload` | Reloads `config.yml` and the language files | `clientsortpaper.reload` |
| `/clientsort status` | Shows how many players have the mod and whether you have it | `clientsortpaper.status` |

## Middle click

Players without the mod sort the same way the mod does: hover over a slot in a chest or in the inventory and press the middle mouse button. A click in the container sorts the container, a click in the player inventory sorts the inventory (not the hotbar). It needs `clientsortpaper.use` and does not work in creative mode, where the middle click copies an item. Players who have the mod are skipped, the mod handles the click itself.

Bedrock players have no middle click, they use `/sort` or the [Bedrock menu](bedrock.md). Switch the feature off or change the order in the `middle-click` section of the [configuration](configuration.md).

## /sort

The same sorting as a command, for players without the mod.

| Command | What it does |
|---|---|
| `/sort inventory` | Sorts your own inventory (not the hotbar) |
| `/sort container` | Sorts the container you are looking at, up to 6 blocks away |
| `/sort` | Sorts your inventory. Bedrock players with Floodgate get a [menu](bedrock.md) instead |
| `/sort ... quantity` | Largest stacks first. The default is by name |

The container does not have to be open. Before sorting, the server asks other plugins as if the player right clicked the chest, so protection plugins like WorldGuard can say no. Locked containers are refused.

## Permissions

| Permission | Default | Meaning |
|---|---|---|
| `clientsortpaper.use` | everyone | May use the server side operations and `/sort` |
| `clientsortpaper.reload` | operators | `/clientsort reload` |
| `clientsortpaper.status` | operators | `/clientsort status` |
| `clientsortpaper.*` | operators | All of the above |

Take `clientsortpaper.use` away from a group with any permissions plugin to keep the mod from using the server. The client then falls back to doing the operation itself, slot by slot, as it does without the plugin.
