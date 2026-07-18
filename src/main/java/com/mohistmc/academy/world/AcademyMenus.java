package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.menu.DevAdvancedMenu;
import com.mohistmc.academy.world.menu.DevNormalMenu;
import com.mohistmc.academy.world.menu.ImagFusorMenu;
import com.mohistmc.academy.world.menu.MatrixMenu;
import com.mohistmc.academy.world.menu.NodeAdvancedMenu;
import com.mohistmc.academy.world.menu.NodeBasicMenu;
import com.mohistmc.academy.world.menu.NodeStandardMenu;
import com.mohistmc.academy.world.menu.PhaseGenMenu;
import com.mohistmc.academy.world.menu.SolarGenMenu;
import com.mohistmc.academy.world.menu.WindGenBaseMenu;
import com.mohistmc.academy.world.menu.WindGenMainMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;


public class AcademyMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, AcademyCraft.MODID);

    public static final Supplier<MenuType<WindGenBaseMenu>> WIND_BASE_MENU = MENUS.register("wind_base_menu", () -> IMenuTypeExtension.create(WindGenBaseMenu::new));
    public static final Supplier<MenuType<WindGenMainMenu>> WIND_MAIN_MENU = MENUS.register("wind_main_menu", () -> IMenuTypeExtension.create(WindGenMainMenu::new));
    public static final Supplier<MenuType<NodeBasicMenu>> NODE_BASIC = MENUS.register("node_basic_menu", () -> IMenuTypeExtension.create(NodeBasicMenu::new));
    public static final Supplier<MenuType<NodeStandardMenu>> NODE_STANDARD_MENU = MENUS.register("node_standard_menu", () -> IMenuTypeExtension.create(NodeStandardMenu::new));
    public static final Supplier<MenuType<NodeAdvancedMenu>> NODE_ADVANCED_MENU = MENUS.register("node_advanced_menu", () -> IMenuTypeExtension.create(NodeAdvancedMenu::new));
    public static final Supplier<MenuType<DevNormalMenu>> DEV_NORMAL_MENU = MENUS.register("dev_normal_menu", () -> IMenuTypeExtension.create(DevNormalMenu::new));
    public static final Supplier<MenuType<DevAdvancedMenu>> DEV_ADVANCED_MENU = MENUS.register("dev_advanced_menu", () -> IMenuTypeExtension.create(DevAdvancedMenu::new));
    public static final Supplier<MenuType<ImagFusorMenu>> IMAG_FUSOR_MENU = MENUS.register("imag_fusor_menu", () -> IMenuTypeExtension.create(ImagFusorMenu::new));
    public static final Supplier<MenuType<SolarGenMenu>> SOLAR_GEN_MENU = MENUS.register("solar_gen_menu", () -> IMenuTypeExtension.create(SolarGenMenu::new));
    public static final Supplier<MenuType<PhaseGenMenu>> PHASE_GEN_MENU = MENUS.register("phase_gen_menu", () -> IMenuTypeExtension.create(PhaseGenMenu::new));
    public static final Supplier<MenuType<MatrixMenu>> MATRIX_MENU = MENUS.register("matrix_menu", () -> IMenuTypeExtension.create(MatrixMenu::new));

}
