package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.client.block.entity.CatEngineBlockEntity;
import com.mohistmc.academy.client.block.entity.DevAdvancedBlockEntity;
import com.mohistmc.academy.client.block.entity.DevNormalBlockEntity;
import com.mohistmc.academy.client.block.entity.DevNormalSubBlockEntity;
import com.mohistmc.academy.client.block.entity.MatrixBlockEntity;
import com.mohistmc.academy.client.block.entity.MatrixSubBlockEntity;
import com.mohistmc.academy.client.block.entity.NodeBasicBlockEntity;
import com.mohistmc.academy.client.block.entity.PhaseGenBlockEntity;
import com.mohistmc.academy.client.block.entity.PhaseLiquidBlockEntity;
import com.mohistmc.academy.client.block.entity.SolarGenBlockEntity;
import com.mohistmc.academy.client.block.entity.WindGenBaseBlockEntity;
import com.mohistmc.academy.client.block.entity.WindGenBaseSubBlockEntity;
import com.mohistmc.academy.client.block.entity.WindGenFanBlockEntity;
import com.mohistmc.academy.client.block.entity.WindGenMainBlockEntity;
import com.mohistmc.academy.client.block.entity.WindGenPillarBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AcademyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AcademyCraft.MODID);


    public static final RegistryObject<BlockEntityType<CatEngineBlockEntity>> CAT_ENGINE = BLOCK_ENTITIES.register("cat_engine", () -> BlockEntityType.Builder.of(CatEngineBlockEntity::new, AcademyBlocks.CAT_ENGINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<DevNormalBlockEntity>> DEV_NORMAL = BLOCK_ENTITIES.register("dev_normal", () -> BlockEntityType.Builder.of(DevNormalBlockEntity::new, AcademyBlocks.DEV_NORMAL.get()).build(null));
    public static final RegistryObject<BlockEntityType<DevAdvancedBlockEntity>> DEV_ADVANCED = BLOCK_ENTITIES.register("dev_advanced", () -> BlockEntityType.Builder.of(DevAdvancedBlockEntity::new, AcademyBlocks.DEV_NORMAL.get()).build(null));
    public static final RegistryObject<BlockEntityType<DevNormalSubBlockEntity>> DEV_NORMAL_SUB = BLOCK_ENTITIES.register("dev_normal_sub", () -> BlockEntityType.Builder.of(DevNormalSubBlockEntity::new, AcademyBlocks.DEV_NORMAL_SUB.get()).build(null));
    public static final RegistryObject<BlockEntityType<DevAdvancedBlockEntity>> DEV_ADVANCED_SUB = BLOCK_ENTITIES.register("dev_advanced_sub", () -> BlockEntityType.Builder.of(DevAdvancedBlockEntity::new, AcademyBlocks.DEV_ADVANCED_SUB.get()).build(null));
    public static final RegistryObject<BlockEntityType<WindGenBaseSubBlockEntity>> WIND_GEN_BASE_SUB = BLOCK_ENTITIES.register("windgen_base_sub", () -> BlockEntityType.Builder.of(WindGenBaseSubBlockEntity::new, AcademyBlocks.WIND_GEN_BASE_SUB.get()).build(null));
    public static final RegistryObject<BlockEntityType<MatrixBlockEntity>> MATRIX = BLOCK_ENTITIES.register("matrix", () -> BlockEntityType.Builder.of(MatrixBlockEntity::new, AcademyBlocks.MATRIX.get()).build(null));
    public static final RegistryObject<BlockEntityType<MatrixSubBlockEntity>> MATRIX_SUB = BLOCK_ENTITIES.register("matrix_sub", () -> BlockEntityType.Builder.of(MatrixSubBlockEntity::new, AcademyBlocks.MATRIX_SUB.get()).build(null));
    public static final RegistryObject<BlockEntityType<PhaseGenBlockEntity>> PHASE_GEN = BLOCK_ENTITIES.register("phase_gen", () -> BlockEntityType.Builder.of(PhaseGenBlockEntity::new, AcademyBlocks.PHASE_GEN.get()).build(null));
    public static final RegistryObject<BlockEntityType<SolarGenBlockEntity>> SOLAR_GEN = BLOCK_ENTITIES.register("solar_gen", () -> BlockEntityType.Builder.of(SolarGenBlockEntity::new, AcademyBlocks.SOLAR_GEN.get()).build(null));
    public static final RegistryObject<BlockEntityType<WindGenBaseBlockEntity>> WINDGEN_BASE = BLOCK_ENTITIES.register("windgen_base", () -> BlockEntityType.Builder.of(WindGenBaseBlockEntity::new, AcademyBlocks.WINDGEN_BASE.get()).build(null));
    public static final RegistryObject<BlockEntityType<WindGenFanBlockEntity>> WINDGEN_FAN = BLOCK_ENTITIES.register("windgen_fan", () -> BlockEntityType.Builder.of(WindGenFanBlockEntity::new, AcademyBlocks.WINDGEN_FAN.get()).build(null));
    public static final RegistryObject<BlockEntityType<WindGenMainBlockEntity>> WINDGEN_MAIN = BLOCK_ENTITIES.register("windgen_main", () -> BlockEntityType.Builder.of(WindGenMainBlockEntity::new, AcademyBlocks.WINDGEN_MAIN.get()).build(null));
    public static final RegistryObject<BlockEntityType<WindGenPillarBlockEntity>> WINDGEN_PILLAR = BLOCK_ENTITIES.register("windgen_pillar", () -> BlockEntityType.Builder.of(WindGenPillarBlockEntity::new, AcademyBlocks.WINDGEN_PILLAR.get()).build(null));
    public static final RegistryObject<BlockEntityType<PhaseLiquidBlockEntity>> PHASE_LIQUID = BLOCK_ENTITIES.register("phase_liquid", () -> BlockEntityType.Builder.of(PhaseLiquidBlockEntity::new, AcademyBlocks.PHASE_LIQUID.get()).build(null));
    public static final RegistryObject<BlockEntityType<NodeBasicBlockEntity>> NODE_BASIC = BLOCK_ENTITIES.register("node_basic", () -> BlockEntityType.Builder.of(NodeBasicBlockEntity::new, AcademyBlocks.NODE_BASIC.get()).build(null));
}
