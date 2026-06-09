package com.mohistmc.academy.crafting;

import com.mohistmc.academy.crafting.MetalFormerRecipes.Mode;
import com.mohistmc.academy.world.AcademyItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * 配方初始化 —— 注册所有 ImagFusor 和 MetalFormer 配方。
 * 在 FMLCommonSetupEvent 中调用 init()。
 * <p>
 * 注意 1.21.1 无 OreDictionary，改用 Tags 做 ore refine recipes。
 *
 * @author WeAthFolD (original), Mgazul (port)
 */
public final class MFIFRecipes {

    private MFIFRecipes() {}

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        // ==================== ImagFusor Recipes ====================
        ImagFusorRecipes ifr = ImagFusorRecipes.INSTANCE;
        ifr.add(new ItemStack(AcademyItems.CRYSTAL_LOW.get()), 3000,
                new ItemStack(AcademyItems.CRYSTAL_NORMAL.get()));
        ifr.add(new ItemStack(AcademyItems.CRYSTAL_NORMAL.get()), 8000,
                new ItemStack(AcademyItems.CRYSTAL_PURE.get()));

        // ==================== MetalFormer Recipes ====================
        MetalFormerRecipes mfr = MetalFormerRecipes.INSTANCE;

        // INCISE — 切割
        mfr.add(new ItemStack(AcademyItems.CONSTRAINT_INGOT.get()),
                new ItemStack(AcademyItems.WAFER.get(), 2), Mode.INCISE);
        mfr.add(new ItemStack(AcademyItems.WAFER.get()),
                new ItemStack(AcademyItems.NEEDLE.get(), 4), Mode.INCISE);

        // ETCH — 蚀刻
        mfr.add(new ItemStack(AcademyItems.DATA_CHIP.get()),
                new ItemStack(AcademyItems.CALC_CHIP.get()), Mode.ETCH);

        // PLATE — 压板
        mfr.add(new ItemStack(Items.IRON_INGOT),
                new ItemStack(AcademyItems.REINFORCED_IRON_PLATE.get()), Mode.PLATE);
        mfr.add(new ItemStack(AcademyItems.CONSTRAINT_INGOT.get()),
                new ItemStack(AcademyItems.CONSTRAINT_PLATE.get()), Mode.PLATE);

        // REFINE — 精炼倍产
        mfr.add(new ItemStack(AcademyItems.IMAGSIL_ORE.get()),
                new ItemStack(AcademyItems.CONSTRAINT_INGOT.get(), 4), Mode.REFINE);
        mfr.add(new ItemStack(AcademyItems.CONSTRAIN_METAL.get()),
                new ItemStack(AcademyItems.CONSTRAINT_INGOT.get(), 2), Mode.REFINE);
        mfr.add(new ItemStack(AcademyItems.RESO_ORE.get()),
                new ItemStack(AcademyItems.RESO_CRYSTAL.get(), 3), Mode.REFINE);
        mfr.add(new ItemStack(AcademyItems.CRYSTAL_ORE.get()),
                new ItemStack(AcademyItems.CRYSTAL_LOW.get(), 4), Mode.REFINE);

        // Vanilla ore refine (2x)
        mfr.add(new ItemStack(Items.GOLD_ORE), new ItemStack(Items.GOLD_INGOT, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.IRON_ORE), new ItemStack(Items.IRON_INGOT, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.DIAMOND_ORE), new ItemStack(Items.DIAMOND, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.EMERALD_ORE), new ItemStack(Items.EMERALD, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.REDSTONE_ORE), new ItemStack(Items.REDSTONE, 12), Mode.REFINE);
        mfr.add(new ItemStack(Items.LAPIS_ORE), new ItemStack(Items.LAPIS_LAZULI, 12), Mode.REFINE);
        mfr.add(new ItemStack(Items.COPPER_ORE), new ItemStack(Items.COPPER_INGOT, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.COAL_ORE), new ItemStack(Items.COAL, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.NETHER_QUARTZ_ORE), new ItemStack(Items.QUARTZ, 2), Mode.REFINE);

        // Deepslate ore refine
        mfr.add(new ItemStack(Items.DEEPSLATE_GOLD_ORE), new ItemStack(Items.GOLD_INGOT, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.DEEPSLATE_IRON_ORE), new ItemStack(Items.IRON_INGOT, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.DEEPSLATE_DIAMOND_ORE), new ItemStack(Items.DIAMOND, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.DEEPSLATE_EMERALD_ORE), new ItemStack(Items.EMERALD, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.DEEPSLATE_REDSTONE_ORE), new ItemStack(Items.REDSTONE, 12), Mode.REFINE);
        mfr.add(new ItemStack(Items.DEEPSLATE_LAPIS_ORE), new ItemStack(Items.LAPIS_LAZULI, 12), Mode.REFINE);
        mfr.add(new ItemStack(Items.DEEPSLATE_COPPER_ORE), new ItemStack(Items.COPPER_INGOT, 2), Mode.REFINE);
        mfr.add(new ItemStack(Items.DEEPSLATE_COAL_ORE), new ItemStack(Items.COAL, 2), Mode.REFINE);
    }
}
