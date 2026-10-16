package com.yonn2222.clientsortpaper.bedrock;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class Scheduling {
    private static final boolean FOLIA = exists("io.papermc.paper.threadedregions.RegionizedServer");

    private Scheduling() {
    }

    private static boolean exists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException missing) {
            return false;
        }
    }

    public static void run(Plugin plugin, Player player, Runnable task) {
        if (FOLIA) {
            player.getScheduler().run(plugin, scheduled -> task.run(), null);
        } else if (Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }
}
