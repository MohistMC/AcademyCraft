package com.mohistmc.academy.world.container;

import com.mohistmc.academy.client.block.entity.WindGenBaseBlockEntity;
import com.mohistmc.academy.world.menu.WindBaseMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class WindGenBaseContainer implements Container, StackedContentsCompatible {
    private final WindBaseMenu menu;
    private ItemStack item = null;

    public WindGenBaseContainer(WindBaseMenu menu) {
        this.menu = menu;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return item == null;
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return isEmpty() ? ItemStack.EMPTY : item;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
        ItemStack back = item;
        item = null;
        return back;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        ItemStack back = item;
        item = null;
        return back;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_) {
        item = p_18945_;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return true;
    }

    @Override
    public void clearContent() {

    }

    @Override
    public void fillStackedContents(StackedContents p_40281_) {
        if (item != null) {
            p_40281_.accountSimpleStack(item);
        }
    }
}
