package com.mohistmc.academy.utils;

import java.util.Random;

/**
 * 随机数工具类
 */
public class RandUtils {

    public static final Random RNG = new Random();

    public static double ranged(double from, double to) {
        return from + RNG.nextDouble() * (to - from);
    }

    public static float rangef(float from, float to) {
        return from + RNG.nextFloat() * (to - from);
    }

    public static int rangei(int from, int to) {
        return from + RNG.nextInt(to - from);
    }

    public static float nextFloat() {
        return RNG.nextFloat();
    }

    public static double nextDouble() {
        return RNG.nextDouble();
    }

    public static int nextInt(int n) {
        return RNG.nextInt(n);
    }
}
