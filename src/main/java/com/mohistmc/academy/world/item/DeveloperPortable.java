package com.mohistmc.academy.world.item;

import com.mohistmc.academy.capability.IEnergyItem;
import com.mohistmc.academy.network.LearnSkillPacket;
import com.mohistmc.academy.network.OpenDevGuiPacket;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class DeveloperPortable extends AcademyItem implements IEnergyItem {

    public static final int MAX_ENERGY = 10000;

    public DeveloperPortable() {
        super(new Properties().durability(MAX_ENERGY));
    }

    @Override
    public int getEnergyStored(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    @Override
    public int getMaxEnergyStored(ItemStack stack) {
        return MAX_ENERGY;
    }

    @Override
    public void setEnergy(ItemStack stack, int energy) {
        int newDamage = Math.max(0, getMaxEnergyStored(stack) - energy);
        stack.setDamageValue(newDamage);
    }

    @Override
    public int extractEnergy(ItemStack stack, int maxExtract, boolean simulate) {
        int energy = getEnergyStored(stack);
        int extracted = Math.min(energy, maxExtract);
        if (!simulate && extracted > 0) {
            setEnergy(stack, energy - extracted);
        }
        return extracted;
    }

    @Override
    public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
        int energy = getEnergyStored(stack);
        int maxEnergy = getMaxEnergyStored(stack);
        int received = Math.min(maxEnergy - energy, maxReceive);
        if (!simulate && received > 0) {
            setEnergy(stack, energy + received);
        }
        return received;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            LearnSkillPacket.syncToClient(serverPlayer);
            int energy = getEnergyStored(stack);
            PacketDistributor.sendToPlayer(serverPlayer, new OpenDevGuiPacket(0, energy, MAX_ENERGY, Optional.empty()));
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, Item.TooltipContext p_333372_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_333372_, p_41423_, p_41424_);
        int energy = getEnergyStored(p_41421_);
        p_41423_.add(Component.literal(energy + "/" + MAX_ENERGY + " IF"));
    }
}
