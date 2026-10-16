package com.yonn2222.clientsortpaper.operation;

import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Set;

public final class Access {
    private final Set<InventoryType> allowedTypes;
    private final boolean allowPluginMenus;

    public Access(Set<InventoryType> allowedTypes, boolean allowPluginMenus) {
        this.allowedTypes = Set.copyOf(allowedTypes);
        this.allowPluginMenus = allowPluginMenus;
    }

    public boolean allows(Inventory inventory) {
        InventoryType type = inventory.getType();
        if (!allowedTypes.contains(type)) {
            return false;
        }
        if (allowPluginMenus || type == InventoryType.ENDER_CHEST) {
            return true;
        }
        return isNative(inventory);
    }

    static boolean isNative(Inventory inventory) {
        if (inventory instanceof DoubleChestInventory) {
            return true;
        }
        InventoryHolder holder = inventory.getHolder();
        return holder instanceof BlockState || holder instanceof Entity
                || holder instanceof org.bukkit.block.DoubleChest;
    }
}
