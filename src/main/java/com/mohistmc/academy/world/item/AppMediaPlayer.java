package com.mohistmc.academy.world.item;

import com.mohistmc.academy.terminal.AppRegistry;

public class AppMediaPlayer extends BaseApp {
    public AppMediaPlayer() {
        super(new Properties());
    }

    @Override
    public String getAppId() {
        return AppRegistry.MEDIA_PLAYER.getAppId();
    }
}
