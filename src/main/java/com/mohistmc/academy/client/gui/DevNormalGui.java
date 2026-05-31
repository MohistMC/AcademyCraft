package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.skill.AbilityCategory;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.skill.SkillType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * @author Mgazul
 * @date 2026/5/30 20:55
 */
@OnlyIn(Dist.CLIENT)
public class DevNormalGui extends Screen {

    private static final int MIN_GUI_WIDTH = 320;
    private static final int MIN_GUI_HEIGHT = 200;
    private static final int PADDING = 10;
    private static final int TOP_BAR_HEIGHT = 42;
    private static final int SKILL_HEIGHT = 18;
    private static final int SKILL_GAP = 4;
    private static final int LEVEL_HEADER_HEIGHT = 14;
    private static final int BAR_HEIGHT = 8;
    private static final int BACK_BTN_SIZE = 18;

    private static final int COLOR_BG = 0xCC101020;
    private static final int COLOR_TOP_BAR = 0xCC1a1a2e;
    private static final int COLOR_LEARNED = 0xFF2ecc71;
    private static final int COLOR_LEARNED_BORDER = 0xFF27ae60;
    private static final int COLOR_AVAILABLE = 0xFF3498db;
    private static final int COLOR_AVAILABLE_BORDER = 0xFF2980b9;
    private static final int COLOR_LOCKED = 0xFF555566;
    private static final int COLOR_LOCKED_BORDER = 0xFF444455;
    private static final int COLOR_PASSIVE_LEARNED = 0xFFf39c12;
    private static final int COLOR_PASSIVE_BORDER = 0xFFe67e22;
    private static final int COLOR_PASSIVE_AVAILABLE = 0xFF9b59b6;
    private static final int COLOR_TEXT_WHITE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_GRAY = 0xFF999999;
    private static final int COLOR_CP_BAR = 0xFF2ecc71;
    private static final int COLOR_CP_BG = 0xFF2c3e50;
    private static final int COLOR_OVERLOAD_BAR = 0xFFe74c3c;
    private static final int COLOR_OVERLOAD_BG = 0xFF2c3e50;
    private static final int COLOR_HOVER = 0x44FFFFFF;
    private static final int COLOR_LINE = 0xFF666688;
    private static final int COLOR_SCROLL_BAR = 0x88FFFFFF;
    private static final int COLOR_BACK_BG = 0xFF162040;
    private static final int COLOR_BACK_HOVER = 0xFF00bcd4;
    private static final int COLOR_BACK_BORDER = 0xFF00bcd4;

    private int guiLeft;
    private int guiTop;
    private int guiWidth;
    private int guiHeight;
    private int colWidth;
    private int skillWidth;
    private int treeAreaLeft;
    private int treeAreaTop;
    private int treeAreaWidth;
    private int treeAreaHeight;
    private int maxScroll = 0;
    private int scrollOffset = 0;
    private boolean isScrolling = false;
    private boolean fromTerminal = false;
    private boolean hoveredBack = false;

    private final List<SkillNode> skillNodes = new ArrayList<>();
    private SkillNode hoveredNode = null;

    public DevNormalGui() {
        super(Component.translatable("block.academy.dev_normal"));
        this.fromTerminal = false;
    }

    public DevNormalGui(boolean fromTerminal) {
        super(Component.translatable("block.academy.dev_normal"));
        this.fromTerminal = fromTerminal;
    }

    @Override
    protected void init() {
        super.init();
        recalcLayout();
        buildSkillNodes();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        recalcLayout();
        buildSkillNodes();
    }

    private void recalcLayout() {
        float scale = Math.min(this.width / 480f, this.height / 300f);
        scale = Math.max(scale, 1.0f);

        this.guiWidth = Math.max(MIN_GUI_WIDTH, (int) (360 * scale));
        this.guiHeight = Math.max(MIN_GUI_HEIGHT, (int) (220 * scale));
        this.guiWidth = Math.min(this.guiWidth, this.width - 20);
        this.guiHeight = Math.min(this.guiHeight, this.height - 20);

        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;

        this.colWidth = Math.max(52, (this.guiWidth - PADDING * 2) / 5);
        this.skillWidth = colWidth - 6;

        this.treeAreaLeft = this.guiLeft + PADDING;
        this.treeAreaTop = this.guiTop + TOP_BAR_HEIGHT + LEVEL_HEADER_HEIGHT;
        this.treeAreaWidth = this.guiWidth - PADDING * 2;
        this.treeAreaHeight = this.guiHeight - TOP_BAR_HEIGHT - LEVEL_HEADER_HEIGHT - PADDING;
    }

    @Override
    public void tick() {
        super.tick();
        if (skillNodes.isEmpty()) {
            buildSkillNodes();
        }
    }

    private void buildSkillNodes() {
        skillNodes.clear();
        scrollOffset = 0;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        AbilityCategory category = data.getCurrentAbility();
        List<Skill> allSkills = SkillRegistry.getSkillsByCategory(category);

        int totalContentHeight = 0;

        for (int level = 1; level <= 5; level++) {
            List<Skill> levelSkills = new ArrayList<>();
            for (Skill s : allSkills) {
                if (s.getLevel() == level) levelSkills.add(s);
            }
            if (levelSkills.isEmpty()) continue;

            int colX = this.treeAreaLeft + (level - 1) * colWidth;
            int columnTotalHeight = levelSkills.size() * SKILL_HEIGHT + (levelSkills.size() - 1) * SKILL_GAP;
            int startY = this.treeAreaTop + (treeAreaHeight - columnTotalHeight) / 2;

            for (int i = 0; i < levelSkills.size(); i++) {
                Skill skill = levelSkills.get(i);
                int y = startY + i * (SKILL_HEIGHT + SKILL_GAP);
                boolean learned = data.hasLearnedSkill(skill.getId());
                boolean canLearn = data.canLearnSkill(skill);
                boolean isPassive = skill.getType() == SkillType.PASSIVE;

                int nodeW = SKILL_HEIGHT;
                int nodeX = colX + (skillWidth - nodeW) / 2;
                skillNodes.add(new SkillNode(skill, nodeX, y, nodeW, SKILL_HEIGHT, learned, canLearn, isPassive));
            }

            totalContentHeight = Math.max(totalContentHeight, columnTotalHeight);
        }

        maxScroll = Math.max(0, totalContentHeight - treeAreaHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 300);

        graphics.fill(this.guiLeft, this.guiTop, this.guiLeft + guiWidth, this.guiTop + guiHeight, COLOR_BG);
        graphics.fill(this.guiLeft, this.guiTop, this.guiLeft + guiWidth, this.guiTop + TOP_BAR_HEIGHT, COLOR_TOP_BAR);

        if (fromTerminal) {
            int backX = this.guiLeft + 4;
            int backY = this.guiTop + 4;
            hoveredBack = mouseX >= backX && mouseX < backX + BACK_BTN_SIZE
                    && mouseY >= backY && mouseY < backY + BACK_BTN_SIZE;

            graphics.fill(backX, backY, backX + BACK_BTN_SIZE, backY + BACK_BTN_SIZE,
                    hoveredBack ? COLOR_BACK_HOVER : COLOR_BACK_BG);
            drawBorder(graphics, backX, backY, BACK_BTN_SIZE, BACK_BTN_SIZE, COLOR_BACK_BORDER);
            String arrow = "<-";
            int aw = this.font.width(arrow);
            graphics.drawString(this.font, arrow, backX + (BACK_BTN_SIZE - aw) / 2, backY + 5, COLOR_TEXT_WHITE);
        }

        hoveredNode = null;

        drawTopBar(graphics);
        drawLevelHeaders(graphics);

        graphics.enableScissor(treeAreaLeft, treeAreaTop, treeAreaLeft + treeAreaWidth, treeAreaTop + treeAreaHeight);
        graphics.pose().pushPose();
        graphics.pose().translate(0, -scrollOffset, 0);

        int adjustedMouseY = mouseY + scrollOffset;
        drawConnectionLines(graphics);
        drawSkillNodes(graphics, mouseX, adjustedMouseY);

        graphics.pose().popPose();
        graphics.disableScissor();

        if (maxScroll > 0) {
            drawScrollBar(graphics);
        }

        Minecraft mc = Minecraft.getInstance();
        PlayerAbilityData data = mc.player != null ? mc.player.getData(AcademyAttachments.PLAYER_ABILITY) : null;
        if (data != null && !data.hasAbility()) {
            String msg = "尚未获得能力，请先使用能力诱导因子。";
            int textWidth = this.font.width(msg);
            graphics.drawString(this.font, msg, this.guiLeft + (guiWidth - textWidth) / 2, this.guiTop + guiHeight / 2 - 4, COLOR_TEXT_GRAY);
        }

        if (hoveredNode != null) {
            drawTooltip(graphics, mouseX, mouseY);
        }

        graphics.pose().popPose();
    }

    private void drawScrollBar(GuiGraphics graphics) {
        int scrollBarX = this.guiLeft + guiWidth - 6;
        int scrollBarTop = treeAreaTop;
        int scrollBarHeight = treeAreaHeight;
        int thumbHeight = Math.max(12, scrollBarHeight * treeAreaHeight / (treeAreaHeight + maxScroll));
        int thumbY = scrollBarTop + (scrollBarHeight - thumbHeight) * scrollOffset / maxScroll;

        graphics.fill(scrollBarX, scrollBarTop, scrollBarX + 3, scrollBarTop + scrollBarHeight, 0x44FFFFFF);
        graphics.fill(scrollBarX, thumbY, scrollBarX + 3, thumbY + thumbHeight, COLOR_SCROLL_BAR);
    }

    private void drawTopBar(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        int x = this.guiLeft + (fromTerminal ? 26 : 8);
        int y = this.guiTop + 5;

        String abilityName = Component.translatable("item.academy.factor_" + data.getCurrentAbility().id()).getString();
        graphics.drawString(this.font, abilityName + "  Lv." + data.getPlayerLevel(), x, y, COLOR_TEXT_WHITE);

        int barX = x;
        int barY = y + 12;
        int barW = Math.min(160, (guiWidth - 80) / 2);
        int barH = BAR_HEIGHT;

        graphics.fill(barX, barY, barX + barW, barY + barH, COLOR_CP_BG);
        float cpRatio = data.getMaxCp() > 0 ? data.getCurrentCp() / data.getMaxCp() : 0;
        int cpFill = (int) (barW * cpRatio);
        graphics.fill(barX, barY, barX + cpFill, barY + barH, COLOR_CP_BAR);
        graphics.drawString(this.font, String.format("CP: %.0f/%.0f", data.getCurrentCp(), data.getMaxCp()), barX + barW + 4, barY, COLOR_TEXT_WHITE, false);

        barY += 12;
        graphics.fill(barX, barY, barX + barW, barY + barH, COLOR_OVERLOAD_BG);
        float olRatio = data.getMaxOverload() > 0 ? data.getCurrentOverload() / data.getMaxOverload() : 0;
        int olFill = (int) (barW * olRatio);
        graphics.fill(barX, barY, barX + olFill, barY + barH, COLOR_OVERLOAD_BAR);
        graphics.drawString(this.font, String.format("OL: %.0f/%.0f", data.getCurrentOverload(), data.getMaxOverload()), barX + barW + 4, barY, COLOR_TEXT_WHITE, false);
    }

    private void drawLevelHeaders(GuiGraphics graphics) {
        for (int level = 1; level <= 5; level++) {
            int colX = this.treeAreaLeft + (level - 1) * colWidth;
            int colTop = this.guiTop + TOP_BAR_HEIGHT + 2;
            String levelText = "Lv." + level;
            int tw = this.font.width(levelText);
            int nodeW = SKILL_HEIGHT;
            int nodeX = colX + (skillWidth - nodeW) / 2;
            graphics.drawString(this.font, levelText, nodeX + (nodeW - tw) / 2, colTop, COLOR_TEXT_GRAY);
        }
    }

    private void drawConnectionLines(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        for (SkillNode node : skillNodes) {
            for (Skill.Prerequisite prereq : node.skill.getPrerequisites()) {
                String prereqId = prereq.skillId();
                if (prereqId.startsWith("any_level_")) continue;

                SkillNode prereqNode = findNode(prereqId);
                if (prereqNode != null) {
                    int x1 = prereqNode.x + prereqNode.w;
                    int y1 = prereqNode.y + prereqNode.h / 2;
                    int x2 = node.x;
                    int y2 = node.y + node.h / 2;

                    int lineColor = data.hasLearnedSkill(prereqId) ? COLOR_LEARNED : COLOR_LINE;
                    int midX = (x1 + x2) / 2;

                    graphics.fill(x1, y1, midX, y1 + 1, lineColor);
                    graphics.fill(midX, Math.min(y1, y2), midX + 1, Math.max(y1, y2) + 1, lineColor);
                    graphics.fill(midX, y2, x2, y2 + 1, lineColor);
                }
            }
        }
    }

    private void drawSkillNodes(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        for (SkillNode node : skillNodes) {
            boolean isHovered = mouseX >= node.x && mouseX < node.x + node.w
                    && mouseY >= node.y && mouseY < node.y + node.h;
            if (isHovered) hoveredNode = node;

            int bgColor, borderColor;
            if (node.learned) {
                bgColor = node.isPassive ? COLOR_PASSIVE_LEARNED : COLOR_LEARNED;
                borderColor = node.isPassive ? COLOR_PASSIVE_BORDER : COLOR_LEARNED_BORDER;
            } else if (node.canLearn) {
                bgColor = node.isPassive ? COLOR_PASSIVE_AVAILABLE : COLOR_AVAILABLE;
                borderColor = node.isPassive ? COLOR_PASSIVE_BORDER : COLOR_AVAILABLE_BORDER;
            } else {
                bgColor = COLOR_LOCKED;
                borderColor = COLOR_LOCKED_BORDER;
            }

            graphics.fill(node.x, node.y, node.x + node.w, node.y + node.h, bgColor);
            graphics.fill(node.x, node.y, node.x + node.w, node.y + 1, borderColor);
            graphics.fill(node.x, node.y + node.h - 1, node.x + node.w, node.y + node.h, borderColor);
            graphics.fill(node.x, node.y, node.x + 1, node.y + node.h, borderColor);
            graphics.fill(node.x + node.w - 1, node.y, node.x + node.w, node.y + node.h, borderColor);

            if (isHovered) {
                graphics.fill(node.x + 1, node.y + 1, node.x + node.w - 1, node.y + node.h - 1, COLOR_HOVER);
            }

            ResourceLocation icon = node.skill.getIconLocation();
            int iconSize = 16;
            int iconX = node.x + (node.w - iconSize) / 2;
            int iconY = node.y + (node.h - iconSize) / 2;
            graphics.blit(icon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);

            if (node.learned && !node.isPassive) {
                float prof = data.getProficiency(node.skill.getId());
                int profBarW = (int) ((node.w - 4) * prof);
                graphics.fill(node.x + 2, node.y + node.h - 1, node.x + 2 + profBarW, node.y + node.h, 0xFFffffff);
            }

            if (node.learned && !node.isPassive) {
                float prof = data.getProficiency(node.skill.getId());
                int profBarW = (int) ((node.w - 4) * prof);
                graphics.fill(node.x + 2, node.y + node.h - 3, node.x + 2 + profBarW, node.y + node.h - 2, 0xFFffffff);
            }
        }
    }

    private void drawTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        Skill skill = hoveredNode.skill;
        Minecraft mc = Minecraft.getInstance();
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);

        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable(skill.getTranslationKey()));

        String desc = Component.translatable(skill.getDescKey()).getString();
        if (!desc.equals(skill.getDescKey())) {
            int maxCharsPerLine = Math.max(16, guiWidth / 18);
            for (int i = 0; i < desc.length(); i += maxCharsPerLine) {
                tooltip.add(Component.literal("§7" + desc.substring(i, Math.min(i + maxCharsPerLine, desc.length()))));
            }
        }

        tooltip.add(Component.empty());
        tooltip.add(Component.literal("§f等级: " + skill.getLevel() + "  类型: " + (skill.getType() == SkillType.PASSIVE ? "被动" : "主动")));
        if (skill.getBaseCpCost() > 0) {
            tooltip.add(Component.literal("§b计算力: " + (int) skill.getBaseCpCost() + "  §c过载: " + (int) skill.getBaseOverload()));
        }

        if (hoveredNode.learned) {
            tooltip.add(Component.literal("§a[已学习]"));
            if (!hoveredNode.isPassive) {
                float prof = data.getProficiency(skill.getId());
                tooltip.add(Component.literal("§e熟练度: " + String.format("%.1f%%", prof * 100)));
            }
        } else if (hoveredNode.canLearn) {
            tooltip.add(Component.literal("§b[点击学习]"));
        } else {
            tooltip.add(Component.literal("§c[未解锁]"));
            for (Skill.Prerequisite prereq : skill.getPrerequisites()) {
                String prereqId = prereq.skillId();
                if (prereqId.startsWith("any_level_")) {
                    int reqLv = Integer.parseInt(prereqId.substring("any_level_".length()));
                    tooltip.add(Component.literal("§7  需要任意 Lv." + reqLv + " 技能"));
                } else {
                    String prereqName = Component.translatable("item.academy.factor_" + skill.getCategory().id() + "." + prereqId).getString();
                    boolean met = data.hasLearnedSkill(prereqId) && data.getProficiency(prereqId) >= prereq.proficiencyRequired();
                    String status = met ? "§a✓" : "§c✗";
                    String reqText = prereq.proficiencyRequired() > 0
                            ? String.format(" %.0f%%", prereq.proficiencyRequired() * 100)
                            : "";
                    tooltip.add(Component.literal(status + " §7" + prereqName + reqText));
                }
            }
        }

        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 400);
        graphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
        graphics.pose().popPose();
    }

    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color) {
        graphics.fill(x, y, x + w, y + 1, color);
        graphics.fill(x, y + h - 1, x + w, y + h, color);
        graphics.fill(x, y, x + 1, y + h, color);
        graphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (fromTerminal && hoveredBack && button == 0) {
            Minecraft.getInstance().setScreen(new DataTerminalGui(true));
            return true;
        }

        if (hoveredNode != null && hoveredNode.canLearn && !hoveredNode.learned && button == 0) {
            PacketDistributor.sendToServer(new LearnSkillPacket(hoveredNode.skill.getId()));
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
                data.learnSkill(hoveredNode.skill.getId());
                mc.player.setData(AcademyAttachments.PLAYER_ABILITY, data);
            }
            buildSkillNodes();
            return true;
        }

        int scrollBarX = this.guiLeft + guiWidth - 6;
        if (maxScroll > 0 && mouseX >= scrollBarX && mouseX <= scrollBarX + 3
                && mouseY >= treeAreaTop && mouseY <= treeAreaTop + treeAreaHeight) {
            isScrolling = true;
            updateScrollFromMouse(mouseY);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isScrolling) {
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (maxScroll > 0 && mouseX >= treeAreaLeft && mouseX <= treeAreaLeft + treeAreaWidth
                && mouseY >= treeAreaTop && mouseY <= treeAreaTop + treeAreaHeight) {
            scrollOffset = (int) Math.clamp(scrollOffset - scrollY * 12, 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void updateScrollFromMouse(double mouseY) {
        int scrollBarHeight = treeAreaHeight;
        int thumbHeight = Math.max(12, scrollBarHeight * treeAreaHeight / (treeAreaHeight + maxScroll));
        double ratio = (mouseY - treeAreaTop - thumbHeight / 2.0) / (scrollBarHeight - thumbHeight);
        scrollOffset = (int) Math.clamp(ratio * maxScroll, 0, maxScroll);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private SkillNode findNode(String skillId) {
        for (SkillNode node : skillNodes) {
            if (node.skill.getId().equals(skillId)) return node;
        }
        return null;
    }

    private record SkillNode(Skill skill, int x, int y, int w, int h, boolean learned, boolean canLearn, boolean isPassive) {
    }
}
