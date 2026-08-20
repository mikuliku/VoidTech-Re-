package com.voidtech.upgrade;

/**
 * Shared upgrade calculations for both VoidTech machine families.
 *
 * This class deliberately keeps the four responsibilities separate:
 * speed -> time, yield -> amount, precision -> output form,
 * dimension -> selected dimension.
 */
public final class UpgradeEffects {
    private UpgradeEffects() {}

    public static int miningInterval(int baseInterval, int speedLevel) {
        int level = UpgradeRules.speedLevel(speedLevel);
        int reduction = baseInterval * 15 * level / 100;
        return Math.max(10, baseInterval - reduction);
    }

    public static int fluidInterval(int baseInterval, int speedLevel) {
        return miningInterval(baseInterval, speedLevel);
    }

    public static int outputAmount(int baseAmount, int yieldLevel) {
        if (baseAmount <= 0) return 0;

        int level = UpgradeRules.yieldLevel(yieldLevel);
        int amount = baseAmount * UpgradeRules.yieldMultiplier(level);

        // Small bonus chance scales with Yield Upgrade, but is still
        // exclusively part of the quantity system.
        return amount;
    }

    public static boolean precisionEnabled(int precisionLevel) {
        return UpgradeRules.hasPrecisionUpgrade(precisionLevel);
    }

    public static boolean dimensionEnabled(int dimensionLevel) {
        return UpgradeRules.hasDimensionUpgrade(dimensionLevel);
    }
}
