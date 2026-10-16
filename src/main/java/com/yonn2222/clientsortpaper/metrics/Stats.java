package com.yonn2222.clientsortpaper.metrics;

import com.yonn2222.clientsortpaper.command.ClientSortCommand;
import com.yonn2222.clientsortpaper.config.Settings;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Supplier;

public final class Stats {
    private Stats() {
    }

    public static void start(JavaPlugin plugin, Supplier<Settings> settings) {
        int id = pluginId(plugin);
        if (id <= 0) {
            return;
        }
        Metrics metrics = new Metrics(plugin, id);
        metrics.addCustomChart(new SimplePie("language", () -> settings.get().language()));
        metrics.addCustomChart(new SimplePie("sort_command", () -> settings.get().sortCommand() ? "enabled" : "disabled"));
        metrics.addCustomChart(new SimplePie("players_with_mod", () -> {
            int total = Bukkit.getOnlinePlayers().size();
            if (total == 0) {
                return "none online";
            }
            int withMod = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (ClientSortCommand.hasMod(player)) {
                    withMod++;
                }
            }
            return withMod == 0 ? "none" : withMod == total ? "all" : "some";
        }));
    }

    private static int pluginId(JavaPlugin plugin) {
        Properties properties = new Properties();
        try (InputStream in = plugin.getResource("metrics.properties")) {
            if (in == null) {
                return 0;
            }
            properties.load(in);
        } catch (IOException e) {
            return 0;
        }
        try {
            return Integer.parseInt(properties.getProperty("bstats-id", "0").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
