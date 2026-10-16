package com.yonn2222.clientsortpaper.command;

import com.yonn2222.clientsortpaper.ClientSortPaper;
import com.yonn2222.clientsortpaper.Messages;
import com.yonn2222.clientsortpaper.operation.Operation;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ClientSortCommand implements CommandExecutor, TabCompleter {
    private final ClientSortPaper plugin;

    public ClientSortCommand(ClientSortPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages messages = plugin.messages();
        String sub = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "reload" -> {
                if (!sender.hasPermission("clientsortpaper.reload")) {
                    sender.sendMessage(messages.get("no-permission"));
                    return true;
                }
                plugin.reloadAll();
                sender.sendMessage(plugin.messages().get("reloaded"));
            }
            case "status" -> {
                if (!sender.hasPermission("clientsortpaper.status")) {
                    sender.sendMessage(messages.get("no-permission"));
                    return true;
                }
                status(sender, messages);
            }
            default -> sender.sendMessage(messages.get("usage", Messages.text("label", label)));
        }
        return true;
    }

    private void status(CommandSender sender, Messages messages) {
        int total = 0;
        int withMod = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            total++;
            if (hasMod(player)) {
                withMod++;
            }
        }
        sender.sendMessage(messages.get("status-players",
                Messages.text("count", String.valueOf(withMod)), Messages.text("total", String.valueOf(total))));
        if (sender instanceof Player player) {
            sender.sendMessage(messages.get(hasMod(player) ? "status-you-yes" : "status-you-no"));
        }
    }

    public static boolean hasMod(Player player) {
        return player.getListeningPluginChannels().contains(Operation.SORT.resultChannel());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("clientsortpaper.reload")) {
                options.add("reload");
            }
            if (sender.hasPermission("clientsortpaper.status")) {
                options.add("status");
            }
            options.removeIf(option -> !option.startsWith(args[0].toLowerCase(Locale.ROOT)));
        }
        return options;
    }
}
