package com.oceanview.resort.patterns.room;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoomStatusTransition {
    private static final Map<String, Set<String>> ALLOWED = new HashMap<>();
    static {
        ALLOWED.put("AVAILABLE", Set.of("OCCUPIED", "MAINTENANCE", "CLEANING", "OUT_OF_ORDER"));
        ALLOWED.put("OCCUPIED", Set.of("AVAILABLE", "CLEANING"));
        ALLOWED.put("MAINTENANCE", Set.of("AVAILABLE", "OUT_OF_ORDER"));
        ALLOWED.put("CLEANING", Set.of("AVAILABLE", "OCCUPIED"));
        ALLOWED.put("OUT_OF_ORDER", Set.of("MAINTENANCE", "AVAILABLE"));
    }

    public static boolean canTransition(String from, String to) {
        if (from == null || to == null) return false;
        Set<String> allowed = ALLOWED.get(from);
        return allowed != null && allowed.contains(to);
    }
}
