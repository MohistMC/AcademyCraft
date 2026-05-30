package com.mohistmc.academy.terminal;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mgazul
 * @date 2026/5/31 02:46
 */
public class TerminalDataCodec implements IAttachmentSerializer<CompoundTag, TerminalData> {

    @Override
    public @NotNull TerminalData read(@NotNull IAttachmentHolder holder, CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        TerminalData data = new TerminalData();
        data.setInstalled(tag.getBoolean("installed"));

        if (tag.contains("apps")) {
            ListTag list = tag.getList("apps", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                data.installApp(list.getString(i));
            }
        }

        return data;
    }

    @Override
    public CompoundTag write(TerminalData data, HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("installed", data.isInstalled());

        ListTag appList = new ListTag();
        for (String appId : data.getInstalledApps()) {
            appList.add(StringTag.valueOf(appId));
        }
        tag.put("apps", appList);

        return tag;
    }
}
