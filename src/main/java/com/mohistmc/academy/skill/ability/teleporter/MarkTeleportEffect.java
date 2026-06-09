package com.mohistmc.academy.skill.ability.teleporter;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 标记传送 —— 第一次使用设置标记，第二次使用传送到标记位置
 */
public class MarkTeleportEffect implements SkillEffect {

    private static final Map<UUID, Vec3> MARKS = new HashMap<>();

    @Override
    public String getId() {
        return "mark_teleport";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        double maxDistance = lerpf(30.0f, 50.0f, exp);
        UUID playerId = player.getUUID();

        if (!MARKS.containsKey(playerId)) {
            Vec3 pos = player.position();
            MARKS.put(playerId, pos);
            player.sendSystemMessage(Component.literal("§a标记已设置: [" + (int) pos.x + ", " + (int) pos.y + ", " + (int) pos.z + "]"));
            if (!data.isDevMode()) {
                data.addProficiency(getId(), 0.003f);
            }
            return;
        }

        Vec3 markPos = MARKS.get(playerId);
        if (player.position().distanceTo(markPos) > maxDistance) {
            player.sendSystemMessage(Component.literal("§c标记距离过远"));
            return;
        }

        ServerLevel level = player.serverLevel();

        level.sendParticles(ParticleTypes.PORTAL,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);

        player.teleportTo(markPos.x, markPos.y, markPos.z);

        level.sendParticles(ParticleTypes.PORTAL,
                player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                20, 0.5, 0.5, 0.5, 0.1);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

        MARKS.remove(playerId);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }
}
