<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/config-dark.svg">
    <img src="../assets/icons/config-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Configuration reference</h1>

<p align="center">Every option and its default.</p>

<p align="center"><a href="README.md">All docs</a></p>

The file is `plugins/ClientSortPaper/config.yml`. Apply changes with `/clientsort reload`.

```yaml
language: en

operations:
  sort: true
  stack-fill: true
  transfer: true

inventories:
  allowed-types: [CHEST, ENDER_CHEST, SHULKER_BOX, BARREL, HOPPER, DISPENSER, DROPPER]
  allow-plugin-menus: false

disabled-worlds: []

rate-limit:
  max-per-second: 20

middle-click:
  enabled: true
  order: name

bedrock-menu:
  enabled: true

sort-command:
  enabled: true

log-operations: false
```

| Option | Default | Meaning |
|---|---|---|
| `language` | `en` | Language file for `/sort` and `/clientsort`, see [Languages](languages.md). The mod shows its own texts |
| `operations.sort` | `true` | Sorting, which includes the collect step before it |
| `operations.stack-fill` | `true` | Fill stacks |
| `operations.transfer` | `true` | Move all |
| `inventories.allowed-types` | see above | Names of the Bukkit `InventoryType` enum that operations may touch besides the player's own inventory |
| `inventories.allow-plugin-menus` | `false` | Allow menus made by other plugins |
| `disabled-worlds` | empty | Worlds where every operation is refused |
| `rate-limit.max-per-second` | `20` | Most requests per player per second, `0` switches the limit off |
| `middle-click.enabled` | `true` | Middle click sorting for players without the mod |
| `middle-click.order` | `name` | `name` or `quantity` (largest stacks first) |
| `bedrock-menu.enabled` | `true` | The `/sort` form for Bedrock players, needs Floodgate |
| `sort-command.enabled` | `true` | The `/sort` command |
| `log-operations` | `false` | Write every performed operation to the console |

An unknown value is reported in the console and replaced by the default. A refused operation makes the client fall back to doing it itself, so switching an operation off never blocks players, it only makes it slower.
