package com.voidtech.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Central rules for dimension-aware void-fluid production.
 *
 * This first version deliberately provides a safe, extensible framework:
 * - a machine without the dimension upgrade uses its current dimension;
 * - a machine with the upgrade uses its selected target dimension;
 * - no dimension is hard-coded to a special fluid yet.
 *
 * Later stages can add explicit dimension rules without changing the machine core.
 */
public final class VoidFluidDimensionCatalog {
    private static final ResourceLocation OVERWORLD =
            new ResourceLocation("minecraft", "overworld");
    private static final ResourceLocation NETHER =
            new ResourceLocation("minecraft", "the_nether");
    private static final ResourceLocation END =
            new ResourceLocation("minecraft", "the_end");

    private VoidFluidDimensionCatalog() {
    }

    public static ResourceLocation currentDimension(Level level) {
        if (level == null) {
            return OVERWORLD;
        }
        return level.dimension().location();
    }

    public static ResourceLocation normalize(ResourceLocation dimension) {
        return dimension == null ? OVERWORLD : dimension;
    }

    public static ResourceKey<Level> key(ResourceLocation dimension) {
        return ResourceKey.create(
                Registries.DIMENSION,
                normalize(dimension)
        );
    }

    /**
     * Returns whether the dimension is one of the vanilla dimensions.
     * This is intentionally only a classification helper; it does not restrict
     * modded dimensions from being used.
     */
    public static boolean isVanillaDimension(ResourceLocation dimension) {
        ResourceLocation id = normalize(dimension);
        return OVERWORLD.equals(id) || NETHER.equals(id) || END.equals(id);
    }

    /**
     * Returns the dimension's initial fluid-rule set.
     *
     * Empty means "use the general fluid catalog" rather than "produce nothing".
     * This distinction is important so modded dimensions continue to work.
     */
    public static Set<ResourceLocation> getExplicitFluidRules(ResourceLocation dimension) {
        return Collections.emptySet();
    }

    /**
     * Returns the explicitly registered dimensions currently known to VoidTech.
     */
    public static Set<ResourceLocation> getKnownDimensions() {
        Set<ResourceLocation> result = new LinkedHashSet<>();
        result.add(OVERWORLD);
        result.add(NETHER);
        result.add(END);
        return Collections.unmodifiableSet(result);
    }

    /**
     * Checks whether an explicit dimension rule exists.
     * The initial implementation returns false for all dimensions.
     */
    public static boolean hasExplicitRule(ResourceLocation dimension) {
        return !getExplicitFluidRules(dimension).isEmpty();
    }
}
