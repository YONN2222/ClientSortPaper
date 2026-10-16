package com.yonn2222.clientsortpaper.bedrock;

import com.yonn2222.clientsortpaper.ClientSortPaper;
import com.yonn2222.clientsortpaper.Messages;
import com.yonn2222.clientsortpaper.command.ManualSort;
import com.yonn2222.clientsortpaper.operation.Order;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;

public final class FloodgateMenu implements BedrockMenu {
    private final ClientSortPaper plugin;

    public FloodgateMenu(ClientSortPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isBedrock(Player player) {
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }

    @Override
    public void open(Player player) {
        Messages messages = plugin.messages();
        SimpleForm.Builder form = SimpleForm.builder()
                .title(messages.get("form-title"))
                .content(messages.get("form-content"))
                .button(messages.get("form-inventory-name"))
                .button(messages.get("form-inventory-quantity"))
                .button(messages.get("form-container-name"))
                .button(messages.get("form-container-quantity"))
                .validResultHandler(response -> {
                    int choice = response.clickedButtonId();
                    Scheduling.run(plugin, player, () -> ManualSort.perform(plugin, player, choice >= 2,
                            choice % 2 == 0 ? Order.NAME : Order.QUANTITY));
                });
        FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
    }
}
