package com.mohistmc.academy.terminal;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Mgazul
 * @date 2026/5/31 04:09
 */
public class BuiltinApp implements TerminalApp {

    private final String appId;
    private final String nameKey;
    private final ResourceLocation icon;
    private Consumer<Minecraft> openAction;

    public BuiltinApp(String appId, String nameKey) {
        this.appId = appId;
        this.nameKey = nameKey;
        this.icon = TerminalApp.super.getIcon();
    }

    public BuiltinApp(String appId, String nameKey, ResourceLocation icon) {
        this.appId = appId;
        this.nameKey = nameKey;
        this.icon = icon;
    }

    public void setOpenAction(Consumer<Minecraft> action) {
        this.openAction = action;
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public String getNameKey() {
        return nameKey;
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public boolean isBuiltIn() {
        return true;
    }

    @Override
    public void open(Minecraft minecraft) {
        if (openAction != null) {
            openAction.accept(minecraft);
        }
    }
}
