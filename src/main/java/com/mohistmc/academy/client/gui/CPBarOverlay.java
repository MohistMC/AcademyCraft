package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class CPBarOverlay {

    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY = 0xFF999999;

    // 材质图集原始尺寸（用于计算宽高比）
    private static final int TEX_WIDTH = 964;
    private static final int TEX_HEIGHT = 147;

    private static final ResourceLocation TEX_CP_BG = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/back_normal.png");
    private static final ResourceLocation TEX_CP_FG = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/cp.png");
    private static final ResourceLocation TEX_OL_BG = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/back_overload.png");
    private static final ResourceLocation TEX_OL_FG = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/front_overload.png");
    private static final ResourceLocation MASK = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "textures/guis/cpbar/mask.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.HOTBAR)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null || mc.options.hideGui) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;
        boolean active = data.isAbilityActive();
        if (!active) return;

        GuiGraphics g = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();

        int HUD_WIDTH = screenW/2 - 40;
        int hudX = screenW - HUD_WIDTH - 30;

        // 基于纹理原始尺寸计算等比例缩放
        float scale = (float) HUD_WIDTH / TEX_WIDTH;

        // 绘制背景（全图渲染，通过 PoseStack 缩放到目标大小）
        g.pose().pushPose();
        g.pose().translate(hudX, 15, 0);
        g.pose().scale(scale, scale, 1.0f);
        g.blit(TEX_CP_BG, 0, 0, 0, 0, TEX_WIDTH, TEX_HEIGHT, TEX_WIDTH, TEX_HEIGHT);
        g.pose().popPose();

        // 绘制前景 CP 条（按比例宽度，右对齐填充：从右向左缩短）
        float cpRatio = data.getMaxCp() > 0 ? Math.min(1.0f, data.getCurrentCp() / data.getMaxCp()) : 0;
        int cpTexWidth = (int) (TEX_WIDTH * cpRatio);
        if (cpTexWidth > 0) {
            int cpTexStart = TEX_WIDTH - cpTexWidth;
            g.pose().pushPose();
            g.pose().translate(hudX + 1, 15 + 1, 0);
            g.pose().scale(scale, scale, 1.0f);
            g.blit(TEX_CP_FG, cpTexStart, 0, cpTexStart, 0, cpTexWidth, TEX_HEIGHT, TEX_WIDTH, TEX_HEIGHT);
            g.pose().popPose();
        }
    }
}
