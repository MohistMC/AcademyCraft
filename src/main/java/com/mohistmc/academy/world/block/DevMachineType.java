package com.mohistmc.academy.world.block;

public enum DevMachineType {
    PORTABLE(2, 30, "便携"),
    NORMAL(3, 60, "基础"),
    ADVANCED(Integer.MAX_VALUE, 100, "高级");

    public final int maxLevel;
    public final int syncRate;
    public final String displayName;

    DevMachineType(int maxLevel, int syncRate, String displayName) {
        this.maxLevel = maxLevel;
        this.syncRate = syncRate;
        this.displayName = displayName;
    }

    public int applySyncRate(int baseCost) {
        return baseCost * 100 / syncRate;
    }

    public static DevMachineType fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return NORMAL;
        }
        return values()[ordinal];
    }
}