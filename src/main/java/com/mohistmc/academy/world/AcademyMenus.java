package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.menu.DevNormalMenu;
import com.mohistmc.academy.world.menu.NodeBasicMenu;
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
    public static final Supplier<MenuType<DevNormalMenu>> DEV_NORMAL_MENU = MENUS.register("dev_normal_menu", () -> IMenuTypeExtension.create(DevNormalMenu::new));
}
