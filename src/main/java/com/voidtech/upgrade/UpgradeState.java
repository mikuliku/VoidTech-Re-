package com.voidtech.upgrade;

import com.voidtech.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * Reads the four upgrade slots as levels.
 *
 * The stack count is the installed level. This lets one upgrade item represent
 * multiple levels without creating sixteen separate item registrations.
 */
public final class UpgradeState {
    public static final int SPEED_SLOT = 0;
    public static final int YIELD_SLOT = 1;
    public static final int PRECISION_SLOT = 2;
    public static final int DIMENSION_SLOT = 3;

    private UpgradeState() {}

    public static int level(ItemStackHandler upgrades, int slot) {
        if (upgrades == null || slot < 0 || slot >= upgrades.getSlots()) {
            return 0;
        }
        return UpgradeRules.clampLevel(upgrades.getStackInSlot(slot).getCount());
    }

    public static int speed(ItemStackHandler upgrades) {
        return level(upgrades, SPEED_SLOT);
    }

    public static int yield(ItemStackHandler upgrades) {
        return level(upgrades, YIELD_SLOT);
    }

    public static int precision(ItemStackHandler upgrades) {
        return level(upgrades, PRECISION_SLOT);
    }

    public static int dimension(ItemStackHandler upgrades) {
        return level(upgrades, DIMENSION_SLOT);
    }

    public static boolean isSpeed(ItemStack stack) {
        return stack.is(ModItems.SPEED_UPGRADE.get());
    }

    public static boolean isYield(ItemStack stack) {
        return stack.is(ModItems.YIELD_UPGRADE.get());
    }

    public static boolean isPrecision(ItemStack stack) {
        return stack.is(ModItems.PRECISION_UPGRADE.get());
    }

    public static boolean isDimension(ItemStack stack) {
        return stack.is(ModItems.DIMENSION_UPGRADE.get());
    }

    public static boolean isValidSlot(int slot, ItemStack stack) {
        return switch (slot) {
            case SPEED_SLOT -> isSpeed(stack);
            case YIELD_SLOT -> isYield(stack);
            case PRECISION_SLOT -> isPrecision(stack);
            case DIMENSION_SLOT -> isDimension(stack);
            default -> false;
        };
    }
}
