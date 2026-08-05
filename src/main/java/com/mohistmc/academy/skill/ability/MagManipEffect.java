package com.mohistmc.academy.skill.ability;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import com.mohistmc.academy.world.AcademySounds;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

public class MagManipEffect implements SkillEffect {

    @Override
    public String getId() {
        return "mag_manip";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        ServerLevel level = player.serverLevel();
        float proficiency = data.getProficiency(getId());
        double range = 10.0;

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        BlockPos targetBlock = null;
        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            BlockPos pos = BlockPos.containing(checkPos.x, checkPos.y, checkPos.z);
            BlockState state = level.getBlockState(pos);
            if (isMagnetic(state)) {
                targetBlock = pos;
                break;
            }
        }

        if (targetBlock != null) {
            level.destroyBlock(targetBlock, false);

            Vec3 targetVec = Vec3.atCenterOf(targetBlock);
            float damage = 6.0f + proficiency * 6.0f;
            List<Entity> entities = level.getEntities(player, new AABB(targetVec.x - 2, targetVec.y - 2, targetVec.z - 2,
                            targetVec.x + 2, targetVec.y + 2, targetVec.z + 2),
                    Entity::isAlive);
            for (Entity e : entities) {
                if (e instanceof LivingEntity living) {
                    living.hurt(player.damageSources().playerAttack(player), damage);
                }
            }

            AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                    AcademySounds.EM_MAG_MANIP, SoundSource.PLAYERS, 0.5f, 1.0f);
        }
    }

    private boolean isMagnetic(BlockState state) {
        return state.is(Blocks.IRON_BLOCK) || state.is(Blocks.IRON_ORE) || state.is(Blocks.DEEPSLATE_IRON_ORE)
                || state.is(Blocks.RAW_IRON_BLOCK) || state.is(Blocks.ANVIL) || state.is(Blocks.HOPPER)
                || state.is(Blocks.PISTON) || state.is(Blocks.STICKY_PISTON)
                || state.is(Blocks.HEAVY_CORE) || state.is(Blocks.NETHERITE_BLOCK)
                || state.is(Blocks.GOLD_BLOCK) || state.is(Blocks.GOLD_ORE)
                || state.is(Blocks.DEEPSLATE_GOLD_ORE) || state.is(Blocks.RAW_GOLD_BLOCK);
    }

    @Override
    public int getCooldownTicks(float proficiency) {
        return (int) lerpf(60, 40, proficiency);
    }
}
