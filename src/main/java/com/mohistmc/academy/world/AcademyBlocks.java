package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 方块
 *
 * @author lliiooll
 */
public class AcademyBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AcademyCraft.MODID);

    public static final RegistryObject<Block> CAT_ENGINE = BLOCKS.register("cat_engine", CatEngine::new);
    public static final RegistryObject<Block> ABILITY_INTERFERER = BLOCKS.register("ability_interferer", AbilityInterferer::new);
    public static final RegistryObject<Block> CONSTRAIN_METAL = BLOCKS.register("constraint_metal", ConstraintMetal::new);
    public static final RegistryObject<Block> CRYSTAL_ORE = BLOCKS.register("crystal_ore", CrystalOre::new);
    public static final RegistryObject<Block> RESO_ORE = BLOCKS.register("reso_ore", ResoOre::new);
    public static final RegistryObject<Block> IMAGSIL_ORE = BLOCKS.register("imagsil_ore", ImagsilOre::new);
    public static final RegistryObject<Block> IMAG_FUSOR = BLOCKS.register("imag_fusor", ImagFusor::new);
    public static final RegistryObject<Block> MACHINE_FRAME = BLOCKS.register("machine_frame", MachineFrame::new);
    public static final RegistryObject<Block> METAL_FORMER = BLOCKS.register("metal_former", MetalFomer::new);
    public static final RegistryObject<Block> NODE_BASIC = BLOCKS.register("node_basic", NodeBasic::new);
    public static final RegistryObject<Block> NODE_ADVANCED = BLOCKS.register("node_advanced", NodeAdvanced::new);
    public static final RegistryObject<Block> NODE_STANDARD = BLOCKS.register("node_standard", NodeStandard::new);
    public static final RegistryObject<Block> DEV_NORMAL = BLOCKS.register("dev_normal", DevNormal::new);
    public static final RegistryObject<Block> DEV_ADVANCED = BLOCKS.register("dev_advanced", DevAdvanced::new);
    public static final RegistryObject<Block> DEV_NORMAL_SUB = BLOCKS.register("dev_normal_sub", DevNormalSubBlock::new);
    public static final RegistryObject<Block> DEV_ADVANCED_SUB = BLOCKS.register("dev_advanced_sub", DevAdvancedSubBlock::new);
    public static final RegistryObject<Block> MATRIX = BLOCKS.register("matrix", Matrix::new);
    public static final RegistryObject<Block> MATRIX_SUB = BLOCKS.register("matrix_sub", MatrixSubBlock::new);
    public static final RegistryObject<Block> PHASE_GEN = BLOCKS.register("phase_gen", PhaseGen::new);
    public static final RegistryObject<Block> SOLAR_GEN = BLOCKS.register("solar_gen", SolarGen::new);
    public static final RegistryObject<Block> WINDGEN_BASE = BLOCKS.register("windgen_base", WindGenBase::new);
    public static final RegistryObject<Block> WINDGEN_MAIN = BLOCKS.register("windgen_main", WindGenMain::new);
    public static final RegistryObject<Block> WINDGEN_PILLAR = BLOCKS.register("windgen_pillar", WindGenPillar::new);
    public static final RegistryObject<Block> WINDGEN_FAN = BLOCKS.register("windgen_fan_block", WindGenFan::new);

}
