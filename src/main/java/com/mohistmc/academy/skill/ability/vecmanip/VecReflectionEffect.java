package com.mohistmc.academy.skill.ability.vecmanip;

import com.mohistmc.academy.client.effect.EffectHelper;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademySounds;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/** 矢量反射 —— 激活后在一段时间内反射弹射物和攻击 */
public class VecReflectionEffect implements SkillEffect {

    @Override
    public String getId() {
        return "vec_reflection";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        float cp = lerpf(15, 11, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < cp) return;
            data.setCurrentCp(data.getCurrentCp() - cp);
        }

        int duration = (int) lerpf(60, 120, exp);
        int amplifier = (int) lerpf(1, 3, exp);

        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, amplifier));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration / 2, 1));

        ServerLevel level = player.serverLevel();
        Vec3 playerPos = player.position();
        double range = 4.0;

        List<Entity> nearby = level.getEntities(player,
                new AABB(playerPos.x - range, playerPos.y - range, playerPos.z - range,
                        playerPos.x + range, playerPos.y + range, playerPos.z + range),
                e -> e != player && e.isAlive() && e.isPickable());

        for (Entity e : nearby) {
            Vec3 delta = e.position().subtract(playerPos).normalize().scale(1.2);
            e.setDeltaMovement(delta.x, Math.abs(delta.y) * 0.3, delta.z);
            e.hurtMarked = true;

            if (e instanceof LivingEntity living) {
                float reflectDamage = lerpf(0.6f, 1.2f, exp) * 5;
                living.hurt(player.damageSources().playerAttack(player), reflectDamage);
            }
        }

        data.addProficiency(getId(), 0.001f);

        EffectHelper.glowBurst(level,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                30, 0.2f, 0x88FF88FF, 12, 1.5f);
        AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                AcademySounds.VM_VEC_REFLECTION, SoundSource.PLAYERS, 0.5f, 1.0f);
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(25, 12, proficiency);
    }
}
