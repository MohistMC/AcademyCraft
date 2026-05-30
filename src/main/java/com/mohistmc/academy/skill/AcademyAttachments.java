package com.mohistmc.academy.skill;

import com.mohistmc.academy.AcademyCraft;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;


/**
 * @author Mgazul
 * @date 2026/5/30 20:29
 */
public class AcademyAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AcademyCraft.MODID);

    public static final Supplier<AttachmentType<PlayerAbilityData>> PLAYER_ABILITY =
            ATTACHMENT_TYPES.register("player_ability",
                    () -> AttachmentType.builder(PlayerAbilityData::new)
                            .serialize(new PlayerAbilityDataCodec())
                            .build());
}
