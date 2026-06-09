package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class ChargingEffect implements SkillEffect {

    @Override
    public String getId() {
        return "charging";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        ServerLevel level = player.serverLevel();
        float proficiency = data.getProficiency(getId());
        int chargeAmount = (int) (100 + proficiency * 200);

        // 先尝试给手持物品充能
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && EnergyItemHelper.isEnergyItem(held)) {
            int received = EnergyItemHelper.receiveEnergy(held, chargeAmount, false);
            if (received > 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
                return;
            }
        }

        // 尝试给准星对准的方块充能
        BlockHitResult hit = (BlockHitResult) player.pick(5.0, 0, false);
        if (hit != null) {
            BlockPos pos = hit.getBlockPos();
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IFEnergyStorage storage) {
                int received = storage.receiveEnergy(chargeAmount, false);
                if (received > 0) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
                }
            }
        }
    }
}
