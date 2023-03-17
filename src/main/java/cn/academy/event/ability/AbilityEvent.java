package cn.academy.event.ability;

import cn.academy.datapart.AbilityData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author WeAthFolD
 */
public abstract class AbilityEvent extends Event {
    
    public final EntityPlayer player;
    
    public AbilityEvent(EntityPlayer _player) {
        player = _player;
    }
    
    public AbilityData getAbilityData() {
        return AbilityData.get(player);
    }
    
}