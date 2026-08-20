package com.voidtech.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stage 4 compatibility layer.
 *
 * The Void Fluid Machine can expose registered vanilla and modded source
 * fluids in addition to the six VoidTech progression fluids.
 *
 * This class only discovers and classifies fluids. It does not apply any
 * production multiplier. Quantity remains controlled by Yield Upgrades.
 */
public final class VoidFluidCompatibilityCatalog {
    private VoidFluidCompatibilityCatalog() {}

    public static List<ResourceLocation> getSelectableFluids(int machineTier) {
        List<ResourceLocation> result = new ArrayList<>();

        for (Fluid fluid : ForgeRegistries.FLUIDS.getValues()) {
            if (fluid == Fluids.EMPTY) {
                continue;
            }

            if (!fluid.defaultFluidState().isSource()) {
                continue;
            }

            ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
            if (id == null) {
                continue;
            }

            if (VoidFluidCatalog.canProduce(id, machineTier)) {
                result.add(id);
            }
        }

        result.sort(Comparator.comparing(ResourceLocation::toString));
        return result;
    }

    public static boolean isSelectable(ResourceLocation id, int machineTier) {
        if (id == null) {
            return false;
        }

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        if (fluid == null || fluid == Fluids.EMPTY) {
            return false;
        }

        if (!fluid.defaultFluidState().isSource()) {
            return false;
        }

        return VoidFluidCatalog.canProduce(id, machineTier);
    }

    public static boolean isVoidTechFluid(ResourceLocation id) {
        return VoidFluidCatalog.isVoidTechFluid(id);
    }

    public static boolean isVanillaFluid(ResourceLocation id) {
        return id != null && "minecraft".equals(id.getNamespace());
    }

    public static boolean isModdedFluid(ResourceLocation id) {
        return id != null
                && !isVanillaFluid(id)
                && !isVoidTechFluid(id);
    }
}
