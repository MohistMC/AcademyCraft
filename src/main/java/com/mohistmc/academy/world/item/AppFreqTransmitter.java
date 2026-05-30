package com.mohistmc.academy.world.item;

public class AppFreqTransmitter extends BaseApp {
    public AppFreqTransmitter() {
        super(new Properties());
    }

    @Override
    public String getAppId() {
        return "freq_transmitter";
    }
}
