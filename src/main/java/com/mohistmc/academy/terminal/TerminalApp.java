package com.mohistmc.academy.terminal;

import net.minecraft.client.Minecraft;

/**
 * @author Mgazul
 * @date 2026/5/31 04:08
 */
public interface TerminalApp {

    String getAppId();

    default String getNameKey() {
        return "app." + getAppId();
    }

    default String getIcon() {
        return "◆";
    }

    default boolean isBuiltIn() {
        return false;
    }

    void open(Minecraft minecraft);
}
