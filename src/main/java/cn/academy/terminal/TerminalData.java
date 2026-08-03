package cn.academy.terminal;

import cn.academy.event.AppInstalledEvent;
import cn.academy.event.TerminalInstalledEvent;
import cn.lambdalib2.s11n.SerializeExcluded;
import cn.lambdalib2.datapart.DataPart;
import cn.lambdalib2.datapart.EntityData;
import cn.lambdalib2.datapart.RegDataPart;
import cn.lambdalib2.s11n.SerializeIncluded;
import cn.lambdalib2.s11n.nbt.NBTS11n;
import cn.lambdalib2.s11n.network.NetworkMessage;
import cn.lambdalib2.s11n.network.NetworkMessage.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import cn.lambdalib2.s11n.network.NetworkS11n;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

/**
 * @author WeAthFolD
 */
@RegDataPart(EntityPlayer.class)
public class TerminalData extends DataPart<EntityPlayer> {
    @SerializeExcluded
    private byte[] _syncRollback;

    @Listener(channel = "itn_sync", side = Side.SERVER)
    private void onSyncIntercept(ByteBuf buf) {
        ByteBuf snap = Unpooled.buffer(512);
        NetworkS11n.serializeRecursively(snap, this, (Class) getClass());
        _syncRollback = ByteBufUtil.getBytes(snap);
        ((EntityPlayerMP) getEntity()).getServerWorld().addScheduledTask(() -> {
            if (_syncRollback != null) {
                NetworkS11n.deserializeRecursivelyInto(Unpooled.wrappedBuffer(_syncRollback), this, (Class) getClass());
                _syncRollback = null;
            }
        });
    }

    @Override
    protected void onSynchronized() {
        if (!isClient() && _syncRollback != null) {
            NetworkS11n.deserializeRecursivelyInto(Unpooled.wrappedBuffer(_syncRollback), this, (Class) getClass());
            _syncRollback = null;
        }
    }


    public static TerminalData get(EntityPlayer player) {
        return EntityData.get(player).getPart(TerminalData.class);
    }

    @SerializeIncluded
    private List<Integer> installedNameHashes = new ArrayList<>();
    @SerializeIncluded
    private boolean isInstalled;

    public TerminalData() {
        setClientNeedSync();
        setNBTStorage();
    }

    public List<App> getInstalledApps() {
        return AppRegistry.enumeration().stream().filter(this::isInstalled).collect(Collectors.toList());
    }

    public boolean isInstalled(App app) {
        return app.isPreInstalled() || installedNameHashes.contains(app.getName().hashCode());
    }

    public boolean isTerminalInstalled() {
        return isInstalled;
    }

    /**
     * Server only. Installs the data terminal.
     */
    public void install() {
        checkSide(Side.SERVER);

        if (!isInstalled) {
            isInstalled = true;

            sync();

            informTerminalInstall();
            NetworkMessage.sendTo(getEntity(), this, "terminal_inst");
        }
    }

    /**
     * Server only. Installs the given app.
     */
    public void installApp(App app) {
        checkSide(Side.SERVER);

        if (!isInstalled(app)) {
            installedNameHashes.add(app.getName().hashCode());

            sync();

            informAppInstall(app.getName());
            NetworkMessage.sendTo(getEntity(), this, "app_inst", app.getName());
        }
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        NBTS11n.read(tag, this);
    }

    @Override
    public void toNBT(NBTTagCompound tag) {
        NBTS11n.write(tag, this);
    }

    @Listener(channel="terminal_inst", side=Side.CLIENT)
    private void informTerminalInstall() {
        MinecraftForge.EVENT_BUS.post(new TerminalInstalledEvent(getEntity()));
    }

    @Listener(channel="app_inst", side=Side.CLIENT)
    private void informAppInstall(String appName) {
        MinecraftForge.EVENT_BUS.post(new AppInstalledEvent(getEntity(), AppRegistry.getByName(appName)));
    }

}