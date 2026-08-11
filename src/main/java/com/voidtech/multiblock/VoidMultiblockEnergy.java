package com.voidtech.multiblock;

import net.minecraftforge.energy.EnergyStorage;

public class VoidMultiblockEnergy extends EnergyStorage {

    private final int tier;

    public VoidMultiblockEnergy(int tier) {
        super(
                capacityFor(tier),
                transferFor(tier),
                transferFor(tier)
        );
        this.tier = Math.max(1, Math.min(6, tier));
    }

    public int getTier() {
        return tier;
    }

    public static int capacityFor(int tier) {
        return switch (Math.max(1, Math.min(6, tier))) {
            case 1 -> 100_000;
            case 2 -> 250_000;
            case 3 -> 500_000;
            case 4 -> 1_000_000;
            case 5 -> 2_500_000;
            default -> 5_000_000;
        };
    }

    public static int transferFor(int tier) {
        return switch (Math.max(1, Math.min(6, tier))) {
            case 1 -> 2_000;
            case 2 -> 5_000;
            case 3 -> 10_000;
            case 4 -> 20_000;
            case 5 -> 40_000;
            default -> 80_000;
        };
    }
}
