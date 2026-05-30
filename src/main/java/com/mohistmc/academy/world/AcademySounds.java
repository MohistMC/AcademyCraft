package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcademySounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AcademyCraft.MODID);

    public static final Holder<SoundEvent> FLIPCOIN = SOUND_EVENTS.register(
            "entity.flipcoin",
            SoundEvent::createVariableRangeEvent
    );

    public static final Holder<SoundEvent> MEDIA_RAILGUN = SOUND_EVENTS.register(
            "media.only_my_railgun",
            SoundEvent::createVariableRangeEvent
    );

    public static final Holder<SoundEvent> MEDIA_JUDGELIGHT = SOUND_EVENTS.register(
            "media.level5_judgelight",
            SoundEvent::createVariableRangeEvent
    );

    public static final Holder<SoundEvent> MEDIA_NOISE = SOUND_EVENTS.register(
            "media.sisters_noise",
            SoundEvent::createVariableRangeEvent
    );
}
