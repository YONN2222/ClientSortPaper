package com.yonn2222.clientsortpaper.operation;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

final class Items {
    private Items() {
    }

    static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.getType().isAir() || stack.getAmount() <= 0;
    }

    static boolean isShulkerBox(ItemStack stack) {
        return stack != null && Tag.SHULKER_BOXES.isTagged(stack.getType());
    }

    static ItemStack withAmount(ItemStack stack, int amount) {
        ItemStack copy = stack.clone();
        copy.setAmount(amount);
        return copy;
    }

    static ItemStack copy(ItemStack stack) {
        return isEmpty(stack) ? null : stack.clone();
    }

    static boolean same(ItemStack a, ItemStack b) {
        return a.isSimilar(b);
    }

    static boolean isAir(Material material) {
        return material.isAir();
    }
}
