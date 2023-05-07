package com.mohistmc.academy.listener;

import com.mohistmc.academy.AcademyCraft;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * 服务器事件监听器
 *
 * @author lliiooll
 */
@Mod.EventBusSubscriber(modid = AcademyCraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class ServerListener {

    private static ServerListener INSTANCE = null;
    private static final Logger LOGGER = LogUtils.getLogger();

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static ServerListener getInstance() {
        if (INSTANCE == null) INSTANCE = new ServerListener();
        return INSTANCE;
    }
}
