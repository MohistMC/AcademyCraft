package com.mohistmc.academy.client;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = AcademyCraft.MODID, value = Dist.CLIENT)
public class ChargingHudOverlay {

    private static int currentTicks = 0;
    private static int maxTicks = 0;
    private static String currentSkillId = "";
    private static boolean charging = false;
    private static long lastUpdateTime = 0;

    public static void setChargingState(int ticks, int max, String skillId) {
        currentTicks = ticks;
        maxTicks = max;
        currentSkillId = skillId;
        charging = ticks >= 0;
        lastUpdateTime = System.currentTimeMillis();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!charging) return;

        // 超时保护：超过 5 秒没收到更新，自动清除（防止包丢失导致残留）
        if (System.currentTimeMillis() - lastUpdateTime > 5000) {
            charging = false;
            return;
        }

        if (Minecraft.getInstance().screen != null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int barWidth = 120;
        int barHeight = 10;
        int x = (screenWidth - barWidth) / 2;
        int y = screenHeight - 60;

        event.getGuiGraphics().fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

        float progress = maxTicks > 0 ? Math.min((float) currentTicks / maxTicks, 1.0f) : 0;
        int progressWidth = (int) (barWidth * progress);
        int color = progress >= 1.0f ? 0xFF00FF00 : 0xFFFFFF00;
        event.getGuiGraphics().fill(x, y, x + progressWidth, y + barHeight, color);

        event.getGuiGraphics().renderOutline(x, y, barWidth, barHeight, 0xFFFFFFFF);

        if (!currentSkillId.isEmpty()) {
            Skill skill = SkillRegistry.getSkill(currentSkillId);
            if (skill != null) {
                Component name = Component.translatable(skill.getTranslationKey());
                int textWidth = mc.font.width(name);
                event.getGuiGraphics().drawString(mc.font, name, (screenWidth - textWidth) / 2, y - 12, 0xFFFFFFFF);
            }
        }
    }
}
