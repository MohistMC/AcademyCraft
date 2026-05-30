package com.mohistmc.academy.world.item;

public class AppSettings extends BaseApp {
    public AppSettings() {
        super(new Properties());
    }

    @Override
    public String getAppId() {
        return "settings";
    }
}
