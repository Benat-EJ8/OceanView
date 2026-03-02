package com.oceanview.resort.patterns.room;

import com.oceanview.resort.domain.RoomCategory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


  // Flyweight: Shared room category data to avoid repeated DB reads.

public class RoomCategoryFlyweight {
    private static final Map<Integer, RoomCategory> CACHE = new ConcurrentHashMap<>();

    public static RoomCategory get(Integer id, java.util.function.Supplier<RoomCategory> loader) {
        return CACHE.computeIfAbsent(id, k -> loader.get());
    }

    public static void put(Integer id, RoomCategory category) {
        if (id != null && category != null) CACHE.put(id, category);
    }

    public static void invalidate(Integer id) {
        CACHE.remove(id);
    }

    public static void clear() {
        CACHE.clear();
    }
}
