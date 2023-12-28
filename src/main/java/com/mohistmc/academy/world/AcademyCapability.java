package com.mohistmc.academy.world;

import com.mohistmc.academy.capability.IIFCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class AcademyCapability {

    public static Capability<IIFCapability> IF_CAPABILITY = CapabilityManager.get(new CapabilityToken<IIFCapability>() {
        @Override
        public String toString() {
            return super.toString();
        }
    });
}
