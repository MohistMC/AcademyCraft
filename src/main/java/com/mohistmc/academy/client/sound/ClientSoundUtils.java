package com.mohistmc.academy.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/**
 * 客户端音效播放工具（仅用于无位置的 UI 音效）。
 */
public class ClientSoundUtils {

    /**
     * 客户端播放 UI 音效（无空间衰减）。
     */
    public static void playClient(Holder<SoundEvent> sound, SoundSource category, float volume, float pitch) {
        Minecraft.getInstance().getSoundManager().play(
                new SimpleSoundInstance(sound.value().getLocation(), category, volume, pitch,
                        RandomSource.create(), false, 1,
                        SoundInstance.Attenuation.NONE, 0, 0, 0, true));
    }
}
