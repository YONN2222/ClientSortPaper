package com.yonn2222.clientsortpaper;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LanguageFilesTest {
    private static final List<String> NAMES = List.of("label", "count", "total", "input", "reason", "prefix");
    private static final Pattern PLACEHOLDER = Pattern.compile("<(label|count|total|input|reason|prefix)>");

    private static YamlConfiguration load(String code) {
        var stream = LanguageFilesTest.class.getResourceAsStream("/lang/" + code + ".yml");
        assertNotNull(stream, code);
        return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private static Set<String> placeholders(String text) {
        Set<String> found = new HashSet<>();
        Matcher matcher = PLACEHOLDER.matcher(text);
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }

    @ParameterizedTest
    @ValueSource(strings = {"zh", "es", "hi", "fr", "ar", "pt", "ru", "de", "ja"})
    void everyLanguageHasTheSameKeysAndPlaceholdersAsEnglish(String code) {
        YamlConfiguration english = load("en");
        YamlConfiguration other = load(code);
        assertEquals(english.getKeys(false), other.getKeys(false));
        for (String key : english.getKeys(false)) {
            assertEquals(placeholders(english.getString(key)), placeholders(other.getString(key)), code + ":" + key);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"en", "zh", "es", "hi", "fr", "ar", "pt", "ru", "de", "ja"})
    void everyMessageParsesAsMiniMessage(String code) {
        YamlConfiguration file = load(code);
        TagResolver.Builder builder = TagResolver.builder();
        for (String name : NAMES) {
            builder.resolver(Placeholder.unparsed(name, "x"));
        }
        TagResolver resolver = builder.build();
        for (String key : file.getKeys(false)) {
            String text = file.getString(key);
            assertNotNull(text, code + ":" + key);
            MiniMessage.miniMessage().deserialize(text, resolver);
        }
    }

    @Test
    void everyMessageUsedByTheCodeExists() {
        YamlConfiguration english = load("en");
        for (String key : List.of("prefix", "usage", "reloaded", "no-permission", "player-only", "status-players",
                "status-you-yes", "status-you-no", "sort-disabled", "sort-unknown-argument", "sort-no-container",
                "sort-nothing", "sort-done-inventory", "sort-done-container", "sort-failed", "form-title", "form-content",
                "form-inventory-name", "form-inventory-quantity", "form-container-name", "form-container-quantity")) {
            assertTrue(english.isString(key), key);
        }
    }
}
