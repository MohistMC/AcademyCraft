package com.mohistmc.academy.world;

import com.mohistmc.academy.capability.IIFCapability;

public class AcademyCapability {

    public static Capability<IIFCapability> IF_CAPABILITY = CapabilityManager.get(new CapabilityToken<IIFCapability>() {
        @Override
        public String toString() {
            return super.toString();
        }
    });
}
