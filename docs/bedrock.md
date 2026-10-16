<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/bedrock-dark.svg">
    <img src="../assets/icons/bedrock-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Bedrock and Floodgate</h1>

<p align="center">A sorting menu for players who join through Geyser.</p>

<p align="center"><a href="README.md">All docs</a></p>

Bedrock players cannot install the ClientSort mod and cannot use the click shortcut. With [Floodgate](https://geysermc.org/wiki/floodgate/) installed, ClientSort Paper gives them a menu instead.

## Setup

1. Install [Geyser](https://geysermc.org/) and Floodgate on the server, as you would for Bedrock support anyway.
2. Install ClientSort Paper. Floodgate is an optional dependency, no setup is needed on top.
3. Check the console at startup. It should print `Floodgate found, Bedrock players get a sorting menu with /sort`.

Without Floodgate the plugin cannot tell Bedrock players apart, `/sort` then works as a plain command for everybody and there is no menu.

## What players see

A Bedrock player types `/sort` and gets a form with four buttons:

| Button | What it sorts |
|---|---|
| Inventory by name | The inventory, without the hotbar, alphabetically |
| Inventory by quantity | The inventory, largest stacks first |
| Container in front of you by name | The container the player looks at, alphabetically |
| Container in front of you by quantity | The container the player looks at, largest stacks first |

The container does not have to be open, the player only has to look at it from up to 6 blocks away. The result is shown in chat. The texts of the form are part of the [language files](languages.md), the keys start with `form-`.

## Safety

The same checks as for every other sort apply. Before the container is touched, the server asks other plugins as if the player right clicked it, so WorldGuard, claim plugins and locks can refuse it. Locked containers are refused. Only the inventory types on `inventories.allowed-types` can be sorted. The item counts are checked before and after, and everything is undone if they do not match.

## Options

| Option | Default | Meaning |
|---|---|---|
| `bedrock-menu.enabled` | `true` | Show the form. Turn it off to keep `/sort` as a plain command |
| `sort-command.enabled` | `true` | If the command is off, there is no menu either |

Players need the permission `clientsortpaper.use`.

## Known limits

- Bedrock players cannot use the click shortcut, see [Commands](commands.md#click-shortcut)
- The menu uses the Floodgate form API and needs a Floodgate version that supports it, 2.2 or newer
