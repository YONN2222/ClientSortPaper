package com.yonn2222.clientsortpaper;

import com.yonn2222.clientsortpaper.bedrock.BedrockMenu;
import com.yonn2222.clientsortpaper.bedrock.FloodgateMenu;
import com.yonn2222.clientsortpaper.command.ClientSortCommand;
import com.yonn2222.clientsortpaper.command.SortCommand;
import com.yonn2222.clientsortpaper.config.Settings;
import com.yonn2222.clientsortpaper.event.MiddleClickListener;
import com.yonn2222.clientsortpaper.metrics.Stats;
import com.yonn2222.clientsortpaper.network.PayloadListener;
import com.yonn2222.clientsortpaper.operation.Operation;
import com.yonn2222.clientsortpaper.operation.OperationService;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;

public final class ClientSortPaper extends JavaPlugin implements Listener {
    private Messages messages;
    private OperationService service;
    private BedrockMenu bedrock;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Settings settings = Settings.read(getConfig(), getLogger());
        messages = new Messages(this, settings.language());
        service = new OperationService(getLogger(), settings);

        registerChannels();
        bind("clientsort", new ClientSortCommand(this));
        bind("sort", new SortCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new MiddleClickListener(this), this);
        if (settings.bedrockMenu() && getServer().getPluginManager().getPlugin("floodgate") != null) {
            bedrock = new FloodgateMenu(this);
            getLogger().info("Floodgate found, Bedrock players get a sorting menu with /sort");
        }
        Stats.start(this, () -> service.settings());
        getLogger().info("Listening for the ClientSort mod on " + Operation.values().length + " channels");
    }

    @Override
    public void onDisable() {
        Messenger messenger = getServer().getMessenger();
        messenger.unregisterIncomingPluginChannel(this);
        messenger.unregisterOutgoingPluginChannel(this);
    }

    private void registerChannels() {
        Messenger messenger = getServer().getMessenger();
        PayloadListener listener = new PayloadListener(this, service);
        for (Operation operation : Operation.values()) {
            messenger.registerIncomingPluginChannel(this, operation.requestChannel(), listener);
            messenger.registerOutgoingPluginChannel(this, operation.resultChannel());
        }
    }

    private <T extends org.bukkit.command.CommandExecutor & org.bukkit.command.TabCompleter> void bind(String name, T handler) {
        PluginCommand command = getCommand(name);
        if (command == null) {
            getLogger().warning("The /" + name + " command is missing from plugin.yml");
            return;
        }
        command.setExecutor(handler);
        command.setTabCompleter(handler);
    }

    public void reloadAll() {
        reloadConfig();
        Settings settings = Settings.read(getConfig(), getLogger());
        messages = new Messages(this, settings.language());
        service.apply(settings);
    }

    public BedrockMenu bedrock() {
        return bedrock;
    }

    public Messages messages() {
        return messages;
    }

    public OperationService service() {
        return service;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        service.forget(event.getPlayer());
    }
}
