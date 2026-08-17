package com.voidtech.fluid;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class VoidFluidDimensionCatalog {
    private static final ResourceLocation OVERWORLD =
            new ResourceLocation("minecraft", "overworld");
    private static final ResourceLocation NETHER =
            new ResourceLocation("minecraft", "the_nether");
    private static final ResourceLocation END =
            new ResourceLocation("minecraft", "the_end");

    private VoidFluidDimensionCatalog() {}

    public static ResourceLocation currentDimension(Level level) {
        return level == null ? OVERWORLD : level.dimension().location();
    }

    public static ResourceLocation normalize(ResourceLocation dimension) {
        return dimension == null ? OVERWORLD : dimension;
    }

    public static ResourceKey<Level> key(ResourceLocation dimension) {
        return ResourceKey.create(Registries.DIMENSION, normalize(dimension));
    }

    public static boolean isVanillaDimension(ResourceLocation dimension) {
        ResourceLocation id = normalize(dimension);
        return OVERWORLD.equals(id) || NETHER.equals(id) || END.equals(id);
    }

    /**
     * Explicit dimension rules are intentionally empty for now.
     * Empty means the general VoidFluidCatalog remains available.
     */
    public static Set<ResourceLocation> getExplicitFluidRules(ResourceLocation dimension) {
        return Collections.emptySet();
    }

    public static boolean hasExplicitRule(ResourceLocation dimension) {
        return !getExplicitFluidRules(dimension).isEmpty();
    }

    /**
     * Returns whether a fluid is allowed by an explicit dimension rule.
     * When no explicit rule exists, return true so existing vanilla/modded
     * fluid compatibility is preserved.
     */
    public static boolean isAllowedByDimension(ResourceLocation dimension,
                                                ResourceLocation fluid) {
        Set<ResourceLocation> rules = getExplicitFluidRules(dimension);
        return rules.isEmpty() || rules.contains(fluid);
    }

    /**
     * Production multiplier hook reserved for future dimension-specific rules.
     * Keeping the default at 1.0 prevents the current production balance from
     * changing until the final dimension balance is defined.
     */
    public static double productionMultiplier(ResourceLocation dimension,
                                               ResourceLocation fluid) {
        return 1.0D;
    }

    public static Set<ResourceLocation> getKnownDimensions() {
        Set<ResourceLocation> result = new LinkedHashSet<>();
        result.add(OVERWORLD);
        result.add(NETHER);
        result.add(END);
        return Collections.unmodifiableSet(result);
    }
}
