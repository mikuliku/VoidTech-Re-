package com.voidtech.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Safe helper for the Void Fluid Machine's fluid selector.
 *
 * Keeps the selector independent from any particular fluid mod. Every
 * registered source fluid can be presented, while VoidTech progression
 * fluids still obey machine-tier restrictions.
 */
public final class VoidFluidCompatibilityHelper {
    private VoidFluidCompatibilityHelper() {}

    public static List<ResourceLocation> getSelectableFluids(int machineTier) {
        return VoidFluidCompatibilityCatalog.getSelectableFluids(machineTier);
    }

    public static boolean canSelect(ResourceLocation id, int machineTier) {
        return VoidFluidCompatibilityCatalog.isSelectable(id, machineTier);
    }

    public static Fluid getFluid(ResourceLocation id) {
        if (id == null) return Fluids.EMPTY;

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        return fluid == null ? Fluids.EMPTY : fluid;
    }

    public static boolean isSource(ResourceLocation id) {
        Fluid fluid = getFluid(id);
        return fluid != Fluids.EMPTY && fluid.defaultFluidState().isSource();
    }
}
