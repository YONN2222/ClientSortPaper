package com.yonn2222.clientsortpaper.command;

import com.yonn2222.clientsortpaper.ClientSortPaper;
import com.yonn2222.clientsortpaper.Messages;
import com.yonn2222.clientsortpaper.network.PayloadException;
import com.yonn2222.clientsortpaper.operation.Order;
import org.bukkit.entity.Player;

public final class ManualSort {
    private ManualSort() {
    }

    public static void perform(ClientSortPaper plugin, Player player, boolean container, Order order) {
        Messages messages = plugin.messages();
        if (container && !plugin.service().hasSortableContainer(player)) {
            player.sendMessage(messages.get("sort-no-container"));
            return;
        }
        try {
            boolean sorted = plugin.service().sortManually(player, container, order);
            if (!sorted) {
                player.sendMessage(messages.get("sort-nothing"));
            } else {
                player.sendMessage(messages.get(container ? "sort-done-container" : "sort-done-inventory"));
            }
        } catch (PayloadException refused) {
            player.sendMessage(messages.get("sort-failed", Messages.text("reason", refused.getMessage())));
        }
    }
}
