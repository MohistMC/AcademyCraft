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
    public static final RegistryObject<Item> TUTORIAL = ITEMS.register("tutorial", Tutorial::new);
    public static final RegistryObject<Item> LOGO = ITEMS.register("logo", Logo::new);
    public static final RegistryObject<Item> CRYSTAL_LOW = ITEMS.register("crystal_low", CrystalLow::new);
    public static final RegistryObject<Item> CRYSTAL_NORMAL = ITEMS.register("crystal_normal", CrystalNormal::new);
    public static final RegistryObject<Item> CRYSTAL_PURE = ITEMS.register("crystal_pure", CrystalPure::new);
    public static final RegistryObject<Item> RESO_CRYSTAL = ITEMS.register("reso_crystal", ResoCrystal::new);


}
