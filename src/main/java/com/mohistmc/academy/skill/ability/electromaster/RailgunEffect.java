package com.mohistmc.academy.skill.ability.electromaster;

import com.mohistmc.academy.entity.RailgunBeamEntity;
import com.mohistmc.academy.skill.ChargingSkillEffect;
import com.mohistmc.academy.skill.PlayerAbilityData;
import com.mohistmc.academy.world.AcademyEntities;
import com.mohistmc.academy.world.AcademyItems;
import java.util.List;
import net.minecraft.core.BlockPos;
import com.mohistmc.academy.client.effect.EffectHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import com.mohistmc.academy.client.sound.AcademySounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import static com.mohistmc.academy.utils.MathUtils.lerpf;

public class RailgunEffect implements ChargingSkillEffect {

    private static final int CHARGE_TICKS = 20;
    private static final double RANGE = 45.0;
    private static final double RADIUS = 1.5;
    private static final double REFLECT_RANGE = 15.0;

    @Override
    public String getId() {
        return "railgun";
    }

    @Override
    public int getMinChargeTicks() {
        return CHARGE_TICKS;
    }

    @Override
    public int getMaxChargeTicks() {
        return CHARGE_TICKS;
    }

    private boolean isAccepted(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(AcademyItems.COIN) || stack.is(Items.IRON_BLOCK));
    }

    @Override
    public void onChargingStart(ServerPlayer player, PlayerAbilityData data) {
        ItemStack held = player.getMainHandItem();
        if (!isAccepted(held)) {
            return;
        }
        float exp = data.getProficiency(getId());
        float overload = lerpf(180, 120, exp);
        if (!data.isDevMode()) {
            data.addOverload(overload);
        }
    }

    @Override
    public boolean onChargingTick(ServerPlayer player, PlayerAbilityData data, int ticks) {
        ItemStack held = player.getMainHandItem();
        return isAccepted(held);
    }

    @Override
    public void onChargingRelease(ServerPlayer player, PlayerAbilityData data, int ticks) {
        ItemStack held = player.getMainHandItem();
        if (!isAccepted(held)) {
            return;
        }

        float exp = data.getProficiency(getId());
        float cp = lerpf(200, 450, exp);

        if (!data.isDevMode() && data.getCurrentCp() < cp) {
            return;
        }

        if (!player.isCreative()) {
            held.shrink(1);
            if (held.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            }
        }

        if (!data.isDevMode()) {
            data.setCurrentCp(data.getCurrentCp() - cp);
        }

        performRailgun(player, data, exp);
    }

    @Override
    public void onChargingAbort(ServerPlayer player, PlayerAbilityData data) {
    }

    @Override
    public void execute(ServerPlayer player, PlayerAbilityData data) {
    }

    private void performRailgun(ServerPlayer player, PlayerAbilityData data, float exp) {
        ServerLevel level = player.serverLevel();
        float damage = lerpf(60, 110, exp);
        double energy = lerpf(900, 2000, exp);

        Vec3 lookVec = player.getLookAngle();
        Vec3 rightVec = lookVec.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 eyePos = player.getEyePosition(0);
        Vec3 startPos = eyePos
                .add(rightVec.scale(0.3))
                .add(0, -0.2, 0)
                .add(lookVec.scale(0.3));

        double beamLength = Math.min(RANGE, energy / 20);

        RailgunBeamEntity beam = new RailgunBeamEntity(AcademyEntities.RAILGUN_BEAM.get(), level);
        beam.setPos(startPos.x, startPos.y, startPos.z);
        beam.setBeam(startPos, lookVec, beamLength);
        level.addFreshEntity(beam);

        boolean hitEntity = false;
        for (double d = 1.0; d <= RANGE; d += 1.0) {
            Vec3 checkPos = startPos.add(lookVec.scale(d));
            AABB area = new AABB(
                    checkPos.x - RADIUS, checkPos.y - RADIUS, checkPos.z - RADIUS,
                    checkPos.x + RADIUS, checkPos.y + RADIUS, checkPos.z + RADIUS
            );
            List<Entity> entities = level.getEntities(player, area, Entity::isAlive);

            for (Entity e : entities) {
                if (e instanceof LivingEntity target && e != player) {
                    target.hurt(player.damageSources().playerAttack(player), damage);
                    EffectHelper.glowBurst(level,
                            e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ(),
                            3, 0.3f, 0x88FFCC44, 10, 0.2f);
                    hitEntity = true;

                    reflectDamage(player, data, exp, e.position());
                }
            }

            BlockPos pos = BlockPos.containing(checkPos.x, checkPos.y, checkPos.z);
            var state = level.getBlockState(pos);
            if (!state.isAir() && !state.is(Blocks.BEDROCK)) {
                // 检查玩家是否有权破坏该方块（领地保护等）
                BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(level, pos, state, player);
                NeoForge.EVENT_BUS.post(breakEvent);
                if (!breakEvent.isCanceled()) {
                    level.destroyBlock(pos, true);
                }
            }
        }

        AcademySounds.playSound(level, player.getX(), player.getY(), player.getZ(),
                AcademySounds.EM_RAILGUN, SoundSource.PLAYERS, 0.5f, 1.0f);

        if (hitEntity) {
            data.addProficiency(getId(), 0.01f);
        } else {
            data.addProficiency(getId(), 0.005f);
        }
    }

    private void reflectDamage(ServerPlayer player, PlayerAbilityData data, float exp, Vec3 reflectorPos) {
        ServerLevel level = player.serverLevel();
        float damage = lerpf(60, 110, exp) * 0.5f;

        AABB area = new AABB(
                reflectorPos.x - REFLECT_RANGE, reflectorPos.y - REFLECT_RANGE, reflectorPos.z - REFLECT_RANGE,
                reflectorPos.x + REFLECT_RANGE, reflectorPos.y + REFLECT_RANGE, reflectorPos.z + REFLECT_RANGE
        );
        List<Entity> entities = level.getEntities(player, area, Entity::isAlive);

        for (Entity e : entities) {
            if (e instanceof LivingEntity target && e != player) {
                double dist = reflectorPos.distanceTo(e.position());
                if (dist <= REFLECT_RANGE) {
                    target.hurt(player.damageSources().playerAttack(player), damage);
                }
            }
        }
    }
}
