package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.energy.api.block.IWirelessNode;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * IF 能源 HUD — 显示附近无线节点和网络连接状态。
 *
 * @author Mgazul
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class EnergyHudOverlay {

    private static final ResourceLocation TEX_WIFI =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/button/button_wifi.png");
    private static final ResourceLocation TEX_NODE =
            ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/button/button_node.png");

    private static final int ICON_SIZE = 12;
    private static final int SCAN_RANGE = 25;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.options.hideGui) return;
        // Only show if player has an ability (meaning they might interact with energy network)
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        GuiGraphics g = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int x = screenW - 100;
        int y = 40;

        // Scan for nearby nodes
        if (mc.level != null && !mc.level.isClientSide) return; // Server-side, skip
        // Client-side: check nearby block entities
        BlockPos playerPos = mc.player.blockPosition();
        boolean foundNode = false;

        for (int dx = -SCAN_RANGE; dx <= SCAN_RANGE; dx += 4) {
            for (int dy = -SCAN_RANGE; dy <= SCAN_RANGE; dy += 4) {
                for (int dz = -SCAN_RANGE; dz <= SCAN_RANGE; dz += 4) {
                    BlockPos bp = playerPos.offset(dx, dy, dz);
                    if (!mc.level.isLoaded(bp)) continue;
                    BlockEntity be = mc.level.getBlockEntity(bp);
                    if (be instanceof IWirelessNode node && node.getEnergy() > 0) {
                        foundNode = true;

                        // Node icon
                        int nodeX = x + dx * 2;
                        int nodeY = y + dz * 2;

                        float energyRatio = (float)(node.getEnergy() / node.getMaxEnergy());
                        int color = energyRatio > 0.7f ? 0xFF00E5FF
                                : energyRatio > 0.3f ? 0xFF0088CC : 0xFF444466;

                        g.fill(nodeX - 2, nodeY - 2, nodeX + 2, nodeY + 2, color);

                        break;
                    }
                }
                if (foundNode) break;
            }
            if (foundNode) break;
        }

        // Status icon
        if (foundNode) {
            g.blit(TEX_WIFI, x - 20, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        }
    }
}
