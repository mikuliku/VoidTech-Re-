package com.voidtech.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Provides the fluid choices a Void Fluid Machine may display.
 *
 * VoidTech progression fluids are limited by machine tier.
 * Vanilla and modded source fluids remain available on every tier.
 *
 * This class does not modify output quantity.
 */
public final class VoidFluidProductionSelection {
    private VoidFluidProductionSelection() {}

    public static List<ResourceLocation> getAvailableFluidIds(int machineTier) {
        List<ResourceLocation> result = new ArrayList<>();

        for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
            if (fluid == Fluids.EMPTY) continue;

            ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
            if (id == null) continue;

            if (!fluid.defaultFluidState().isSource()) continue;

            if (VoidFluidCatalog.canProduce(id, machineTier)) {
                result.add(id);
            }
        }

        result.sort(Comparator.comparing(ResourceLocation::toString));
        return result;
    }

    public static boolean isSelectable(ResourceLocation id, int machineTier) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        return fluid != null
                && fluid != Fluids.EMPTY
                && fluid.defaultFluidState().isSource()
                && VoidFluidCatalog.canProduce(id, machineTier);
    }
}
