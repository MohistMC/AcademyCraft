package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.menu.NodeBasicMenu;
import com.mohistmc.academy.world.menu.WindGenBaseMenu;
import com.mohistmc.academy.world.menu.WindGenMainMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AcademyMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AcademyCraft.MODID);

    public static final RegistryObject<MenuType<WindGenBaseMenu>> WIND_BASE_MENU = MENUS.register("wind_base_menu", () -> IForgeMenuType.create(WindGenBaseMenu::new));
    public static final RegistryObject<MenuType<WindGenMainMenu>> WIND_MAIN_MENU = MENUS.register("wind_main_menu", () -> IForgeMenuType.create(WindGenMainMenu::new));
    public static final RegistryObject<MenuType<NodeBasicMenu>> NODE_BASIC = MENUS.register("node_basic_menu", () -> IForgeMenuType.create(NodeBasicMenu::new));
}
