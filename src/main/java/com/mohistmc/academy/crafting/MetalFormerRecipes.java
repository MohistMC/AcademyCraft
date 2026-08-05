package com.mohistmc.academy.crafting;

/**
 * 金属成型机工作模式。
 */
public final class MetalFormerRecipes {

    private MetalFormerRecipes() {}

    public enum Mode {
        PLATE, INCISE, ETCH, REFINE;

        public static Mode byOrdinal(int i) {
            Mode[] v = values();
            return (i >= 0 && i < v.length) ? v[i] : PLATE;
        }
    }
}
