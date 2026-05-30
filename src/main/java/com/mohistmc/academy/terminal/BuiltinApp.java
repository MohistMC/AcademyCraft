package com.mohistmc.academy.terminal;


import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

/**
 * @author Mgazul
 * @date 2026/5/31 04:09
 */
class BuiltinApp implements TerminalApp {

    private final String appId;
    private final String nameKey;
    private final String icon;
    private Consumer<Minecraft> openAction;

    BuiltinApp(String appId, String nameKey, String icon) {
        this.appId = appId;
        this.nameKey = nameKey;
        this.icon = icon;
    }

    void setOpenAction(Consumer<Minecraft> action) {
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
    public String getIcon() {
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
