package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.block.AbilityInterferer;
import com.mohistmc.academy.world.block.CatEngine;
import com.mohistmc.academy.world.block.ConstraintMetal;
import com.mohistmc.academy.world.block.CrystalOre;
import com.mohistmc.academy.world.block.DevAdvanced;
import com.mohistmc.academy.world.block.DevAdvancedSubBlock;
import com.mohistmc.academy.world.block.DevNormal;
import com.mohistmc.academy.world.block.DevNormalSubBlock;
import com.mohistmc.academy.world.block.ImagFusor;
import com.mohistmc.academy.world.block.ImagsilOre;
import com.mohistmc.academy.world.block.MachineFrame;
import com.mohistmc.academy.world.block.Matrix;
import com.mohistmc.academy.world.block.MatrixSubBlock;
import com.mohistmc.academy.world.block.MetalFomer;
import com.mohistmc.academy.world.block.NodeAdvanced;
import com.mohistmc.academy.world.block.NodeBasic;
import com.mohistmc.academy.world.block.NodeStandard;
import com.mohistmc.academy.world.block.PhaseFluidBlock;
import com.mohistmc.academy.world.block.PhaseGen;
import com.mohistmc.academy.world.block.ResoOre;
import com.mohistmc.academy.world.block.SolarGen;
import com.mohistmc.academy.world.block.WindGenBase;
import com.mohistmc.academy.world.block.WindGenBaseSubBlock;
import com.mohistmc.academy.world.block.WindGenFan;
import com.mohistmc.academy.world.block.WindGenMain;
import com.mohistmc.academy.world.block.WindGenPillar;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AcademyBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AcademyCraft.MODID);

    public static final DeferredBlock<Block> CAT_ENGINE = BLOCKS.register("cat_engine", () -> new CatEngine(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(20.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> ABILITY_INTERFERER = BLOCKS.register("ability_interferer", AbilityInterferer::new);
    public static final DeferredBlock<Block> CONSTRAIN_METAL = BLOCKS.register("constraint_metal", ConstraintMetal::new);
    public static final DeferredBlock<Block> CRYSTAL_ORE = BLOCKS.register("crystal_ore", CrystalOre::new);
    public static final DeferredBlock<Block> RESO_ORE = BLOCKS.register("reso_ore", ResoOre::new);
    public static final DeferredBlock<Block> IMAGSIL_ORE = BLOCKS.register("imagsil_ore", ImagsilOre::new);
    public static final DeferredBlock<Block> IMAG_FUSOR = BLOCKS.register("imag_fusor", () -> new ImagFusor(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MACHINE_FRAME = BLOCKS.register("machine_frame", MachineFrame::new);
    public static final DeferredBlock<Block> METAL_FORMER = BLOCKS.register("metal_former", MetalFomer::new);
    public static final DeferredBlock<Block> NODE_BASIC = BLOCKS.register("node_basic", () -> new NodeBasic(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> NODE_STANDARD = BLOCKS.register("node_standard", () -> new NodeStandard(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> NODE_ADVANCED = BLOCKS.register("node_advanced", () -> new NodeAdvanced(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DEV_NORMAL = BLOCKS.register("dev_normal", () -> new DevNormal(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(5.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DEV_ADVANCED = BLOCKS.register("dev_advanced", () -> new DevAdvanced(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(5.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DEV_NORMAL_SUB = BLOCKS.register("dev_normal_sub", () -> new DevNormalSubBlock(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> DEV_ADVANCED_SUB = BLOCKS.register("dev_advanced_sub", () -> new DevAdvancedSubBlock(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> WIND_GEN_BASE_SUB = BLOCKS.register("windgen_base_sub", () -> new WindGenBaseSubBlock(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MATRIX = BLOCKS.register("matrix", () -> new Matrix(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(5.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> MATRIX_SUB = BLOCKS.register("matrix_sub", () -> new MatrixSubBlock(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> PHASE_GEN = BLOCKS.register("phase_gen", () -> new PhaseGen(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> SOLAR_GEN = BLOCKS.register("solar_gen", () -> new SolarGen(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> WINDGEN_BASE = BLOCKS.register("windgen_base", () -> new WindGenBase(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> WINDGEN_MAIN = BLOCKS.register("windgen_main", () -> new WindGenMain(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> WINDGEN_PILLAR = BLOCKS.register("windgen_pillar", () -> new WindGenPillar(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .strength(4.0f)
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> WINDGEN_FAN = BLOCKS.register("windgen_fan_block", () -> new WindGenFan(Properties.of()
            .sound(SoundType.STONE)
            .noOcclusion()
            .requiresCorrectToolForDrops()));
    public static final DeferredBlock<Block> PHASE_LIQUID = BLOCKS.register("phase_liquid", PhaseFluidBlock::new);

}
