package com.mohistmc.academy.world.item;

import com.mohistmc.academy.terminal.AppRegistry;

public class AppSettings extends BaseApp {
    public AppSettings() {
        super(new Properties());
    }

    @Override
    public String getAppId() {
        return AppRegistry.SETTINGS.getAppId();
    }
}
