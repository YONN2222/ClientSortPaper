package com.yonn2222.clientsortpaper.operation;

import com.yonn2222.clientsortpaper.network.PayloadException;
import org.bukkit.inventory.Inventory;

public final class PlayerInventorySource implements SlotSource {
    private static final int MENU_SIZE = 46;

    private final Inventory inventory;

    public PlayerInventorySource(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Cell cell(int slotId) throws PayloadException {
        if (slotId < 0 || slotId >= MENU_SIZE) {
            throw PayloadException.invalid("Slot " + slotId + " is outside the inventory menu");
        }
        if (slotId >= 9 && slotId <= 35) {
            return new BukkitCell(inventory, slotId);
        }
        if (slotId >= 36 && slotId <= 44) {
            return new BukkitCell(inventory, slotId - 36);
        }
        throw PayloadException.unsupported("Slot " + slotId + " is not a storage slot");
    }
}
