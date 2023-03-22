package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.block.entity.CatEngineBlockEntity;
import com.mohistmc.academy.client.block.entity.DevNormalBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 方块
 *
 * @author lliiooll
 */
public class AcademyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AcademyCraft.MODID);


    public static final RegistryObject<BlockEntityType<CatEngineBlockEntity>> CAT_ENGINE = BLOCK_ENTITIES.register("cat_engine", () -> BlockEntityType.Builder.of(CatEngineBlockEntity::new, AcademyBlocks.CAT_ENGINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<DevNormalBlockEntity>> DEV_NORMAL = BLOCK_ENTITIES.register("dev_normal", () -> BlockEntityType.Builder.of(DevNormalBlockEntity::new, AcademyBlocks.DEV_NORMAL.get()).build(null));

}
