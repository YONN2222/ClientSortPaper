# Changelog

## [1.0.0]

- Server side support for the ClientSort mod: collect, sort, stack fill and transfer, speaking the mod's `clientsort` protocol
- Works on Paper, Spigot, Purpur and Folia, Minecraft 1.21 to 26.3, with only the Bukkit API
- Every request is validated: container id, slot ids, valid reordering, allowed inventory types, no shulker box in a shulker box
- Item counts are checked before and after, a failed operation is rolled back and the client falls back to clicking
- Middle click sorting for players without the mod, in containers and in the player inventory, with a configurable order
- Every operation fires a real click event first, so protection plugins can refuse it and logging plugins see the moves. Sorting a container the player looks at fires a right click event and refuses locked containers
- Optional Floodgate menu for Bedrock players, `/sort container` also works on the container in sight
- `/sort [inventory|container] [name|quantity]` for players without the mod, for example Bedrock through Geyser
- `/clientsort reload` and `/clientsort status`
- Config: operations on or off, allowed inventory types, plugin menus, disabled worlds, rate limit, console log
- Permissions: `clientsortpaper.use`, `.reload`, `.status`
- Cancellable `ClientSortOperationEvent` for other plugins
- Messages in 10 languages: en, zh, es, hi, fr, ar, pt, ru, de, ja
- Optional bStats metrics, disabled globally in the bStats config
