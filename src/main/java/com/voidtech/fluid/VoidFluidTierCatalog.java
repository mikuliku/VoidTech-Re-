package com.voidtech.fluid;

import com.voidtech.registry.ModFluids;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/**
 * Six-tier VoidTech fluid progression.
 *
 * A machine may produce every VoidTech fluid tier up to its machine tier.
 * Quantity is handled separately by the Yield Upgrade system.
 */
public final class VoidFluidTierCatalog {
    private static final List<RegistryObject<? extends Fluid>> TIERS = List.of(
            ModFluids.VOID_ORIGINAL,
            ModFluids.VOID_IGNITED,
            ModFluids.VOID_METAL_MELT,
            ModFluids.VOID_ESSENCE,
            ModFluids.VOID_ENERGY,
            ModFluids.VOID_CONCENTRATE
    );

    private VoidFluidTierCatalog() {}

    public static int maxTier() {
        return TIERS.size();
    }

    public static boolean canProduce(int machineTier, int fluidTier) {
        return machineTier >= 1
                && machineTier <= maxTier()
                && fluidTier >= 1
                && fluidTier <= machineTier;
    }

    public static RegistryObject<? extends Fluid> getFluid(int fluidTier) {
        if (fluidTier < 1 || fluidTier > maxTier()) {
            throw new IllegalArgumentException("Invalid VoidTech fluid tier: " + fluidTier);
        }
        return TIERS.get(fluidTier - 1);
    }

    public static ResourceLocation getId(int fluidTier) {
        return getFluid(fluidTier).getId();
    }

    public static List<RegistryObject<? extends Fluid>> getAvailableFluids(int machineTier) {
        if (machineTier < 1) return List.of();
        int end = Math.min(machineTier, maxTier());
        return TIERS.subList(0, end);
    }
}
