package com.mohistmc.academy.skill;

import com.mojang.logging.LogUtils;
import com.mohistmc.academy.config.ACConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;

/**
 * 跨服能力数据存储（解决 BungeeCord / Velocity 下能力丢失，见 Issue #16）。
 *
 * <p>NeoForge 的玩家附件（attachment）默认只保存在各后端服务器自己的 playerdata 中，
 * 不会随代理网络跨服传送自动迁移，导致跨服后能力丢失。开启 {@code crossServerSync} 后，
 * 本类把玩家能力数据以 NBT 形式写入一个<strong>多后端共享目录</strong>（见 {@code crossServerSyncDir}），
 * 并在登录时读回，从而实现跨服保留。</p>
 *
 * <p>该目录必须能被所有后端服务器访问（例如容器化代理集群挂载的共享卷）；若不共享文件系统，
 * 则需要改用共享数据库（MySQL 等）方案——留作后续扩展。</p>
 */
public final class CrossServerAbilityStore {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CrossServerAbilityStore() {}

    private static Path fileFor(UUID uuid) {
        String dir = ACConfig.Server.crossServerSyncDir();
        Path base = (dir == null || dir.isBlank()) ? Path.of("academy_cross_server") : Path.of(dir);
        return base.resolve(uuid.toString() + ".dat");
    }

    public static Optional<PlayerAbilityData> load(ServerPlayer player) {
        Path file = fileFor(player.getUUID());
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        try (InputStream is = Files.newInputStream(file)) {
            CompoundTag tag = NbtIo.readCompressed(is);
            return Optional.of(PlayerAbilityData.fromSyncTag(tag));
        } catch (IOException e) {
            LOGGER.warn("AcademyCraft: failed to load cross-server ability data for {}: {}",
                    player.getUUID(), e.getMessage());
            return Optional.empty();
        }
    }

    public static void save(ServerPlayer player) {
        Path file = fileFor(player.getUUID());
        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            CompoundTag tag = player.getData(AcademyAttachments.PLAYER_ABILITY).toSyncTag();
            try (OutputStream os = Files.newOutputStream(file)) {
                NbtIo.writeCompressed(tag, os);
            }
        } catch (IOException e) {
            LOGGER.warn("AcademyCraft: failed to save cross-server ability data for {}: {}",
                    player.getUUID(), e.getMessage());
        }
    }
}
