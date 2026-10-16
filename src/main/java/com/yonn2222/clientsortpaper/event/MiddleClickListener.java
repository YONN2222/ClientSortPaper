package com.yonn2222.clientsortpaper.event;

import com.yonn2222.clientsortpaper.ClientSortPaper;
import com.yonn2222.clientsortpaper.command.ClientSortCommand;
import com.yonn2222.clientsortpaper.config.Settings;
import com.yonn2222.clientsortpaper.network.PayloadException;
import com.yonn2222.clientsortpaper.Messages;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class MiddleClickListener implements Listener {
    private final ClientSortPaper plugin;

    public MiddleClickListener(ClientSortPaper plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (event.getClick() != ClickType.MIDDLE || !(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Settings settings = plugin.service().settings();
        if (!settings.middleClick() || ClientSortCommand.hasMod(player)
                || player.getGameMode() == GameMode.CREATIVE || !player.hasPermission("clientsortpaper.use")) {
            return;
        }
        Inventory clicked = event.getClickedInventory();
        if (clicked == null) {
            return;
        }
        boolean container = clicked == event.getView().getTopInventory();
        if (container ? !plugin.service().hasOpenContainer(player) : clicked.getType() != InventoryType.PLAYER) {
            return;
        }
        try {
            plugin.service().sortManually(player, container, settings.middleClickOrder());
        } catch (PayloadException refused) {
            player.sendMessage(plugin.messages().get("sort-failed", Messages.text("reason", refused.getMessage())));
        }
    }
}
