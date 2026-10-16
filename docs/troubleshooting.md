<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/help-dark.svg">
    <img src="../assets/icons/help-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Troubleshooting</h1>

<p align="center">Common questions and fixes.</p>

<p align="center"><a href="README.md">All docs</a></p>

## Nothing happens, sorting is still slow

The ClientSort mod only uses the server when it knows the server can do it. Check, in this order:

1. The log shows `Listening for the ClientSort mod on 4 channels` at startup. If not, the plugin did not load, read the log above that line.
2. `/clientsort status` in game says your client has the mod. If it does not, rejoin once. If it still does not, the mod is too old or has its server communication switched off.
3. The player has `clientsortpaper.use`.
4. The operation is on in `config.yml` and the world is not in `disabled-worlds`.

## It works in my inventory but not in a chest

The inventory type must be on `inventories.allowed-types`. Chests of other plugins, shops and GUI menus are refused by default, see [Operations](operations.md#which-inventories).

## "Too many requests, slow down"

The rate limit is 20 requests a second per player. A normal player never reaches it. Raise `rate-limit.max-per-second` or set it to `0` if you have a macro or a test setup.

## Items got shuffled or something looks wrong

Every operation is counted before and after and undone when the counts differ, so items cannot be lost or duplicated by the plugin. If a container looks wrong, close and reopen it. The client shows what it expects until the server sends the real content. Please open an issue with the server version and, if you can, `log-operations: true` output.

## Bedrock players cannot sort

They have no mod and no middle click. They can use `/sort` or, with Floodgate, the menu behind it, see [Commands](commands.md#sort).

## The plugin does not load

- Java 21 or newer is needed
- Minecraft 1.21 or newer is needed
- Read the first error in the log. Wrong values in `config.yml` are reported as warnings and replaced by the defaults, a broken YAML file stops the plugin from starting
