package com.yonn2222.clientsortpaper.operation;

import com.yonn2222.clientsortpaper.network.PayloadException;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;

public final class ViewSource implements SlotSource {
    private static final int PLAYER_STORAGE_SLOTS = 36;

    private final InventoryView view;
    private final Access access;

    public ViewSource(InventoryView view, Access access) {
        this.view = view;
        this.access = access;
    }

    @Override
    public Cell cell(int slotId) throws PayloadException {
        if (slotId < 0 || slotId >= view.countSlots()) {
            throw PayloadException.invalid("Slot " + slotId + " is outside the open container");
        }
        SlotType type = view.getSlotType(slotId);
        if (type != SlotType.CONTAINER && type != SlotType.QUICKBAR) {
            throw PayloadException.unsupported("Slot " + slotId + " is not a storage slot");
        }
        Inventory inventory = view.getInventory(slotId);
        if (inventory == null) {
            throw PayloadException.invalid("Slot " + slotId + " does not belong to an inventory");
        }
        int local = view.convertSlot(slotId);
        if (inventory instanceof PlayerInventory) {
            if (local < 0 || local >= PLAYER_STORAGE_SLOTS) {
                throw PayloadException.unsupported("Slot " + slotId + " is not a storage slot");
            }
        } else if (!access.allows(inventory)) {
            throw PayloadException.unsupported("This kind of inventory is not supported");
        }
        return new BukkitCell(inventory, local);
    }
}
