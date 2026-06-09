package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.client.sound.AcademySounds;
import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 生物电强化 —— 蓄力后获得多种药水效果
 * <p>
 * 参考旧代码 BodyIntensify.scala：
 * - 蓄力时间越长，buff 强度越高，持续时间越久
 * - 概率性获取 buff（熟练度越高概率越大）
 * - 有饥饿 debuff
 *
 * @author Mgazul
 */
public class BodyIntensifyEffect implements ChargingSkillEffect {

    private static final int MIN_TICKS = 10;
    private static final int MAX_TICKS = 40;
    private static final int MAX_TOLERANT_TICKS = 100;

    private static final List<MobEffectInstance> BASE_EFFECTS = List.of(
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 0, 3),
            new MobEffectInstance(MobEffects.JUMP, 0, 1),
            new MobEffectInstance(MobEffects.REGENERATION, 0, 1),
            new MobEffectInstance(MobEffects.DAMAGE_BOOST, 0, 1),
            new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 0, 1)
    );

    @Override
    public String getId() {
        return "body_intensify";
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
        float overload = lerpf(200, 120, exp);
        if (!data.isDevMode()) {
            data.addOverload(overload);
        }
        // 播放强化循环音效
        AcademySounds.playSound(player, AcademySounds.EM_INTENSIFY_LOOP, 0.5f, 1.0f);
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        float exp = data.getProficiency(getId());
        float consumption = lerpf(20, 15, exp);

        if (!data.isDevMode()) {
            if (data.getCurrentCp() < consumption) {
                return false;
            }
            data.setCurrentCp(data.getCurrentCp() - consumption);
        }

        // 到达最大容忍时间，自动释放
        if (ticks >= MAX_TOLERANT_TICKS) {
            return false;
        }

        // MAX_TICKS 后自动释放
        return ticks < MAX_TICKS;
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        if (ticks < MIN_TICKS) {
            return; // 蓄力不足
        }

        float exp = data.getProficiency(getId());
        int effectiveTicks = Math.min(ticks, MAX_TICKS);

        // 计算概率和 buff 时长
        double probability = (effectiveTicks - 10.0) / 18.0;
        int buffTime = (int) ((1 + (int) (Math.random() * 2)) * effectiveTicks * lerpf(1.5f, 2.5f, exp));
        int buffLevel = (int) Math.floor(probability);

        // 随机排列效果列表
        List<MobEffectInstance> shuffled = new ArrayList<>(BASE_EFFECTS);
        Collections.shuffle(shuffled);

        // 概率性应用 buff
        double p = probability;
        int idx = 0;
        while (p > 0 && idx < shuffled.size()) {
            if (Math.random() < p) {
                MobEffectInstance template = shuffled.get(idx);
                int level = Math.min(buffLevel, template.getAmplifier());
                int duration = buffTime;
                player.addEffect(new MobEffectInstance(
                        template.getEffect(), duration, level,
                        template.isAmbient(), true, true
                ));
            }
            p -= 1.0;
            idx++;
        }

        // 饥饿 debuff（副作用）
        int hungerTime = (int) (1.25f * effectiveTicks);
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, hungerTime, 2));

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.01f);
        }
        // 播放激活音效
        AcademySounds.playSound(player, AcademySounds.EM_INTENSIFY_ACTIVATE, 0.5f, 1.0f);
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
        // 蓄力取消，不产生效果
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        // 蓄力技能通过 Charging 接口执行
    }
}
