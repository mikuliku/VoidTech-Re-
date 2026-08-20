package com.voidtech.upgrade;

/**
 * Central rules for VoidTech's four upgrade families.
 *
 * Speed      -> production time only
 * Yield      -> output amount only
 * Precision  -> output form/precision only
 * Dimension  -> selected dimension/resource pool only
 *
 * No upgrade in this class changes another upgrade's responsibility.
 */
public final class UpgradeRules {
    public enum Type {
        SPEED,
        YIELD,
        PRECISION,
        DIMENSION
    }

    private static final int MAX_LEVEL = 6;

    private UpgradeRules() {}

    public static int clampLevel(int level) {
        return Math.max(0, Math.min(MAX_LEVEL, level));
    }

    public static int speedLevel(int level) {
        return clampLevel(level);
    }

    public static int yieldLevel(int level) {
        return clampLevel(level);
    }

    public static int precisionLevel(int level) {
        return clampLevel(level);
    }

    public static int dimensionLevel(int level) {
        return clampLevel(level);
    }

    /**
     * Returns the speed multiplier only.
     * The base production time is reduced by 15% per level.
     */
    public static double speedMultiplier(int level) {
        return 1.0 / (1.0 + 0.15 * speedLevel(level));
    }

    /**
     * Returns the quantity multiplier only.
     * This is the ONLY multiplier supplied by the upgrade system.
     */
    public static int yieldMultiplier(int level) {
        return 1 + yieldLevel(level);
    }

    /**
     * Precision is deliberately represented as a level, not a multiplier.
     */
    public static boolean hasPrecisionUpgrade(int level) {
        return precisionLevel(level) > 0;
    }

    /**
     * Dimension is deliberately represented as a level, not a multiplier.
     */
    public static boolean hasDimensionUpgrade(int level) {
        return dimensionLevel(level) > 0;
    }

    public static boolean isValid(Type type, int level) {
        return type != null && level >= 0 && level <= MAX_LEVEL;
    }

    public static int maxLevel() {
        return MAX_LEVEL;
    }
}
