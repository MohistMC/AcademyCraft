package com.mohistmc.academy.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.server.ServerMain;

public class GameTimer {
    static long storedTime, timeLag;

    static long beginTimeClient, beginTimeServer;

    private static double getTime(boolean isClient) {
        if (isClient) {
            if (beginTimeClient == 0) beginTimeClient = getRawTimeClient();
            long elapsed = getRawTimeClient() - beginTimeClient;
            return elapsed / 1000.0;
        } else {
            if (beginTimeServer == 0) beginTimeServer = getRawTimeServer();
            long elapsed = getRawTimeServer() - beginTimeServer;
            return elapsed / 1000.0;
        }
    }

    private static long getRawTimeClient() {
        long time = Minecraft.getInstance().getFrameTimeNs();
        if (Minecraft.getInstance().isPaused()) {
            timeLag = time - storedTime;
        } else {
            storedTime = time - timeLag;
        }
        return time - timeLag;
    }

    private static long getRawTimeServer() {
        return System.currentTimeMillis();
    }


    public static double getTime() {
        if (beginTimeClient == 0L)
            beginTimeClient = System.currentTimeMillis();
        return (System.currentTimeMillis() - beginTimeClient) / 1000.0;
    }

}
