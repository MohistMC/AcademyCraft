package cn.academy.ability;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import cn.academy.ability.api.PresetData;
import cn.academy.ability.api.event.SwitchPresetEvent;
import cn.academy.ability.client.ui.PresetEditUI;
import cn.academy.core.registry.RegACKeyHandler;
import cn.annoreg.core.Registrant;
import cn.annoreg.mc.RegInit;
import cn.liutils.util.helper.KeyHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Registrant
@RegInit(side = RegInit.Side.CLIENT_ONLY)
@SideOnly(Side.CLIENT)
public class ModuleAbilityClient {
	
	@RegACKeyHandler(name = "Edit Preset", defaultKey = Keyboard.KEY_N)
	public static KeyHandler keyEditPreset = new KeyHandler() {
		@Override
		public void onKeyDown() {
			PresetData data = PresetData.get(Minecraft.getMinecraft().thePlayer);
			if(data.isActive()) {
				PresetEditUI.guiHandler.openClientGui();
			} else {
				System.out.println("Not active!");
			}
		}
	};
	
	@RegACKeyHandler(name = "Switch Preset", defaultKey = Keyboard.KEY_C)
	public static KeyHandler keySwitchPreset = new KeyHandler() {
		@Override
		public void onKeyDown() {
			PresetData data = PresetData.get(getPlayer());
			//TODO: If in special skill mode, doesn't change preset?
			if(data.isActive()) {
				int next = (data.getCurrentID() + 1) % 4;
				data.switchCurrent(next);
				MinecraftForge.EVENT_BUS.post(new SwitchPresetEvent(data.getPlayer()));
			}
		}
	};
	
	public static void init() {
		
	}
	
}
