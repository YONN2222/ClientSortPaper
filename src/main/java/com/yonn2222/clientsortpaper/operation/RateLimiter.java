package com.yonn2222.clientsortpaper.operation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public final class RateLimiter {
    private static final long WINDOW_NANOS = 1_000_000_000L;

    private final Map<UUID, Window> windows = new ConcurrentHashMap<>();
    private final LongSupplier clock;

    public RateLimiter() {
        this(System::nanoTime);
    }

    public RateLimiter(LongSupplier clock) {
        this.clock = clock;
    }

    public boolean allow(UUID player, int limit) {
        if (limit <= 0) {
            return true;
        }
        long now = clock.getAsLong();
        Window window = windows.computeIfAbsent(player, id -> new Window(now));
        synchronized (window) {
            if (now - window.start >= WINDOW_NANOS) {
                window.start = now;
                window.count = 0;
            }
            window.count++;
            return window.count <= limit;
        }
    }

    public void forget(UUID player) {
        windows.remove(player);
    }

    private static final class Window {
        private long start;
        private int count;

        private Window(long start) {
            this.start = start;
        }
    }
}
