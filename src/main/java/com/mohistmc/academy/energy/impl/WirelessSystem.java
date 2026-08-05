package com.mohistmc.academy.energy.impl;

import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.impl.VBlocks.VNGenerator;
import com.mohistmc.academy.energy.impl.VBlocks.VNReceiver;
import com.mohistmc.academy.energy.impl.VBlocks.VWNode;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * 无线能源系统入口 —— 监听服务端 tick，驱动所有网络和节点连接的更新。
 */
@EventBusSubscriber
public class WirelessSystem {

    private WirelessSystem() {}

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            WiWorldData data = WiWorldData.getNonCreate(level);
            if (data != null) {
                data.level = level;
                data.tick();
            }
        }
    }

    // ==================== 便捷方法 ====================

    /** 创建无线网络 */
    public static boolean createNetwork(ServerLevel level,
                                         com.mohistmc.academy.energy.api.block.IWirelessMatrix matrix,
                                         String ssid, String password) {
        WiWorldData data = WiWorldData.get(level);
        return data.createNetwork(matrix, ssid, password);
    }

    /** 节点加入网络 */
    public static boolean linkNode(ServerLevel level,
                                    com.mohistmc.academy.energy.api.block.IWirelessMatrix matrix,
                                    com.mohistmc.academy.energy.api.block.IWirelessNode node,
                                    String password) {
        WiWorldData data = WiWorldData.get(level);
        WirelessNet net = data.getNetwork(matrix);
        if (net == null) return false;
        return net.addNode(new VWNode(node), password);
    }

    /** 发电机加入节点 */
    public static boolean linkGenerator(ServerLevel level,
                                         com.mohistmc.academy.energy.api.block.IWirelessNode node,
                                         IWirelessGenerator gen,
                                         boolean needAuth, String password) {
        WiWorldData data = WiWorldData.get(level);
        if (needAuth) {
            if (!node.getPassword().equals(password)) return false;
        }
        NodeConn conn = data.getNodeConnection(node);
        return conn.addGenerator(new VNGenerator(gen));
    }

    /** 接收器加入节点 */
    public static boolean linkReceiver(ServerLevel level,
                                        com.mohistmc.academy.energy.api.block.IWirelessNode node,
                                        IWirelessReceiver rec,
                                        boolean needAuth, String password) {
        WiWorldData data = WiWorldData.get(level);
        if (needAuth) {
            if (!node.getPassword().equals(password)) return false;
        }
        NodeConn conn = data.getNodeConnection(node);
        return conn.addReceiver(new VNReceiver(rec));
    }

    /** 获取节点连接信息 */
    public static NodeConn getNodeConnection(ServerLevel level,
                                               com.mohistmc.academy.energy.api.block.IWirelessNode node) {
        WiWorldData data = WiWorldData.getNonCreate(level);
        if (data == null) return null;
        return data.getNodeConnection(node);
    }

    /** 获取节点所属网络 */
    public static WirelessNet getNetwork(ServerLevel level,
                                           com.mohistmc.academy.energy.api.block.IWirelessNode node) {
        WiWorldData data = WiWorldData.getNonCreate(level);
        if (data == null) return null;
        return data.getNetwork(node);
    }
}
