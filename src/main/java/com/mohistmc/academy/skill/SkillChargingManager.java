package com.mohistmc.academy.skill;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkillChargingManager {

    public static class ChargingState {
        public final int slotIndex;
        public int ticks;
        public boolean releasing = false; // 防止 onChargingRelease 被重复调用

        public ChargingState(int slotIndex) {
            this.slotIndex = slotIndex;
            this.ticks = 0;
        }
    }

    private static final Map<UUID, ChargingState> STATES = new HashMap<>();

    public static void startCharging(UUID playerId, int slotIndex) {
        STATES.put(playerId, new ChargingState(slotIndex));
    }

    public static ChargingState getState(UUID playerId) {
        return STATES.get(playerId);
    }

    public static void stopCharging(UUID playerId) {
        STATES.remove(playerId);
    }

    public static boolean isCharging(UUID playerId) {
        return STATES.containsKey(playerId);
    }
}
