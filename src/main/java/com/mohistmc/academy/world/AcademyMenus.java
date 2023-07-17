package com.mohistmc.academy.world;

import com.mohistmc.academy.AcademyCraft;
import com.mohistmc.academy.world.menu.AcademyMenu;
import com.mohistmc.academy.world.menu.WindGenBaseMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AcademyMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AcademyCraft.MODID);

    public static final RegistryObject<MenuType<WindGenBaseMenu>> WINDBASE_MENU = MENUS.register("windbase_menu", () -> IForgeMenuType.create(WindGenBaseMenu::new));
}
