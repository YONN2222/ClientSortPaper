package com.yonn2222.clientsortpaper.network;

import com.yonn2222.clientsortpaper.operation.Operation;
import com.yonn2222.clientsortpaper.operation.OperationService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class PayloadListener implements PluginMessageListener {
    private static final String UNEXPECTED = "Unexpected exception, check server logs for more info.";

    private final Plugin plugin;
    private final Logger logger;
    private final OperationService service;

    public PayloadListener(Plugin plugin, OperationService service) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.service = service;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        Operation operation = Operation.byRequestChannel(channel);
        if (operation == null) {
            return;
        }
        PayloadResult result = PayloadResult.SUCCESS;
        String text = "";
        String requestId = "";
        try {
            Request request = Request.decode(operation, message);
            if (request instanceof Request.Collect collect) {
                requestId = collect.id();
            }
            service.execute(player, request);
        } catch (PayloadException refused) {
            result = refused.result();
            text = refused.getMessage();
        } catch (RuntimeException failure) {
            result = PayloadResult.FAILURE;
            text = UNEXPECTED;
            logger.log(Level.SEVERE, "Unexpected exception while handling " + channel + " from " + player.getName(), failure);
        }
        reply(player, operation, result, text, requestId);
    }

    private void reply(Player player, Operation operation, PayloadResult result, String text, String requestId) {
        if (!player.getListeningPluginChannels().contains(operation.resultChannel())) {
            return;
        }
        player.sendPluginMessage(plugin, operation.resultChannel(), Request.encodeResult(operation, result, text, requestId));
    }
}
