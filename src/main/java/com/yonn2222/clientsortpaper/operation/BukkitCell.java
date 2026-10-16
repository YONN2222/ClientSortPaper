package com.yonn2222.clientsortpaper.operation;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class BukkitCell implements Cell {
    private final Inventory inventory;
    private final int index;

    public BukkitCell(Inventory inventory, int index) {
        this.inventory = inventory;
        this.index = index;
    }

    @Override
    public ItemStack get() {
        return Items.copy(inventory.getItem(index));
    }

    @Override
    public void set(ItemStack stack) {
        inventory.setItem(index, Items.copy(stack));
    }

    @Override
    public int limit(ItemStack stack) {
        return Math.min(inventory.getMaxStackSize(), stack.getMaxStackSize());
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !(inventory.getType() == InventoryType.SHULKER_BOX && Items.isShulkerBox(stack));
    }

    @Override
    public Object container() {
        return inventory;
    }

    @Override
    public int index() {
        return index;
    }

    public Inventory inventory() {
        return inventory;
    }
}
