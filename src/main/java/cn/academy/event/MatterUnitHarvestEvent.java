package cn.academy.event;

import cn.academy.item.ItemMatterUnit.MatterMaterial;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author WeAthFolD
 */
public class MatterUnitHarvestEvent extends Event {
    
    public final EntityPlayer player;
    public final MatterMaterial mat;
    
    public MatterUnitHarvestEvent(EntityPlayer _player, MatterMaterial _mat) {
        player = _player;
        mat = _mat;
    }
    
}