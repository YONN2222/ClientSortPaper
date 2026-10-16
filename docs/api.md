<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="../assets/icons/api-dark.svg">
    <img src="../assets/icons/api-light.svg" alt="" width="96">
  </picture>
</p>

<h1 align="center">Developer API</h1>

<p align="center">Let other plugins say no.</p>

<p align="center"><a href="README.md">All docs</a></p>

ClientSort Paper fires a Bukkit event before it changes anything.

```java
@EventHandler
public void onSort(com.yonn2222.clientsortpaper.event.ClientSortOperationEvent event) {
    if (event.getOperation() == Operation.TRANSFER && isShopMenu(event.getPlayer())) {
        event.setCancelled(true); // the client gets UNSUPPORTED_OP and falls back
    }
}
```

| Method | Meaning |
|---|---|
| `getPlayer()` | The player who sent the request |
| `getOperation()` | `COLLECT`, `SORT`, `STACK_FILL` or `TRANSFER` |
| `getSource()` | The inventory the items come from |
| `getTarget()` | The inventory the items go to, the same as the source for collect and sort |

Before the event, ClientSort Paper also fires a normal `InventoryClickEvent` for the player (and a `PlayerInteractEvent` when sorting a container the player looks at), so most plugins need no special support.

The event runs on the thread that owns the player, which is the region thread on Folia. Item changes by other plugins during the event are not rolled back by ClientSort Paper, only its own changes are checked.
