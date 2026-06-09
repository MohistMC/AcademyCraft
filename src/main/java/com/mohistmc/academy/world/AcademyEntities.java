package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.entity.RailgunBeamEntity;
import com.mohistmc.academy.world.entity.CoinEntity;
import com.mohistmc.academy.world.entity.OreHighlightEntity;
import java.util.function.Supplier;
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

    public static final DeferredHolder<EntityType<?>, EntityType<OreHighlightEntity>> ORE_HIGHLIGHT = ENTITIES.register("ore_highlight",
            () -> EntityType.Builder.of(OreHighlightEntity::new, MobCategory.MISC)
                    .sized(1.0f, 1.0f)
                    .clientTrackingRange(64)
                    .updateInterval(20)
                    .build("ore_highlight"));

    public static final DeferredHolder<EntityType<?>, EntityType<RailgunBeamEntity>> RAILGUN_BEAM = ENTITIES.register("railgun_beam",
            () -> EntityType.Builder.of(RailgunBeamEntity::new, MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(64)
            .updateInterval(1)
            .noSummon()
            .build("railgun_beam"));
}
