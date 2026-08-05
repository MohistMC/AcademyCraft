package com.mohistmc.academy.world.menu;

import com.mohistmc.academy.crafting.MetalFormerRecipes.Mode;
import com.mohistmc.academy.world.AcademyMenus;
import com.mohistmc.academy.world.block.entity.MetalFomerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 金属成型机菜单 —— 输入槽 + 输出槽 + 电池槽，通过 ContainerData 同步能量/进度/模式。
 */
public class MetalFomerMenu extends AcademyMenu {

    private final ContainerData data;

    public MetalFomerMenu(int windowId, Inventory inv, FriendlyByteBuf data) {
        super(AcademyMenus.METAL_FORMER_MENU.get(), windowId, inv, data, true);

        // 服务端从方块实体动态读取，客户端由 SimpleContainerData 同步
        if (inv.player.level().isClientSide) {
            this.data = new SimpleContainerData(3);
        } else if (inv.player.level().getBlockEntity(pos) instanceof MetalFomerBlockEntity be) {
            this.data = new ContainerData() {
                @Override
                public int get(int index) {
                    return switch (index) {
                        case 0 -> (int) be.getEnergy();
                        case 1 -> (int) (be.getWorkProgress() * 1000);
                        default -> be.getMode().ordinal();
                    };
                }

                @Override
                public void set(int index, int value) {}

                @Override
                public int getCount() {
                    return 3;
                }
            };
        } else {
            this.data = new SimpleContainerData(3);
        }
        addDataSlots(this.data);

        addAcademySlot(new Slot(container, MetalFomerBlockEntity.SLOT_IN, 15, 49) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return MetalFomerBlockEntity.isSlotValid(inv.player.level(), MetalFomerBlockEntity.SLOT_IN, item);
            }
        });

        addAcademySlot(new Slot(container, MetalFomerBlockEntity.SLOT_OUT, 145, 49) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return false;
            }
        });

        addAcademySlot(new Slot(container, MetalFomerBlockEntity.SLOT_BATTERY, 44, 80) {
            @Override
            public boolean mayPlace(ItemStack item) {
                return MetalFomerBlockEntity.isSlotValid(inv.player.level(), MetalFomerBlockEntity.SLOT_BATTERY, item);
            }
        });
    }

    public int getEnergy() {
        return data.get(0);
    }

    public double getProgress() {
        return data.get(1) / 1000.0;
    }

    public Mode getMode() {
        return Mode.byOrdinal(data.get(2));
    }

}
