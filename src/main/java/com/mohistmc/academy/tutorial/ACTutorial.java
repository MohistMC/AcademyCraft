package com.mohistmc.academy.tutorial;

import com.mohistmc.academy.AcademyCraft;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.player.Player;

/**
 * 教程条目 —— 内容从 assets/academy/tutorials/{lang}/{id}.md 加载。
 */
public class ACTutorial {

    public enum Tag {
        CRAFT, SMELT, VIEW;

        public final ResourceLocation icon = ResourceLocation.fromNamespaceAndPath(
                AcademyCraft.MODID, "textures/guis/icons/icon_" + this.name().toLowerCase(Locale.ROOT) + ".png");
    }

    public static final boolean SHOW_ALL = false;

    public final String id;

    private Condition condition = Conditions.alwaysTrue();
    private boolean defaultInstalled = true;

    // 教程 md 资源为烘焙进包的只读内容，运行时不会变化，缓存避免每次调用重复读盘
    private String cachedContent;

    private final List<ViewGroup> previewHandlers = new ArrayList<>();

    public ACTutorial(String id) {
        this.id = id;
    }

    public ACTutorial addCondition(Condition condition) {
        defaultInstalled = false;
        if (this.condition == Conditions.alwaysTrue()) {
            this.condition = condition;
        } else {
            this.condition = this.condition.or(condition);
        }
        return this;
    }

    public ACTutorial addPreview(ViewGroup... handlers) {
        for (ViewGroup h : handlers) previewHandlers.add(h);
        return this;
    }

    public List<ViewGroup> getPreview() {
        return previewHandlers;
    }

    public String getContent() {
        if (cachedContent != null) return cachedContent;
        final String unknown = "![title]\nUNKNOWN \n![brief]\n![content]\n ";
        try {
            String lang = Minecraft.getInstance().getLanguageManager().getSelected();
            String s = readMd(lang);
            if (s == null) s = readMd("en_us");
            cachedContent = s == null ? unknown : s;
        } catch (Exception e) {
            cachedContent = unknown;
        }
        return cachedContent;
    }

    private String readMd(String lang) {
        try {
            Optional<Resource> opt = Minecraft.getInstance().getResourceManager()
                    .getResource(ResourceLocation.fromNamespaceAndPath(AcademyCraft.MODID, "tutorials/" + lang + "/" + id + ".md"));
            if (opt.isEmpty()) return null;
            try (InputStream in = opt.get().open()) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            return null;
        }
    }

    /** 解析 ![title] 段 */
    public String getTitle() {
        String raw = getContent();
        int i1 = raw.indexOf("![title]"), i2 = raw.indexOf("![brief]");
        if (i1 == -1 || i2 == -1 || i1 >= i2) return id;
        return raw.substring(i1 + 8, i2).trim();
    }

    /** 解析 ![brief] 段 */
    public String getBrief() {
        String raw = getContent();
        int i1 = raw.indexOf("![brief]"), i2 = raw.indexOf("![content]");
        if (i1 == -1 || i2 == -1 || i1 >= i2) return "";
        return raw.substring(i1 + 8, i2).trim();
    }

    /** 解析 ![content] 段 */
    public String getContentText() {
        String raw = getContent();
        int i = raw.indexOf("![content]");
        if (i == -1) return raw;
        return raw.substring(i + 10);
    }

    public boolean isActivated(Player player) {
        if (SHOW_ALL) return true;
        return this.condition.test(player);
    }

    public boolean isDefaultInstalled() {
        return defaultInstalled;
    }
}
