package com.yonn2222.clientsortpaper.command;

import com.yonn2222.clientsortpaper.ClientSortPaper;
import com.yonn2222.clientsortpaper.Messages;
import com.yonn2222.clientsortpaper.operation.Order;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class SortCommand implements CommandExecutor, TabCompleter {
    private final ClientSortPaper plugin;

    public SortCommand(ClientSortPaper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages messages = plugin.messages();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.get("player-only"));
            return true;
        }
        if (!plugin.service().settings().sortCommand()) {
            sender.sendMessage(messages.get("sort-disabled"));
            return true;
        }
        if (!player.hasPermission("clientsortpaper.use")) {
            sender.sendMessage(messages.get("no-permission"));
            return true;
        }

        if (args.length == 0 && plugin.bedrock() != null && plugin.bedrock().isBedrock(player)) {
            plugin.bedrock().open(player);
            return true;
        }

        Boolean container = null;
        Order order = Order.NAME;
        for (String arg : args) {
            String lower = arg.toLowerCase(Locale.ROOT);
            if (lower.equals("inventory") || lower.equals("container")) {
                container = lower.equals("container");
            } else if (Order.parse(lower) != null) {
                order = Order.parse(lower);
            } else {
                sender.sendMessage(messages.get("sort-unknown-argument", Messages.text("input", arg)));
                return true;
            }
        }

        boolean sortContainer = container != null ? container : plugin.service().hasOpenContainer(player);
        ManualSort.perform(plugin, player, sortContainer, order);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length >= 1 && args.length <= 2) {
            options.addAll(List.of("inventory", "container", "name", "quantity"));
            options.removeIf(option -> !option.startsWith(args[args.length - 1].toLowerCase(Locale.ROOT)));
        }
        return options;
    }
}
