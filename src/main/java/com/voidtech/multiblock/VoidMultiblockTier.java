package com.voidtech.multiblock;

public enum VoidMultiblockTier {

    T1(1),
    T2(2),
    T3(3),
    T4(4),
    T5(5),
    T6(6);

    private final int level;

    VoidMultiblockTier(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static VoidMultiblockTier fromLevel(int level) {
        return switch (level) {
            case 1 -> T1;
            case 2 -> T2;
            case 3 -> T3;
            case 4 -> T4;
            case 5 -> T5;
            default -> T6;
        };
    }
}
