package com.yonn2222.clientsortpaper.config;

import com.yonn2222.clientsortpaper.operation.Operation;
import com.yonn2222.clientsortpaper.operation.Order;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

public record Settings(
        String language,
        Set<String> enabledOperations,
        Set<InventoryType> allowedInventories,
        boolean allowPluginMenus,
        Set<String> disabledWorlds,
        int maxPerSecond,
        boolean sortCommand,
        boolean middleClick,
        Order middleClickOrder,
        boolean bedrockMenu,
        boolean logOperations
) {
    public static final List<String> OPERATION_KEYS = List.of("sort", "stack-fill", "transfer");

    private static final List<InventoryType> DEFAULT_INVENTORIES = List.of(
            InventoryType.CHEST, InventoryType.ENDER_CHEST, InventoryType.SHULKER_BOX, InventoryType.BARREL,
            InventoryType.HOPPER, InventoryType.DISPENSER, InventoryType.DROPPER);

    public boolean isEnabled(Operation operation) {
        return enabledOperations.contains(operation.settingKey());
    }

    public static Settings read(FileConfiguration config, Logger logger) {
        String language = config.getString("language", "en").trim().toLowerCase(Locale.ROOT);

        Set<String> enabled = new HashSet<>();
        for (String key : OPERATION_KEYS) {
            if (config.getBoolean("operations." + key, true)) {
                enabled.add(key);
            }
        }

        Set<InventoryType> inventories = EnumSet.noneOf(InventoryType.class);
        if (config.isList("inventories.allowed-types")) {
            for (String name : config.getStringList("inventories.allowed-types")) {
                try {
                    inventories.add(InventoryType.valueOf(name.trim().toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException unknown) {
                    logger.warning("Unknown inventory type '" + name + "' in inventories.allowed-types, ignored");
                }
            }
        } else {
            inventories.addAll(DEFAULT_INVENTORIES);
        }

        Set<String> worlds = new HashSet<>(config.getStringList("disabled-worlds"));

        int perSecond = config.getInt("rate-limit.max-per-second", 20);
        if (perSecond < 0) {
            logger.warning("rate-limit.max-per-second must not be negative, using 20");
            perSecond = 20;
        }

        Order order = Order.parse(config.getString("middle-click.order", "name").trim());
        if (order == null) {
            logger.warning("middle-click.order must be name or quantity, using name");
            order = Order.NAME;
        }

        return new Settings(
                language,
                Set.copyOf(enabled),
                Set.copyOf(inventories),
                config.getBoolean("inventories.allow-plugin-menus", false),
                Set.copyOf(worlds),
                perSecond,
                config.getBoolean("sort-command.enabled", true),
                config.getBoolean("middle-click.enabled", true),
                order,
                config.getBoolean("bedrock-menu.enabled", true),
                config.getBoolean("log-operations", false)
        );
    }
}
