package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcademySounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AcademyCraft.MODID);

    public static final Holder<SoundEvent> MY_SOUND = SOUND_EVENTS.register(
            "entity.flipcoin",
            SoundEvent::createVariableRangeEvent
    );
}
