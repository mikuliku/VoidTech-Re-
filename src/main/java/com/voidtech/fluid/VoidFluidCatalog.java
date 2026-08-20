package com.voidtech.fluid;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Central definition of the six VoidTech progression fluids.
 *
 * Machine tier controls which VoidTech fluids may be selected.
 * Vanilla and modded fluids are not restricted by this progression.
 * Output quantity is intentionally not handled here; that belongs to
 * the Yield Upgrade system.
 */
public final class VoidFluidCatalog {
    private static final Map<String, Integer> VOID_FLUID_TIERS = Map.of(
            "void_original", 1,
            "void_ignited", 2,
            "void_metal_melt", 3,
            "void_essence", 4,
            "void_energy", 5,
            "void_concentrate", 6
    );

    private VoidFluidCatalog() {}

    public static int requiredTier(ResourceLocation id) {
        if (id == null || !"voidtech".equals(id.getNamespace())) return 0;
        return VOID_FLUID_TIERS.getOrDefault(id.getPath(), 0);
    }

    public static boolean canProduce(ResourceLocation id, int machineTier) {
        if (id == null) return false;
        int required = requiredTier(id);
        return required == 0 || machineTier >= required;
    }

    public static boolean isVoidTechFluid(ResourceLocation id) {
        return requiredTier(id) > 0;
    }

    public static int clampTier(int tier) {
        return Math.max(1, Math.min(6, tier));
    }
}
