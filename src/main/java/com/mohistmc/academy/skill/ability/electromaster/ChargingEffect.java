package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.capability.EnergyItemHelper;
import com.mohistmc.academy.capability.IFEnergyStorage;
import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.world.AcademySounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/** 电流回冲 —— 持续按住给方块/物品充能 */
public class ChargingEffect implements ChargingSkillEffect {

    private static final int MIN_TICKS = 1;
    private static final int MAX_TICKS = 100; // 最多持续 5 秒

    @Override
    public String getId() {
        return "charging";
    }

    @Override
    public int getMinChargeTicks() {
        return MIN_TICKS;
    }

    @Override
    public int getMaxChargeTicks() {
        return MAX_TICKS;
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float overload = lerpf(65, 48, exp);
        if (!data.isDevMode()) {
            data.addOverload(overload);
        }
        AcademySounds.playSound(player, AcademySounds.EM_CHARGE_LOOP, 0.3f, 1.0f);
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        float exp = data.getProficiency(getId());
        float consumption = lerpf(3, 7, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < consumption) {
                return false;
            }
            data.setCurrentCp(data.getCurrentCp() - consumption);
        }

        float chargeAmount = lerpf(15, 35, exp);

        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && EnergyItemHelper.isEnergyItem(held)) {
            EnergyItemHelper.receiveEnergy(held, (int) chargeAmount, false);
            return true;
        }

        BlockHitResult hit = (BlockHitResult) player.pick(15.0, 0, false);
        if (hit != null) {
            ServerLevel level = player.serverLevel();
            BlockPos pos = hit.getBlockPos();
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof IFEnergyStorage storage) {
                storage.receiveEnergy((int) chargeAmount, false);
            }
        }

        return true;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        if (!data.isDevMode()) {
            data.addProficiency(getId(), ticks * 0.0001f);
        }
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // 蓄力技能通过 Charging 接口执行
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(20, 8, proficiency);
    }
}
