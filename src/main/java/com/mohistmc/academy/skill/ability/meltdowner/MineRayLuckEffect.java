package com.mohistmc.academy.skill.ability.meltdowner;

import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.skill.SkillEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.client.sound.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

/**
 * 矿物射线(幸运) —— 矿物射线幸运版，有概率获得额外掉落
 */
public class MineRayLuckEffect implements SkillEffect {

    private static final double RANGE = 20.0;

    @Override
    public String getId() {
        return "mine_ray_luck";
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
        float exp = data.getProficiency(getId());
        double range = lerpf(15.0f, 20.0f, exp);
        float extraDropChance = lerpf(0.2f, 0.5f, exp);

        ServerLevel level = player.serverLevel();
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        for (double d = 1.0; d <= range; d += 0.5) {
            Vec3 checkPos = eyePos.add(lookVec.scale(d));
            BlockPos bp = BlockPos.containing(checkPos.x, checkPos.y, checkPos.z);
            BlockState state = level.getBlockState(bp);

            if (isOre(state)) {
                level.sendParticles(ParticleTypes.FLAME,
                        checkPos.x, checkPos.y, checkPos.z,
                        2, 0.2, 0.2, 0.2, 0.01);
                level.destroyBlock(bp, true, player);

                // 幸运额外掉落
                if (level.random.nextFloat() < extraDropChance) {
                    net.minecraft.world.entity.item.ItemEntity drop = new net.minecraft.world.entity.item.ItemEntity(
                            level, bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5,
                            new ItemStack(Items.DIAMOND));
                    level.addFreshEntity(drop);
                }
                break;
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                AcademySounds.MD_RAY_SMALL, SoundSource.PLAYERS, 0.5f, 1.5f);

        if (!data.isDevMode()) {
            data.addProficiency(getId(), 0.005f);
        }
    }

    private boolean isOre(BlockState state) {
        return state.is(BlockTags.COAL_ORES)
                || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.COPPER_ORES)
                || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.REDSTONE_ORES)
                || state.is(BlockTags.LAPIS_ORES)
                || state.is(BlockTags.DIAMOND_ORES)
                || state.is(BlockTags.EMERALD_ORES);
    }
}
