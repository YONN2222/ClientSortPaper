<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/operations-dark.svg">
    <img src="../assets/icons/operations-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Operations</h1>

<p align="center">What the ClientSort mod asks the server to do.</p>

<p align="center"><a href="README.md">All docs</a></p>

The mod works out what the result should look like. The server checks the request and applies it. The plugin never decides the order, it only accepts a reordering that is valid.

| Operation | Mod feature | What the server does |
|---|---|---|
| Collect | First step of sorting | Merges partial stacks of the same item, in the slots the client names |
| Sort | Sort button, key or middle mouse button | Moves items between slots according to the client's order. Only a pure reordering is accepted |
| Stack fill | Fill stacks | Tops up partial stacks in the target from the source. Empty target slots are never used |
| Transfer | Move all, shift click all | Moves items from one inventory to the other, topping up stacks first and then using empty slots |

The mod sends Collect, waits for the answer and then sends Sort. The server answers every request with a result, so the client knows whether to fall back.

## Without the mod

Players without the mod can middle click a slot to sort, see [Commands](commands.md#middle-click). It uses the same checks and the same rollback as the mod requests.

## Which inventories

Your own inventory is always allowed. Everything else has to be on `inventories.allowed-types` in the config. The default is chests, ender chests, shulker boxes, barrels, hoppers, dispensers and droppers.

Not on the list, on purpose: crafting tables, furnaces, anvils, villagers, brewing stands and everything else with special slots. Menus made by other plugins (shops, GUIs) look like chests but their slots are buttons. They are refused unless `inventories.allow-plugin-menus` is `true`.

## What the plugin checks

- The player has `clientsortpaper.use`, is not a spectator and is not in a disabled world
- The container id of the request is the container the player really has open
- Every slot id exists, is a normal storage slot and appears only once
- A sort request is a real permutation: every source slot is also a destination, nothing is created or deleted
- Before every operation in an open container, a real click event is fired for the player. Protection and logging plugins see it like a normal click: if a plugin cancels it, the operation is refused, and plugins that log container changes, such as CoreProtect, can see the change
- Sorting a container the player only looks at fires a right click on that block first, so claim and region plugins such as WorldGuard can refuse it, and locked containers are refused
- Shulker boxes are never moved into shulker boxes
- A rate limit per player, 20 requests a second by default
- The items before and after are counted. If anything was lost or duplicated, everything is put back and the client gets an error

Other plugins can cancel an operation, see [Developer API](api.md).

## When the server says no

The client gets a result code and falls back to doing the operation itself, one click at a time, unless the player turned that off in the mod. Nothing breaks, it is just slower.

| Code | Name | Typical reason |
|---|---|---|
| 0 | SUCCESS | Done |
| 1 | FAILURE | Rate limit reached or an unexpected error |
| 2 | UNSUPPORTED_OP | Switched off, no permission, inventory type not allowed |
| 3 | INVALID_DATA | Wrong container id, bad slot, not a valid reordering |
| 4 | INCONSISTENT_STATE | The check failed and everything was rolled back |
