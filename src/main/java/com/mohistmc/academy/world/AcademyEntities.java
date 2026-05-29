package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.entity.CoinEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcademyEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, AcademyCraft.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<CoinEntity>> COIN_ENTITY = ENTITIES.register("coin_entity",
            () -> EntityType.Builder.of(CoinEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.1F) // 硬币的大小
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("coin_entity"));
}
