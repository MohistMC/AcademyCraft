package com.mohistmc.academy.client.gui;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.skill.AbilityCategory;
import com.mohistmc.academy.skill.AcademyAttachments;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.Skill;
import com.mohistmc.academy.skill.SkillRegistry;
import com.mohistmc.academy.skill.SkillType;
import com.mohistmc.academy.world.block.DevMachineType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@OnlyIn(Dist.CLIENT)
public class SkillTreeGui extends AcademyScreen {

    private static final int MIN_GUI_WIDTH = 320;
    private static final int MIN_GUI_HEIGHT = 200;
    private static final int PADDING = 10;
    private static final int TOP_BAR_HEIGHT = 26;
    private static final int SKILL_HEIGHT = 18;
    private static final int SKILL_GAP = 4;
    private static final int LEVEL_HEADER_HEIGHT = 14;
    private static final int INFO_PANEL_WIDTH = 120;

    // 技能树业务颜色
    private static final int COLOR_LEARNED = 0xFF2ecc71;
    private static final int COLOR_LEARNED_BORDER = 0xFF27ae60;
    private static final int COLOR_AVAILABLE = 0xFF3498db;
    private static final int COLOR_AVAILABLE_BORDER = 0xFF2980b9;
    private static final int COLOR_LOCKED = 0xFF555566;
    private static final int COLOR_LOCKED_BORDER = 0xFF444455;
    private static final int COLOR_PASSIVE_LEARNED = 0xFFf39c12;
    private static final int COLOR_PASSIVE_BORDER = 0xFFe67e22;
    private static final int COLOR_PASSIVE_AVAILABLE = 0xFF9b59b6;
    private static final int COLOR_LINE = 0xFF666688;
    private static final int COLOR_SCROLL_BAR = 0x88FFFFFF;

    private int colWidth;
    private int skillWidth;
    private int treeAreaLeft;
    private int treeAreaTop;
    private int treeAreaWidth;
    private int treeAreaHeight;
    private int maxScroll = 0;
    private int scrollOffset = 0;
    private boolean isScrolling = false;
    private final boolean fromTerminal;
    private boolean hoveredBack = false;
    private final boolean readOnly;
    private final DevMachineType devType;
    private final int energy;
    private final int maxEnergy;
    private final BlockPos devPos;

    private final List<SkillNode> skillNodes = new ArrayList<>();
    private SkillNode hoveredNode = null;

    public SkillTreeGui() { this(false, false, null, 0, 0, null); }
    public SkillTreeGui(boolean fromTerminal) { this(fromTerminal, false, null, 0, 0, null); }
    public SkillTreeGui(boolean fromTerminal, boolean readOnly) { this(fromTerminal, readOnly, null, 0, 0, null); }
    public SkillTreeGui(boolean fromTerminal, boolean readOnly, DevMachineType devType, int energy, int maxEnergy) {
        this(fromTerminal, readOnly, devType, energy, maxEnergy, null);
    }
    public SkillTreeGui(boolean fromTerminal, boolean readOnly, DevMachineType devType, int energy, int maxEnergy, BlockPos devPos) {
        super(Component.translatable("block.academy.dev_normal"));
        this.fromTerminal = fromTerminal;
        this.readOnly = readOnly || devType == null;
        this.devType = devType;
        this.energy = energy;
        this.maxEnergy = maxEnergy;
        this.devPos = devPos;
    }

    @Override protected void init() { super.init(); recalcLayout(); buildSkillNodes(); }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        recalcLayout();
        buildSkillNodes();
    }

    private void recalcLayout() {
        float scale = Math.min(this.width / 480f, this.height / 300f);
        this.guiWidth = Math.max(MIN_GUI_WIDTH, (int) (360 * scale));
        this.guiHeight = Math.max(MIN_GUI_HEIGHT, (int) (220 * scale));
        this.guiWidth = Math.min(this.guiWidth, this.width - 20);
        this.guiHeight = Math.min(this.guiHeight, this.height - 20);
        this.guiLeft = (this.width - this.guiWidth) / 2;
        this.guiTop = (this.height - this.guiHeight) / 2;
        this.colWidth = Math.max(24, (this.guiWidth - PADDING * 2 - INFO_PANEL_WIDTH) / 5);
        this.skillWidth = colWidth - 6;
        this.treeAreaLeft = this.guiLeft + PADDING + INFO_PANEL_WIDTH;
        this.treeAreaTop = this.guiTop + TOP_BAR_HEIGHT + LEVEL_HEADER_HEIGHT;
        this.treeAreaWidth = this.guiWidth - PADDING * 2 - INFO_PANEL_WIDTH;
        this.treeAreaHeight = this.guiHeight - TOP_BAR_HEIGHT - LEVEL_HEADER_HEIGHT - PADDING;
    }

    @Override public void tick() { super.tick(); if (skillNodes.isEmpty()) buildSkillNodes(); }

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
            for (Skill s : allSkills) if (s.getLevel() == level) levelSkills.add(s);
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
                int nodeX = colX + (skillWidth - SKILL_HEIGHT) / 2;
                skillNodes.add(new SkillNode(skill, nodeX, y, SKILL_HEIGHT, SKILL_HEIGHT, learned, canLearn, isPassive));
            }
            totalContentHeight = Math.max(totalContentHeight, columnTotalHeight);
        }
        maxScroll = Math.max(0, totalContentHeight - treeAreaHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        pushZ(graphics);

        graphics.fill(this.guiLeft, this.guiTop, this.guiLeft + guiWidth, this.guiTop + guiHeight, AcademyColors.BG_DARK);
        graphics.fill(this.guiLeft, this.guiTop, this.guiLeft + guiWidth, this.guiTop + TOP_BAR_HEIGHT, AcademyColors.BG_PANEL);

        if (fromTerminal) {
            int backX = this.guiLeft + 4, backY = this.guiTop + 4;
            hoveredBack = drawBackButton(graphics, backX, backY, mouseX, mouseY);
        }

        drawDevInfoPanel(graphics);
        hoveredNode = null;
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
            int scrollBarX = this.guiLeft + guiWidth - 6, thumbHeight = Math.max(12, treeAreaHeight * treeAreaHeight / (treeAreaHeight + maxScroll));
            int thumbY = treeAreaTop + (treeAreaHeight - thumbHeight) * scrollOffset / maxScroll;
            graphics.fill(scrollBarX, treeAreaTop, scrollBarX + 3, treeAreaTop + treeAreaHeight, 0x44FFFFFF);
            graphics.fill(scrollBarX, thumbY, scrollBarX + 3, thumbY + thumbHeight, COLOR_SCROLL_BAR);
        }

        Minecraft mc = Minecraft.getInstance();
        PlayerAbilityData data = mc.player != null ? mc.player.getData(AcademyAttachments.PLAYER_ABILITY) : null;
        if (data != null && !data.hasAbility()) {
            String msg = "尚未获得能力，请先使用能力诱导因子。";
            graphics.drawString(this.font, msg, this.guiLeft + (guiWidth - this.font.width(msg)) / 2,
                    this.guiTop + guiHeight / 2 - 4, AcademyColors.TEXT_SECONDARY);
        }

        if (hoveredNode != null) drawTooltip(graphics, mouseX, mouseY);
        popZ(graphics);
    }

    private void drawDevInfoPanel(GuiGraphics graphics) {
        int panelLeft = this.guiLeft + PADDING;
        int panelTop = this.treeAreaTop;
        int panelWidth = INFO_PANEL_WIDTH - PADDING;
        int panelHeight = this.treeAreaHeight;

        graphics.fill(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xCC1a1a2e);
        drawBorder(graphics, panelLeft, panelTop, panelWidth, panelHeight, 0xFF444455);

        int y = panelTop + 4;
        Minecraft mc = Minecraft.getInstance();
        PlayerAbilityData data = mc.player != null ? mc.player.getData(AcademyAttachments.PLAYER_ABILITY) : null;

        if (data != null && data.hasAbility()) {
            int iconSize = 16, iconX = panelLeft + 5;
            ResourceLocation abilityIcon = ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID,
                    "textures/abilities/" + data.getCurrentAbility().id() + "/icon.png");
            graphics.blit(abilityIcon, iconX, y, 0, 0, iconSize, iconSize, iconSize, iconSize);
            graphics.drawString(this.font, Component.translatable("item.academy.factor_" + data.getCurrentAbility().id()).getString(),
                    iconX + iconSize + 4, y + 4, AcademyColors.TEXT);
            y += iconSize + 6;

            int barW = panelWidth - 10;
            y = GuiUtils.drawProgressBar(graphics, this.font, panelLeft + 5, y, barW, "CP",
                    (int) data.getCurrentCp(), (int) data.getMaxCp(), AcademyColors.SUCCESS, AcademyColors.PROGRESS_BG);
            y += 2;
            y = GuiUtils.drawProgressBar(graphics, this.font, panelLeft + 5, y, barW, "OL",
                    (int) data.getCurrentOverload(), (int) data.getMaxOverload(), AcademyColors.ERROR, AcademyColors.PROGRESS_BG);
            y += 4;
        }

        if (!fromTerminal && devType != null) {
            int minY = panelTop + panelHeight - 54 - 4;
            if (y < minY) y = minY;
            Component title = Component.literal(devType.displayName + "开发机").withStyle(
                    net.minecraft.network.chat.Style.EMPTY.withBold(true));
            graphics.drawString(this.font, title, panelLeft + 5, y - 2, AcademyColors.TEXT);
            y += 10;
            y = GuiUtils.drawProgressBar(graphics, this.font, panelLeft + 5, y, panelWidth - 10, "IF能量",
                    energy, maxEnergy, 0xFFf1c40f, AcademyColors.PROGRESS_BG);
            y += 4;
            y = GuiUtils.drawProgressBar(graphics, this.font, panelLeft + 5, y, panelWidth - 10, "同步率",
                    devType.syncRate, 100, AcademyColors.INFO, AcademyColors.PROGRESS_BG);
        }
    }

    private void drawLevelHeaders(GuiGraphics graphics) {
        for (int level = 1; level <= 5; level++) {
            int colX = this.treeAreaLeft + (level - 1) * colWidth;
            String levelText = "Lv." + level;
            int tw = this.font.width(levelText);
            int nodeX = colX + (skillWidth - SKILL_HEIGHT) / 2;
            graphics.drawString(this.font, levelText, nodeX + (SKILL_HEIGHT - tw) / 2, this.treeAreaTop + 2, AcademyColors.TEXT_SECONDARY);
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
                if (prereqNode == null) continue;

                int x1 = prereqNode.x + prereqNode.w, y1 = prereqNode.y + prereqNode.h / 2;
                int x2 = node.x, y2 = node.y + node.h / 2;
                int lineColor = data.hasLearnedSkill(prereqId) ? COLOR_LEARNED : COLOR_LINE;
                int midX = (x1 + x2) / 2;

                graphics.fill(x1, y1, midX, y1 + 1, lineColor);
                graphics.fill(midX, Math.min(y1, y2), midX + 1, Math.max(y1, y2) + 1, lineColor);
                graphics.fill(midX, y2, x2, y2 + 1, lineColor);
            }
        }
    }

    private void drawSkillNodes(GuiGraphics graphics, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        PlayerAbilityData data = mc.player.getData(AcademyAttachments.PLAYER_ABILITY);
        if (!data.hasAbility()) return;

        for (SkillNode node : skillNodes) {
            boolean isHovered = mouseX >= node.x && mouseX < node.x + node.w && mouseY >= node.y && mouseY < node.y + node.h;
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

            if (isHovered) {
                graphics.pose().pushPose();
                float cx = node.x + node.w / 2f, cy = node.y + node.h / 2f;
                graphics.pose().translate(cx, cy, 0);
                graphics.pose().scale(1.15f, 1.15f, 1.0f);
                graphics.pose().translate(-cx, -cy, 0);
            }

            graphics.fill(node.x, node.y, node.x + node.w, node.y + node.h, bgColor);
            drawBorder(graphics, node.x, node.y, node.w, node.h, borderColor);
            if (isHovered) graphics.fill(node.x + 1, node.y + 1, node.x + node.w - 1, node.y + node.h - 1, 0x44FFFFFF);

            ResourceLocation icon = node.skill.getIconLocation();
            int iconSize = 16;
            graphics.blit(icon, node.x + (node.w - iconSize) / 2, node.y + (node.h - iconSize) / 2,
                    0, 0, iconSize, iconSize, iconSize, iconSize);

            if (node.learned && !node.isPassive) {
                int profBarW = (int) ((node.w - 4) * data.getProficiency(node.skill.getId()));
                graphics.fill(node.x + 2, node.y + node.h - 3, node.x + 2 + profBarW, node.y + node.h - 2, 0xFFffffff);
            }

            if (isHovered) graphics.pose().popPose();
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
            // 已学习状态
            tooltip.add(Component.literal("§a[已学习]"));
            if (!hoveredNode.isPassive) {
                float prof = data.getProficiency(skill.getId());
                tooltip.add(Component.literal("§e熟练度: " + String.format("%.1f%%", prof * 100)));
            }
            // 开发机能量不足时追加提示
            if (!fromTerminal && devType != null && energy <= 0) {
                tooltip.add(Component.literal("§c[开发机能量不足] 该技能暂不可用"));
            }
        } else if (hoveredNode.canLearn) {
            // 可学习状态
            if (readOnly) {
                tooltip.add(Component.literal("§7[仅查看] 请使用开发机学习"));
            } else if (skill.getLevel() > devType.maxLevel) {
                tooltip.add(Component.literal("§c[同步率不足] 该开发机无法支持此等级技能"));
            } else if (!fromTerminal && devType != null && energy <= 0) {
                // 能量不足时优先提示
                tooltip.add(Component.literal("§c[开发机能量不足] 暂无法学习该技能"));
            } else {
                int cost = devType.applySyncRate(100 + skill.getLevel() * 50);
                tooltip.add(Component.literal("§b[点击学习] 消耗: " + cost + " IF"));
            }
        } else {
            // 未解锁状态
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


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (fromTerminal && hoveredBack && button == 0) {
            Minecraft.getInstance().setScreen(new DataTerminalGui());
            return true;
        }
        if (!readOnly && hoveredNode != null && hoveredNode.canLearn && !hoveredNode.learned && button == 0) {
            if (hoveredNode.skill.getLevel() > devType.maxLevel) return true;
            if (!fromTerminal && devType != null && energy <= 0) {
                return true;
            }
            PacketDistributor.sendToServer(new LearnSkillPacket(hoveredNode.skill.getId(), devType.ordinal(), java.util.Optional.ofNullable(devPos)));
            return true;
        }
        if (maxScroll > 0) {
            int scrollBarX = this.guiLeft + guiWidth - 6;
            if (mouseX >= scrollBarX && mouseX <= scrollBarX + 3 && mouseY >= treeAreaTop && mouseY <= treeAreaTop + treeAreaHeight) {
                isScrolling = true;
                updateScrollFromMouse(mouseY);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isScrolling) { updateScrollFromMouse(mouseY); return true; }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) { isScrolling = false; return super.mouseReleased(mouseX, mouseY, button); }

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

    private SkillNode findNode(String skillId) {
        for (SkillNode node : skillNodes) if (node.skill.getId().equals(skillId)) return node;
        return null;
    }

    private record SkillNode(Skill skill, int x, int y, int w, int h, boolean learned, boolean canLearn, boolean isPassive) {}
}
