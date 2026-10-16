package com.yonn2222.clientsortpaper.operation;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.OptionalInt;
import java.util.logging.Logger;

final class MenuIds {
    private static volatile boolean unavailable;
    private static volatile Method getHandle;
    private static volatile Field menuField;
    private static volatile Field idField;

    private MenuIds() {
    }

    static OptionalInt current(Player player, Logger logger) {
        if (unavailable) {
            return OptionalInt.empty();
        }
        try {
            Method method = getHandle;
            if (method == null) {
                method = player.getClass().getMethod("getHandle");
                getHandle = method;
            }
            Object handle = method.invoke(player);
            Field menu = menuField;
            if (menu == null) {
                menu = find(handle.getClass(), "containerMenu");
                menuField = menu;
            }
            Object container = menu.get(handle);
            Field id = idField;
            if (id == null) {
                id = find(container.getClass(), "containerId");
                idField = id;
            }
            return OptionalInt.of(id.getInt(container));
        } catch (ReflectiveOperationException | RuntimeException failure) {
            unavailable = true;
            logger.info("The open container id cannot be read on this server, the check is skipped (" + failure.getClass().getSimpleName() + ")");
            return OptionalInt.empty();
        }
    }

    private static Field find(Class<?> type, String name) throws NoSuchFieldException {
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new NoSuchFieldException(name);
    }
}
