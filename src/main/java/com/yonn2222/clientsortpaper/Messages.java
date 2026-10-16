package com.yonn2222.clientsortpaper;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class Messages {
    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().character('§').hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    public static final List<String> BUNDLED = List.of("en", "zh", "es", "hi", "fr", "ar", "pt", "ru", "de", "ja");

    private final FileConfiguration active;
    private final FileConfiguration fallback;

    public Messages(JavaPlugin plugin, String language) {
        for (String code : BUNDLED) {
            File target = new File(plugin.getDataFolder(), "lang/" + code + ".yml");
            if (!target.exists()) {
                plugin.saveResource("lang/" + code + ".yml", false);
            }
        }
        this.fallback = bundled(plugin, "en");
        File chosen = new File(plugin.getDataFolder(), "lang/" + language + ".yml");
        if (chosen.exists()) {
            this.active = YamlConfiguration.loadConfiguration(chosen);
        } else {
            plugin.getLogger().warning("Language file lang/" + language + ".yml not found, using English");
            this.active = fallback;
        }
    }

    private static FileConfiguration bundled(JavaPlugin plugin, String code) {
        InputStream stream = plugin.getResource("lang/" + code + ".yml");
        if (stream == null) {
            return new YamlConfiguration();
        }
        return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    public String raw(String key) {
        String value = active.getString(key);
        if (value == null) {
            value = fallback.getString(key, key);
        }
        return value;
    }

    public String get(String key, TagResolver... resolvers) {
        List<TagResolver> all = new ArrayList<>(List.of(resolvers));
        all.add(Placeholder.parsed("prefix", raw("prefix")));
        return LEGACY.serialize(MINI.deserialize(raw(key), TagResolver.resolver(all)));
    }

    public static TagResolver text(String name, String value) {
        return Placeholder.unparsed(name, value);
    }
}
