package com.yonn2222.clientsortpaper;

import com.yonn2222.clientsortpaper.operation.Cell;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

final class FakeCell implements Cell {
    private final Object container;
    private final int index;
    private final boolean refuseShulkers;
    private ItemStack stack;

    FakeCell(Object container, int index) {
        this(container, index, false);
    }

    FakeCell(Object container, int index, boolean refuseShulkers) {
        this.container = container;
        this.index = index;
        this.refuseShulkers = refuseShulkers;
    }

    FakeCell with(Material type, int amount) {
        this.stack = new ItemStack(type, amount);
        return this;
    }

    static FakeCell[] row(Object container, int size) {
        FakeCell[] cells = new FakeCell[size];
        for (int i = 0; i < size; i++) {
            cells[i] = new FakeCell(container, i);
        }
        return cells;
    }

    int amount() {
        return stack == null ? 0 : stack.getAmount();
    }

    Material type() {
        return stack == null ? Material.AIR : stack.getType();
    }

    @Override
    public ItemStack get() {
        return stack == null ? null : stack.clone();
    }

    @Override
    public void set(ItemStack stack) {
        this.stack = stack == null || stack.getAmount() <= 0 ? null : stack.clone();
    }

    @Override
    public int limit(ItemStack stack) {
        return stack.getMaxStackSize();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !(refuseShulkers && stack.getType() == Material.SHULKER_BOX);
    }

    @Override
    public Object container() {
        return container;
    }

    @Override
    public int index() {
        return index;
    }
}
