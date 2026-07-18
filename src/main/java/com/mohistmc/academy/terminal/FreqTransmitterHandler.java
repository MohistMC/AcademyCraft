package com.mohistmc.academy.terminal;

import com.mohistmc.academy.energy.api.block.IWirelessGenerator;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.energy.api.block.IWirelessReceiver;
import com.mohistmc.academy.energy.impl.WirelessSystem;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * 频率变送器 —— 右键节点/机器的连接处理。
 * 玩家安装频率变送器 App 后，手持数据终端右键节点→右键机器完成连接。
 *
 * @author Mgazul
 */
public class FreqTransmitterHandler {

    /** 玩家当前选中的节点位置（客户端/服务端同步） */
    private static final Map<UUID, BlockPos> selectedNode = new HashMap<>();

    /**
     * 获取玩家选中的节点位置。
     */
    public static BlockPos getSelectedNode(Player player) {
        return selectedNode.get(player.getUUID());
    }

    /**
     * 清除玩家的选择状态。
     */
    public static void clearSelection(Player player) {
        selectedNode.remove(player.getUUID());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = event.getLevel();

        // 检查是否安装了频率变送器
        if (level.isClientSide()) return;
        PlayerAbilityData data = player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.isTerminalInstalled() || !data.hasApp(AppRegistry.FREQ_TRANSMITTER)) {
            return;
        }

        // 检查玩家是否拿着数据终端 (任意手)
        boolean holdingTerminal = player.getItemInHand(InteractionHand.MAIN_HAND).is(
                net.minecraft.world.item.Items.AIR
        ) == false; // 简化检查：有终端安装即可
        // 实际应该检查持有终端物品，但终端安装器使用后消失，终端本身没有物品形态
        // 这里简化为安装了终端即可使用

        BlockPos clickedPos = event.getPos();
        BlockEntity be = level.getBlockEntity(clickedPos);

        if (be instanceof IWirelessNode iNode) {
            // 第一次右键：选中节点
            selectedNode.put(player.getUUID(), clickedPos);
            player.sendSystemMessage(Component.literal("§b[频率变送器] §a已选择节点: " + iNode.getNodeName()));
            player.sendSystemMessage(Component.literal("§7接下来请右键要连接的机器"));
            event.setCanceled(true);
            return;
        }

        // 检查是否有选中的节点
        BlockPos nodePos = selectedNode.get(player.getUUID());
        if (nodePos == null) return;

        // 第二次右键：连接机器到节点
        BlockEntity nodeBe = level.getBlockEntity(nodePos);
        if (!(nodeBe instanceof IWirelessNode iNode)) {
            selectedNode.remove(player.getUUID());
            player.sendSystemMessage(Component.literal("§c[频率变送器] §e选中的节点已失效，请重新选择"));
            return;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            ServerLevel serverLevel = serverPlayer.serverLevel();

            if (be instanceof IWirelessGenerator gen) {
                boolean ok = WirelessSystem.linkGenerator(serverLevel, iNode, gen, false, "");
                if (ok) {
                    player.sendSystemMessage(Component.literal("§b[频率变送器] §a发电机已连接到节点 " + iNode.getNodeName()));
                } else {
                    player.sendSystemMessage(Component.literal("§b[频率变送器] §c连接失败（距离过远或容量已满）"));
                }
            } else if (be instanceof IWirelessReceiver rec) {
                boolean ok = WirelessSystem.linkReceiver(serverLevel, iNode, rec, false, "");
                if (ok) {
                    player.sendSystemMessage(Component.literal("§b[频率变送器] §a机器已连接到节点 " + iNode.getNodeName()));
                } else {
                    player.sendSystemMessage(Component.literal("§b[频率变送器] §c连接失败（距离过远或容量已满）"));
                }
            } else {
                player.sendSystemMessage(Component.literal("§b[频率变送器] §c该方块不支持无线连接"));
                return;
            }
        }

        selectedNode.remove(player.getUUID());
        event.setCanceled(true);
    }
}
