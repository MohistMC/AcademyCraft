package com.mohistmc.academy.skill.ability.telekinesis;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 念力传输 —— 用念力恢复自身CP并减少过载
 */
public class PsychoTransmissionEffect implements SkillEffect {

    @Override
    public String getId() {
        return "psycho_transmission";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float cpRestore = lerpf(50.0f, 150.0f, exp);
        float overloadReduce = lerpf(30.0f, 80.0f, exp);

        ServerLevel level = player.serverLevel();

        EffectHelper.psychoBurst(level, player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(), 20, 0.5);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.8f, 1.5f);

        // 恢复CP并减少过载
        data.setCurrentCp(data.getCurrentCp() + cpRestore);
        data.setCurrentOverload(data.getCurrentOverload() - overloadReduce);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
