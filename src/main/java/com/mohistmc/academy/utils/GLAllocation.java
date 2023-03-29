package com.mohistmc.academy.utils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@OnlyIn(Dist.CLIENT)
public class GLAllocation {
    public static synchronized ByteBuffer createDirectByteBuffer(int p_74524_0_) {
        return ByteBuffer.allocateDirect(p_74524_0_).order(ByteOrder.nativeOrder());
    }

    public static FloatBuffer createDirectFloatBuffer(int p_74529_0_) {
        return createDirectByteBuffer(p_74529_0_ << 2).asFloatBuffer();
    }
}
