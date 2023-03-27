package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.item.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 物品
 *
 * @author lliiooll
 */
public class AcademyItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AcademyCraft.MODID);

    public static final RegistryObject<Item> CAT_ENGINE = ITEMS.register("cat_engine", () -> new BlockItem(AcademyBlocks.CAT_ENGINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ABILITY_INTERFERER = ITEMS.register("ability_interferer", () -> new BlockItem(AcademyBlocks.ABILITY_INTERFERER.get(), new Item.Properties()));
    public static final RegistryObject<Item> CONSTRAIN_METAL = ITEMS.register("constraint_metal", () -> new BlockItem(AcademyBlocks.CONSTRAIN_METAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL_ORE = ITEMS.register("crystal_ore", () -> new BlockItem(AcademyBlocks.CRYSTAL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RESO_ORE = ITEMS.register("reso_ore", () -> new BlockItem(AcademyBlocks.RESO_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> IMAGSIL_ORE = ITEMS.register("imagsil_ore", () -> new BlockItem(AcademyBlocks.IMAGSIL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> IMAG_FUSOR = ITEMS.register("imag_fusor", () -> new BlockItem(AcademyBlocks.IMAG_FUSOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_FRAME = ITEMS.register("machine_frame", () -> new BlockItem(AcademyBlocks.MACHINE_FRAME.get(), new Item.Properties()));
    public static final RegistryObject<Item> METAL_FORMER = ITEMS.register("metal_former", () -> new BlockItem(AcademyBlocks.METAL_FORMER.get(), new Item.Properties()));
    public static final RegistryObject<Item> NODE_BASIC = ITEMS.register("node_basic", () -> new BlockItem(AcademyBlocks.NODE_BASIC.get(), new Item.Properties()));
    public static final RegistryObject<Item> NODE_ADVANCED = ITEMS.register("node_advanced", () -> new BlockItem(AcademyBlocks.NODE_ADVANCED.get(), new Item.Properties()));
    public static final RegistryObject<Item> NODE_STANDARD = ITEMS.register("node_standard", () -> new BlockItem(AcademyBlocks.NODE_STANDARD.get(), new Item.Properties()));
    public static final RegistryObject<Item> DEV_NORMAL = ITEMS.register("dev_normal", () -> new BlockItem(AcademyBlocks.DEV_NORMAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> DEV_ADVANCED = ITEMS.register("dev_advanced", () -> new BlockItem(AcademyBlocks.DEV_ADVANCED.get(), new Item.Properties()));
    public static final RegistryObject<Item> DEV_NORMAL_SUB = ITEMS.register("dev_normal_sub", () -> new BlockItem(AcademyBlocks.DEV_NORMAL_SUB.get(), new Item.Properties()));
    public static final RegistryObject<Item> DEV_ADVANCED_SUB = ITEMS.register("dev_advanced_sub", () -> new BlockItem(AcademyBlocks.DEV_NORMAL_SUB.get(), new Item.Properties()));
    public static final RegistryObject<Item> MATRIX = ITEMS.register("matrix", () -> new BlockItem(AcademyBlocks.MATRIX.get(), new Item.Properties()));
    public static final RegistryObject<Item> MATRIX_SUB = ITEMS.register("matrix_sub", () -> new BlockItem(AcademyBlocks.MATRIX_SUB.get(), new Item.Properties()));
    public static final RegistryObject<Item> PHASE_GEN = ITEMS.register("phase_gen", () -> new BlockItem(AcademyBlocks.PHASE_GEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> TUTORIAL = ITEMS.register("tutorial", Tutorial::new);
    public static final RegistryObject<Item> LOGO = ITEMS.register("logo", Logo::new);
    public static final RegistryObject<Item> CRYSTAL_LOW = ITEMS.register("crystal_low", CrystalLow::new);
    public static final RegistryObject<Item> CRYSTAL_NORMAL = ITEMS.register("crystal_normal", CrystalNormal::new);
    public static final RegistryObject<Item> CRYSTAL_PURE = ITEMS.register("crystal_pure", CrystalPure::new);
    public static final RegistryObject<Item> RESO_CRYSTAL = ITEMS.register("reso_crystal", ResoCrystal::new);




    public static final RegistryObject<Item> APP_FREQ_TRANSMITTER = ITEMS.register("app_freq_transmitter", AppFreqTransmitter::new);

    public static final RegistryObject<Item> APP_MEDIA_PLAYER = ITEMS.register("app_media_player", AppMediaPlayer::new);

    public static final RegistryObject<Item> APP_SETTINGS = ITEMS.register("app_settings", AppSettings::new);

    public static final RegistryObject<Item> APP_SKILL_TREE = ITEMS.register("app_skill_tree", AppSkillTree::new);

    public static final RegistryObject<Item> BRAIN_COMPONENT = ITEMS.register("brain_component", BrainComponent::new);

    public static final RegistryObject<Item> CALC_CHIP = ITEMS.register("calc_chip", CalcChip::new);

    public static final RegistryObject<Item> COIN = ITEMS.register("coin", Coin::new);

    public static final RegistryObject<Item> CONSTRAINT_INGOT = ITEMS.register("constraint_ingot", ConstraintIngot::new);

    public static final RegistryObject<Item> CONSTRAINT_PLATE = ITEMS.register("constraint_plate", ConstraintPlate::new);

    public static final RegistryObject<Item> DATA_CHIP = ITEMS.register("data_chip", DataChip::new);

    public static final RegistryObject<Item> DEVELOPER_PORTABLE = ITEMS.register("developer_portable", DeveloperPortable::new);

    public static final RegistryObject<Item> ENERGY_CONVERT_COMPONENT = ITEMS.register("energy_convert_component", EnergyConvertComponent::new);

    public static final RegistryObject<Item> ENERGY_UNIT = ITEMS.register("energy_unit", EnergyUnit::new);

    public static final RegistryObject<Item> FACTOR_ELECTROMASTER = ITEMS.register("factor_electromaster", FactorElectromaster::new);

    public static final RegistryObject<Item> FACTOR_MELTDOWNER = ITEMS.register("factor_meltdowner", FactorMeltdowner::new);

    public static final RegistryObject<Item> FACTOR_TELEPORTER = ITEMS.register("factor_teleporter", FactorTeleporter::new);

    public static final RegistryObject<Item> FACTOR_VECMANIP = ITEMS.register("factor_vecmanip", FactorVecmanip::new);

    public static final RegistryObject<Item> INFO_COMPONENT = ITEMS.register("info_component", InfoComponent::new);

    public static final RegistryObject<Item> MAGNETIC_COIL = ITEMS.register("magnetic_coil", MagneticCoil::new);

    public static final RegistryObject<Item> MAG_HOOK = ITEMS.register("mag_hook", MagHook::new);

    public static final RegistryObject<Item> MATTER_UNIT = ITEMS.register("matter_unit", MatterUnit::new);

    public static final RegistryObject<Item> MAT_CORE_0 = ITEMS.register("mat_core_0", MatCore0::new);

    public static final RegistryObject<Item> MAT_CORE_1 = ITEMS.register("mat_core_1", MatCore1::new);

    public static final RegistryObject<Item> MAT_CORE_2 = ITEMS.register("mat_core_2", MatCore2::new);

    public static final RegistryObject<Item> MEDIA_LEVEL5_JUDGELIGHT = ITEMS.register("media_level5_judgelight", MediaLevel5Judgelight::new);

    public static final RegistryObject<Item> MEDIA_ONLY_MY_RAILGUN = ITEMS.register("media_only_my_railgun", MediaOnlyMyRailgun::new);

    public static final RegistryObject<Item> MEDIA_SISTERS_NOISE = ITEMS.register("media_sisters_noise", MediaSistersNoise::new);

    public static final RegistryObject<Item> NEEDLE = ITEMS.register("needle", Needle::new);

    public static final RegistryObject<Item> REINFORCED_IRON_PLATE = ITEMS.register("reinforced_iron_plate", ReinforcedIronPlate::new);

    public static final RegistryObject<Item> RESONANCE_COMPONENT = ITEMS.register("resonance_component", ResonanceComponent::new);

    public static final RegistryObject<Item> SILBARN = ITEMS.register("silbarn", Silbarn::new);

    public static final RegistryObject<Item> TERMINAL_INSTALLER = ITEMS.register("terminal_installer", TerminalInstaller::new);

    public static final RegistryObject<Item> WAFER = ITEMS.register("wafer", Wafer::new);

    public static final RegistryObject<Item> WINDGEN_FAN = ITEMS.register("windgen_fan", WindgenFan::new);

}
