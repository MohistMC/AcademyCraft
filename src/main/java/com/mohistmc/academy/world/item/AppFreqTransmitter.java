package com.mohistmc.academy.world.item;

import com.mohistmc.academy.terminal.AppRegistry;

public class AppFreqTransmitter extends BaseApp {
    public AppFreqTransmitter() {
        super(new Properties());
    }

    @Override
    public String getAppId() {
        return AppRegistry.FREQ_TRANSMITTER.getAppId();
    }
}
