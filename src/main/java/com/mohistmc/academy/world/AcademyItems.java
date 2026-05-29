package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.block.DevAdvancedSubBlock;
import com.mohistmc.academy.world.block.DevNormalSubBlock;
import com.mohistmc.academy.world.block.MatrixSubBlock;
import com.mohistmc.academy.world.block.WindGenBaseSubBlock;
import com.mohistmc.academy.world.block.WindGenFan;
import com.mohistmc.academy.world.item.AppFreqTransmitter;
import com.mohistmc.academy.world.item.AppMediaPlayer;
import com.mohistmc.academy.world.item.AppSettings;
import com.mohistmc.academy.world.item.AppSkillTree;
import com.mohistmc.academy.world.item.BrainComponent;
import com.mohistmc.academy.world.item.CalcChip;
import com.mohistmc.academy.world.item.Coin;
import com.mohistmc.academy.world.item.ConstraintIngot;
import com.mohistmc.academy.world.item.ConstraintPlate;
import com.mohistmc.academy.world.item.CrystalLow;
import com.mohistmc.academy.world.item.CrystalNormal;
import com.mohistmc.academy.world.item.CrystalPure;
import com.mohistmc.academy.world.item.DataChip;
import com.mohistmc.academy.world.item.DeveloperPortable;
import com.mohistmc.academy.world.item.EnergyConvertComponent;
import com.mohistmc.academy.world.item.EnergyUnit;
import com.mohistmc.academy.world.item.FactorElectromaster;
import com.mohistmc.academy.world.item.FactorMeltdowner;
import com.mohistmc.academy.world.item.FactorTeleporter;
import com.mohistmc.academy.world.item.FactorVecmanip;
import com.mohistmc.academy.world.item.InfoComponent;
import com.mohistmc.academy.world.item.Logo;
import com.mohistmc.academy.world.item.MagHook;
import com.mohistmc.academy.world.item.MagneticCoil;
import com.mohistmc.academy.world.item.MatCore0;
import com.mohistmc.academy.world.item.MatCore1;
import com.mohistmc.academy.world.item.MatCore2;
import com.mohistmc.academy.world.item.MatterUnit;
import com.mohistmc.academy.world.item.MediaLevel5Judgelight;
import com.mohistmc.academy.world.item.MediaOnlyMyRailgun;
import com.mohistmc.academy.world.item.MediaSistersNoise;
import com.mohistmc.academy.world.item.Needle;
import com.mohistmc.academy.world.item.ReinforcedIronPlate;
import com.mohistmc.academy.world.item.ResoCrystal;
import com.mohistmc.academy.world.item.ResonanceComponent;
import com.mohistmc.academy.world.item.Silbarn;
import com.mohistmc.academy.world.item.TerminalInstaller;
import com.mohistmc.academy.world.item.Tutorial;
import com.mohistmc.academy.world.item.Wafer;
import com.mohistmc.academy.world.item.WindgenFan;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.registries.DeferredRegister;


public class AcademyItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, AcademyCraft.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AcademyCraft.MODID);

    public static final Supplier<Item> CAT_ENGINE = ITEMS.register("cat_engine", () -> new BlockItem(AcademyBlocks.CAT_ENGINE.get(), new Item.Properties()));
    public static final Supplier<Item> ABILITY_INTERFERER = ITEMS.register("ability_interferer", () -> new BlockItem(AcademyBlocks.ABILITY_INTERFERER.get(), new Item.Properties()));
    public static final Supplier<Item> CONSTRAIN_METAL = ITEMS.register("constraint_metal", () -> new BlockItem(AcademyBlocks.CONSTRAIN_METAL.get(), new Item.Properties()));
    public static final Supplier<Item> CRYSTAL_ORE = ITEMS.register("crystal_ore", () -> new BlockItem(AcademyBlocks.CRYSTAL_ORE.get(), new Item.Properties()));
    public static final Supplier<Item> RESO_ORE = ITEMS.register("reso_ore", () -> new BlockItem(AcademyBlocks.RESO_ORE.get(), new Item.Properties()));
    public static final Supplier<Item> IMAGSIL_ORE = ITEMS.register("imagsil_ore", () -> new BlockItem(AcademyBlocks.IMAGSIL_ORE.get(), new Item.Properties()));
    public static final Supplier<Item> IMAG_FUSOR = ITEMS.register("imag_fusor", () -> new BlockItem(AcademyBlocks.IMAG_FUSOR.get(), new Item.Properties()));
    public static final Supplier<Item> MACHINE_FRAME = ITEMS.register("machine_frame", () -> new BlockItem(AcademyBlocks.MACHINE_FRAME.get(), new Item.Properties()));
    public static final Supplier<Item> METAL_FORMER = ITEMS.register("metal_former", () -> new BlockItem(AcademyBlocks.METAL_FORMER.get(), new Item.Properties()));
    public static final Supplier<Item> NODE_BASIC = ITEMS.register("node_basic", () -> new BlockItem(AcademyBlocks.NODE_BASIC.get(), new Item.Properties()));
    public static final Supplier<Item> NODE_ADVANCED = ITEMS.register("node_advanced", () -> new BlockItem(AcademyBlocks.NODE_ADVANCED.get(), new Item.Properties()));
    public static final Supplier<Item> NODE_STANDARD = ITEMS.register("node_standard", () -> new BlockItem(AcademyBlocks.NODE_STANDARD.get(), new Item.Properties()));
    public static final Supplier<Item> DEV_NORMAL = ITEMS.register("dev_normal", () -> new BlockItem(AcademyBlocks.DEV_NORMAL.get(), new Item.Properties()));
    public static final Supplier<Item> DEV_ADVANCED = ITEMS.register("dev_advanced", () -> new BlockItem(AcademyBlocks.DEV_ADVANCED.get(), new Item.Properties()));
    public static final Supplier<Item> DEV_NORMAL_SUB = ITEMS.register("dev_normal_sub", () -> new BlockItem(AcademyBlocks.DEV_NORMAL_SUB.get(), new Item.Properties()));
    public static final Supplier<Item> DEV_ADVANCED_SUB = ITEMS.register("dev_advanced_sub", () -> new BlockItem(AcademyBlocks.DEV_NORMAL_SUB.get(), new Item.Properties()));
    public static final Supplier<Item> MATRIX = ITEMS.register("matrix", () -> new BlockItem(AcademyBlocks.MATRIX.get(), new Item.Properties()));
    public static final Supplier<Item> MATRIX_SUB = ITEMS.register("matrix_sub", () -> new BlockItem(AcademyBlocks.MATRIX_SUB.get(), new Item.Properties()));
    public static final Supplier<Item> PHASE_GEN = ITEMS.register("phase_gen", () -> new BlockItem(AcademyBlocks.PHASE_GEN.get(), new Item.Properties()));
    public static final Supplier<Item> SOLAR_GEN = ITEMS.register("solar_gen", () -> new BlockItem(AcademyBlocks.SOLAR_GEN.get(), new Item.Properties()));
    public static final Supplier<Item> WINDGEN_BASE = ITEMS.register("windgen_base", () -> new BlockItem(AcademyBlocks.WINDGEN_BASE.get(), new Item.Properties()));
    public static final Supplier<Item> WINDGEN_MAIN = ITEMS.register("windgen_main", () -> new BlockItem(AcademyBlocks.WINDGEN_MAIN.get(), new Item.Properties()));
    public static final Supplier<Item> WINDGEN_PILLAR = ITEMS.register("windgen_pillar", () -> new BlockItem(AcademyBlocks.WINDGEN_PILLAR.get(), new Item.Properties()));
    public static final Supplier<Item> WINDGEN_FAN_BLOCK = ITEMS.register("windgen_fan_block", () -> new BlockItem(AcademyBlocks.WINDGEN_FAN.get(), new Item.Properties()));
    public static final Supplier<Item> TUTORIAL = ITEMS.register("tutorial", Tutorial::new);
    public static final Supplier<Item> LOGO = ITEMS.register("logo", Logo::new);
    public static final Supplier<Item> CRYSTAL_LOW = ITEMS.register("crystal_low", CrystalLow::new);
    public static final Supplier<Item> CRYSTAL_NORMAL = ITEMS.register("crystal_normal", CrystalNormal::new);
    public static final Supplier<Item> CRYSTAL_PURE = ITEMS.register("crystal_pure", CrystalPure::new);
    public static final Supplier<Item> RESO_CRYSTAL = ITEMS.register("reso_crystal", ResoCrystal::new);
    public static final Supplier<Item> PHASE_BUCKET = ITEMS.register("imag_phase", () -> new BucketItem(AcademyFluids.PHASE_LIQUID.get(), (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));


    public static final Supplier<Item> APP_FREQ_TRANSMITTER = ITEMS.register("app_freq_transmitter", AppFreqTransmitter::new);

    public static final Supplier<Item> APP_MEDIA_PLAYER = ITEMS.register("app_media_player", AppMediaPlayer::new);

    public static final Supplier<Item> APP_SETTINGS = ITEMS.register("app_settings", AppSettings::new);

    public static final Supplier<Item> APP_SKILL_TREE = ITEMS.register("app_skill_tree", AppSkillTree::new);

    public static final Supplier<Item> BRAIN_COMPONENT = ITEMS.register("brain_component", BrainComponent::new);

    public static final Supplier<Item> CALC_CHIP = ITEMS.register("calc_chip", CalcChip::new);

    public static final Supplier<Item> COIN = ITEMS.register("coin", Coin::new);

    public static final Supplier<Item> CONSTRAINT_INGOT = ITEMS.register("constraint_ingot", ConstraintIngot::new);

    public static final Supplier<Item> CONSTRAINT_PLATE = ITEMS.register("constraint_plate", ConstraintPlate::new);

    public static final Supplier<Item> DATA_CHIP = ITEMS.register("data_chip", DataChip::new);

    public static final Supplier<Item> DEVELOPER_PORTABLE = ITEMS.register("developer_portable", DeveloperPortable::new);

    public static final Supplier<Item> ENERGY_CONVERT_COMPONENT = ITEMS.register("energy_convert_component", EnergyConvertComponent::new);

    public static final Supplier<Item> ENERGY_UNIT = ITEMS.register("energy_unit", EnergyUnit::new);

    public static final Supplier<Item> FACTOR_ELECTROMASTER = ITEMS.register("factor_electromaster", FactorElectromaster::new);

    public static final Supplier<Item> FACTOR_MELTDOWNER = ITEMS.register("factor_meltdowner", FactorMeltdowner::new);

    public static final Supplier<Item> FACTOR_TELEPORTER = ITEMS.register("factor_teleporter", FactorTeleporter::new);

    public static final Supplier<Item> FACTOR_VECMANIP = ITEMS.register("factor_vecmanip", FactorVecmanip::new);

    public static final Supplier<Item> INFO_COMPONENT = ITEMS.register("info_component", InfoComponent::new);

    public static final Supplier<Item> MAGNETIC_COIL = ITEMS.register("magnetic_coil", MagneticCoil::new);

    public static final Supplier<Item> MAG_HOOK = ITEMS.register("mag_hook", MagHook::new);

    public static final Supplier<Item> MATTER_UNIT = ITEMS.register("matter_unit", MatterUnit::new);

    public static final Supplier<Item> MAT_CORE_0 = ITEMS.register("mat_core_0", MatCore0::new);

    public static final Supplier<Item> MAT_CORE_1 = ITEMS.register("mat_core_1", MatCore1::new);

    public static final Supplier<Item> MAT_CORE_2 = ITEMS.register("mat_core_2", MatCore2::new);

    public static final Supplier<Item> MEDIA_LEVEL5_JUDGELIGHT = ITEMS.register("media_level5_judgelight", MediaLevel5Judgelight::new);

    public static final Supplier<Item> MEDIA_ONLY_MY_RAILGUN = ITEMS.register("media_only_my_railgun", MediaOnlyMyRailgun::new);

    public static final Supplier<Item> MEDIA_SISTERS_NOISE = ITEMS.register("media_sisters_noise", MediaSistersNoise::new);

    public static final Supplier<Item> NEEDLE = ITEMS.register("needle", Needle::new);

    public static final Supplier<Item> REINFORCED_IRON_PLATE = ITEMS.register("reinforced_iron_plate", ReinforcedIronPlate::new);

    public static final Supplier<Item> RESONANCE_COMPONENT = ITEMS.register("resonance_component", ResonanceComponent::new);

    public static final Supplier<Item> SILBARN = ITEMS.register("silbarn", Silbarn::new);

    public static final Supplier<Item> TERMINAL_INSTALLER = ITEMS.register("terminal_installer", TerminalInstaller::new);

    public static final Supplier<Item> WAFER = ITEMS.register("wafer", Wafer::new);

    public static final Supplier<Item> WINDGEN_FAN = ITEMS.register("windgen_fan", WindgenFan::new);

    public static final Supplier<CreativeModeTab> EXAMPLE_TAB = TABS.register("academy_group", () -> CreativeModeTab.builder()
            // Set name of tab to display
            .title(Component.translatable("itemGroup." + AcademyCraft.MODID))
            // Set icon of creative tab
            .icon(() -> new ItemStack(AcademyItems.LOGO.get()))
            // Add default items to tab
            .displayItems((params, output) -> {
                AcademyItems.ITEMS.getEntries().stream().filter(item ->
                        !((item.get() instanceof Logo)
                                || (item.get() instanceof AppSettings)
                                || item.get().getDescriptionId().contains("dev_normal_sub")
                                || item.get().getDescriptionId().contains("dev_advanced_sub")
                                || item.get().getDescriptionId().contains("windgen_fan_block")
                                || item.get().getDescriptionId().contains("wingen_base_sub")
                                || item.get().getDescriptionId().contains("matrix_sub"))
                ).forEach(item -> {
                    if (item.get() == ENERGY_UNIT.get()) {
                        ItemStack empty_energy_unit = ENERGY_UNIT.get().getDefaultInstance();
                        empty_energy_unit.setDamageValue(EnergyUnit.maxEnergy);
                        output.accept(empty_energy_unit);
                    }
                    if (item.get() == DEVELOPER_PORTABLE.get()) {
                        ItemStack empty_developer_portable = DEVELOPER_PORTABLE.get().getDefaultInstance();
                        empty_developer_portable.setDamageValue(DeveloperPortable.maxEnergy);
                        output.accept(empty_developer_portable);
                    }
                    output.accept(item.get());
                });
                AcademyBlocks.BLOCKS.getEntries().stream().filter(block ->
                        !(block.get() instanceof DevNormalSubBlock)
                                && !(block.get() instanceof DevAdvancedSubBlock)
                                && !(block.get() instanceof MatrixSubBlock)
                                && !(block.get() instanceof WindGenBaseSubBlock)
                                && !(block.get() instanceof WindGenFan)
                                && !(block.get() instanceof LiquidBlock)
                ).forEach(block -> output.accept(block.get()));
            })
            .build()
    );

}
