package com.yonn2222.clientsortpaper.operation;

import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.Locale;

public enum Order {
    NAME(Comparator.<ItemStack, String>comparing(stack -> stack.getType().name())
            .thenComparing(Comparator.comparingInt(ItemStack::getAmount).reversed())),
    QUANTITY(Comparator.<ItemStack>comparingInt(ItemStack::getAmount).reversed()
            .thenComparing(stack -> stack.getType().name()));

    private final Comparator<ItemStack> comparator;

    Order(Comparator<ItemStack> comparator) {
        this.comparator = comparator;
    }

    public Comparator<ItemStack> comparator() {
        return comparator;
    }

    public static Order parse(String input) {
        for (Order order : values()) {
            if (order.name().toLowerCase(Locale.ROOT).equals(input.toLowerCase(Locale.ROOT))) {
                return order;
            }
        }
        return null;
    }
}
