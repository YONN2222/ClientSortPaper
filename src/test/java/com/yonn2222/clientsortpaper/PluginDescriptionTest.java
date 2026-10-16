package com.yonn2222.clientsortpaper;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginDescriptionTest {
    @Test
    void pluginYmlIsAcceptedByBukkit() throws InvalidDescriptionException {
        var stream = getClass().getResourceAsStream("/plugin.yml");
        assertNotNull(stream);
        PluginDescriptionFile description = new PluginDescriptionFile(new InputStreamReader(stream, StandardCharsets.UTF_8));
        assertEquals("ClientSortPaper", description.getName());
        assertEquals("com.yonn2222.clientsortpaper.ClientSortPaper", description.getMain());
        assertTrue(description.getCommands().containsKey("clientsort"));
        assertTrue(description.getCommands().containsKey("sort"));
        assertTrue(description.getPermissions().stream().anyMatch(p -> p.getName().equals("clientsortpaper.use")));
    }

    @Test
    void bundledConfigIsValidYaml() {
        var stream = getClass().getResourceAsStream("/config.yml");
        assertNotNull(stream);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        assertTrue(config.getBoolean("operations.sort"));
        assertTrue(config.isList("inventories.allowed-types"));
    }
}
