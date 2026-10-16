package com.yonn2222.clientsortpaper.operation;

import org.bukkit.inventory.ItemStack;

public interface Cell {
    ItemStack get();

    void set(ItemStack stack);

    int limit(ItemStack stack);

    boolean mayPlace(ItemStack stack);

    Object container();

    int index();

    default boolean sameSlot(Cell other) {
        return container() == other.container() && index() == other.index();
    }
}
