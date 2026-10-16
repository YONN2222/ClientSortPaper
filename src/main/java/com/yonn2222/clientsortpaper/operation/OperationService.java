package com.yonn2222.clientsortpaper.operation;

import com.yonn2222.clientsortpaper.config.Settings;
import com.yonn2222.clientsortpaper.event.ClientSortOperationEvent;
import com.yonn2222.clientsortpaper.network.PayloadException;
import com.yonn2222.clientsortpaper.network.PayloadResult;
import com.yonn2222.clientsortpaper.network.Request;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.Lockable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.logging.Logger;

public final class OperationService {
    private static final String USE_PERMISSION = "clientsortpaper.use";

    private final Logger logger;
    private final RateLimiter limiter = new RateLimiter();
    private volatile Settings settings;
    private volatile Access access;

    public OperationService(Logger logger, Settings settings) {
        this.logger = logger;
        apply(settings);
    }

    public void apply(Settings settings) {
        this.access = new Access(settings.allowedInventories(), settings.allowPluginMenus());
        this.settings = settings;
    }

    public Settings settings() {
        return settings;
    }

    public void forget(Player player) {
        limiter.forget(player.getUniqueId());
    }

    public void execute(Player player, Request request) throws PayloadException {
        Settings current = settings;
        check(player, request.operation(), current);
        SlotSource source = source(player, request.containerId());

        switch (request) {
            case Request.Collect collect -> {
                Cell[] cells = SlotValidation.array(source, collect.slotIds());
                run(player, request, cells, null, current, () -> Operations.collect(cells));
            }
            case Request.Sort sort -> {
                Cell[][] pairs = SlotValidation.mapping(source, sort.slotMapping());
                run(player, request, pairs[0], pairs[1], current, () -> Operations.sort(pairs[0], pairs[1]));
            }
            case Request.StackFill fill -> {
                Cell[] from = SlotValidation.array(source, fill.sourceSlots());
                Cell[] to = SlotValidation.array(source, fill.destinationSlots());
                run(player, request, from, to, current, () -> Operations.stackFill(from, to, fill.reversed()));
            }
            case Request.Transfer transfer -> {
                Cell[] from = SlotValidation.array(source, transfer.sourceSlots());
                Cell[] to = SlotValidation.array(source, transfer.destinationSlots());
                run(player, request, from, to, current, () -> Operations.transfer(from, to, transfer.reversed()));
            }
        }
    }

    private void check(Player player, Operation operation, Settings current) throws PayloadException {
        if (player.getGameMode() == GameMode.SPECTATOR) {
            throw PayloadException.unsupported("This operation is not available in spectator mode");
        }
        if (!player.hasPermission(USE_PERMISSION)) {
            throw PayloadException.unsupported("You do not have permission to use ClientSort on this server");
        }
        if (!current.isEnabled(operation)) {
            throw PayloadException.unsupported("The " + operation.label() + " operation is disabled on this server");
        }
        if (current.disabledWorlds().contains(player.getWorld().getName())) {
            throw PayloadException.unsupported("ClientSort is disabled in this world");
        }
        if (!limiter.allow(player.getUniqueId(), current.maxPerSecond())) {
            throw new PayloadException(PayloadResult.FAILURE, "Too many requests, slow down");
        }
    }

    private SlotSource source(Player player, int containerId) throws PayloadException {
        if (containerId == 0) {
            return new PlayerInventorySource(player.getInventory());
        }
        InventoryView view = player.getOpenInventory();
        OptionalInt open = MenuIds.current(player, logger);
        if (open.isPresent()) {
            if (open.getAsInt() != containerId) {
                throw PayloadException.invalid("Container id " + containerId + " does not match the open container");
            }
        } else if (view.getType() == InventoryType.CRAFTING || view.getType() == InventoryType.CREATIVE) {
            throw PayloadException.invalid("No container is open");
        }
        return new ViewSource(view, access);
    }

    private void run(Player player, Request request, Cell[] first, Cell[] second, Settings current, Runnable change)
            throws PayloadException {
        Inventory source = inventoryOf(first);
        Inventory target = second == null ? source : inventoryOf(second);
        ClientSortOperationEvent event = new ClientSortOperationEvent(player, request.operation(), source, target);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            throw PayloadException.unsupported("The operation was cancelled by another plugin");
        }

        approve(player, request.operation(), first, second);

        List<Cell> involved = new ArrayList<>(List.of(first));
        if (second != null) {
            involved.addAll(List.of(second));
        }
        Operations.guarded(involved, change);
        player.updateInventory();
        if (current.logOperations()) {
            logger.info(player.getName() + " " + request.operation().label() + " " + describe(source, target));
        }
    }

    private void approve(Player player, Operation operation, Cell[] first, Cell[] second) throws PayloadException {
        InventoryView view = player.getOpenInventory();
        Inventory top = view.getTopInventory();
        BukkitCell probe = null;
        for (Cell[] group : new Cell[][]{first, second}) {
            if (group == null) {
                continue;
            }
            for (Cell cell : group) {
                if (cell instanceof BukkitCell bukkit && bukkit.inventory() == top && top.getType() != InventoryType.PLAYER) {
                    probe = bukkit;
                    break;
                }
            }
            if (probe != null) {
                break;
            }
        }
        if (probe == null) {
            return;
        }
        boolean move = operation == Operation.TRANSFER || operation == Operation.STACK_FILL;
        InventoryClickEvent click = new InventoryClickEvent(view, InventoryType.SlotType.CONTAINER, probe.index(),
                move ? ClickType.SHIFT_LEFT : ClickType.LEFT,
                move ? InventoryAction.MOVE_TO_OTHER_INVENTORY : InventoryAction.PICKUP_ALL);
        Bukkit.getPluginManager().callEvent(click);
        if (click.isCancelled()) {
            throw PayloadException.unsupported("The operation was cancelled by another plugin");
        }
    }

    public boolean hasOpenContainer(Player player) {
        return access.allows(player.getOpenInventory().getTopInventory());
    }

    public boolean hasSortableContainer(Player player) {
        return hasOpenContainer(player) || containerInSight(player) != null;
    }

    private Inventory containerInSight(Player player) {
        Block block = player.getTargetBlockExact(6);
        if (block == null || !(block.getState() instanceof Container state)) {
            return null;
        }
        if (state instanceof Lockable lockable && lockable.isLocked()) {
            return null;
        }
        Inventory inventory = state.getInventory();
        return access.allows(inventory) ? inventory : null;
    }

    private Inventory resolveContainer(Player player) throws PayloadException {
        Inventory open = player.getOpenInventory().getTopInventory();
        if (access.allows(open)) {
            return open;
        }
        Inventory inSight = containerInSight(player);
        if (inSight == null) {
            throw PayloadException.unsupported("No supported container is open or in sight");
        }
        Block block = player.getTargetBlockExact(6);
        PlayerInteractEvent interact = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK,
                player.getInventory().getItemInMainHand(), block, BlockFace.UP);
        Bukkit.getPluginManager().callEvent(interact);
        if (interact.useInteractedBlock() == Event.Result.DENY) {
            throw PayloadException.unsupported("You are not allowed to use this container");
        }
        return inSight;
    }

    public boolean sortManually(Player player, boolean container, Order order) throws PayloadException {
        Settings current = settings;
        check(player, Operation.SORT, current);
        Inventory inventory;
        int first;
        int last;
        if (container) {
            inventory = resolveContainer(player);
            first = 0;
            last = inventory.getSize();
        } else {
            inventory = player.getInventory();
            first = 9;
            last = 36;
        }
        Cell[] cells = new Cell[last - first];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new BukkitCell(inventory, first + i);
        }

        ClientSortOperationEvent event = new ClientSortOperationEvent(player, Operation.SORT, inventory, inventory);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            throw PayloadException.unsupported("The operation was cancelled by another plugin");
        }

        approve(player, Operation.SORT, cells, null);

        boolean[] anything = {false};
        Operations.guarded(List.of(cells), () -> {
            Operations.collect(cells);
            Integer[] sorted = new Integer[cells.length];
            ItemStack[] items = new ItemStack[cells.length];
            for (int i = 0; i < cells.length; i++) {
                sorted[i] = i;
                items[i] = cells[i].get();
                anything[0] |= items[i] != null;
            }

            Comparator<Integer> byOrder = (a, b) -> {
                if (items[a] == null || items[b] == null) {
                    return items[a] == null ? (items[b] == null ? 0 : 1) : -1;
                }
                return order.comparator().compare(items[a], items[b]);
            };
            java.util.Arrays.sort(sorted, byOrder);
            Cell[] from = new Cell[cells.length];
            for (int i = 0; i < cells.length; i++) {
                from[i] = cells[sorted[i]];
            }
            Operations.sort(from, cells);
        });
        player.updateInventory();
        return anything[0];
    }

    private static Inventory inventoryOf(Cell[] cells) {
        return cells[0] instanceof BukkitCell cell ? cell.inventory() : null;
    }

    private static String describe(Inventory source, Inventory target) {
        if (source == null || source == target) {
            return source == null ? "" : source.getType().name().toLowerCase(Locale.ROOT);
        }
        return source.getType().name().toLowerCase(Locale.ROOT) + " -> " + target.getType().name().toLowerCase(Locale.ROOT);
    }
}
