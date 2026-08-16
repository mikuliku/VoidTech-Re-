package com.voidtech.fluid;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Central definition of the VoidTech fluid production tiers.
 *
 * <p>Only the six fluids registered by VoidTech are tier-restricted.
 * Vanilla fluids and fluids supplied by other mods remain selectable,
 * provided that they are registered source fluids.</p>
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

    private VoidFluidCatalog() {
    }

    /**
     * Returns the minimum VoidFluidMachine tier required for this fluid.
     * Non-VoidTech fluids return 0, meaning they are not tier restricted.
     */
    public static int requiredTier(ResourceLocation id) {
        if (id == null || !"voidtech".equals(id.getNamespace())) {
            return 0;
        }

        return VOID_FLUID_TIERS.getOrDefault(id.getPath(), 0);
    }

    /**
     * Checks whether a machine of the supplied tier may produce the fluid.
     */
    public static boolean canProduce(ResourceLocation id, int machineTier) {
        if (id == null) {
            return false;
        }

        int required = requiredTier(id);
        return required == 0 || machineTier >= required;
    }

    /**
     * Returns true when the fluid is one of the six VoidTech progression fluids.
     */
    public static boolean isVoidTechFluid(ResourceLocation id) {
        return id != null
                && "voidtech".equals(id.getNamespace())
                && VOID_FLUID_TIERS.containsKey(id.getPath());
    }

    private static int clampTier(int tier) {
        return Math.max(1, Math.min(6, tier));
    }
}
