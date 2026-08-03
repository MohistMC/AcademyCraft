package cn.academy.misc.media

import java.util

import cn.lambdalib2.s11n.SerializeExcluded
import cn.lambdalib2.s11n.SerializeIncluded
import cn.lambdalib2.datapart.{DataPart, EntityData, RegDataPart}
import net.minecraftforge.fml.relauncher.Side
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.{NBTTagCompound, NBTTagList, NBTTagString}
import cn.lambdalib2.s11n.network.NetworkMessage.Listener
import cn.lambdalib2.s11n.network.NetworkS11n
import io.netty.buffer.{ByteBuf, ByteBufUtil, Unpooled}


object MediaAcquireData {
  def apply(player: EntityPlayer) = EntityData.get(player).getPart(classOf[MediaAcquireData])
}


@RegDataPart(value=classOf[EntityPlayer])
class MediaAcquireData extends DataPart[EntityPlayer] {
  @SerializeExcluded
  private var _syncRollback: Array[Byte] = null

  @Listener(channel = "itn_sync", side = Array(Side.SERVER))
  private def onSyncIntercept(buf: ByteBuf) = {
    val snap = Unpooled.buffer(512)
    NetworkS11n.serializeRecursively(snap, this, getClass.asInstanceOf[Class[AnyRef]])
    _syncRollback = ByteBufUtil.getBytes(snap)
    getEntity.asInstanceOf[EntityPlayerMP].getServerWorld.addScheduledTask(new Runnable {
      override def run(): Unit = {
        if (_syncRollback != null) {
          NetworkS11n.deserializeRecursivelyInto(Unpooled.wrappedBuffer(_syncRollback), this, getClass.asInstanceOf[Class[AnyRef]])
          _syncRollback = null
        }
      }
    })
  }

  override protected def onSynchronized(): Unit = {
    if (!isClient() && _syncRollback != null) {
      NetworkS11n.deserializeRecursivelyInto(Unpooled.wrappedBuffer(_syncRollback), this, getClass.asInstanceOf[Class[AnyRef]])
      _syncRollback = null
    }
  }


  @SerializeIncluded
  private var bitset = new util.BitSet() // Var for synchronization

  setClientNeedSync()
  setNBTStorage()

  /**
    * Install the given media. It must be internal. Should only be called in SERVER.
    */
  def install(media: Media) = {
    require(!media.external)
    checkSide(Side.SERVER)

    bitset.set(MediaManager.internalMedias.indexOf(media))

    sync()
  }

  /**
    * @return All installed **internal** medias.
    */
  def installed: List[Media] = {
    MediaManager.internalMedias.zipWithIndex
      .filter { case (_, id) => bitset.get(id) }
      .map(_._1)
      .toList
  }

  /**
    * @return Whether a media is installed for player. True for all external media.
    */
  def isInstalled(media: Media) = {
    media.external || bitset.get(MediaManager.internalMedias.indexOf(media))
  }

  override def toNBT(tag: NBTTagCompound): Unit = {
    val list = new NBTTagList
    installed.foreach (media => list.appendTag(new NBTTagString(media.id)))

    tag.setTag("acquired", list)
  }

  override def fromNBT(tag: NBTTagCompound): Unit = {
    if (tag.hasKey("acquired")) {
      tag.getTag("acquired") match {
        case list: NBTTagList =>
          for (i <- 0 until list.tagCount) {
            val mediaList = MediaManager.internalMedias
            val idx = mediaList.indexOf(MediaManager.get(list.getStringTagAt(i)))
            bitset.set(idx)
          }
        case _ => {}
      }
    }
  }
}