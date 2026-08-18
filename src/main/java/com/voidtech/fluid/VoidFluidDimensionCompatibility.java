package com.voidtech.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Compatibility layer for dimensions without an explicit VoidTech resource pool.
 *
 * Dimension Upgrade chooses the dimension. This class only discovers resources;
 * it never changes output quantity. Quantity remains the responsibility of the
 * Yield Upgrade.
 */
public final class VoidFluidDimensionCompatibility {
    private static final TagKey<Block> ORES =
            TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "ores"));

    private VoidFluidDimensionCompatibility() {}

    public static List<ResourceLocation> getResources(ResourceLocation dimension) {
        List<ResourceLocation> explicit =
                VoidFluidDimensionOreCatalog.getPool(dimension);

        if (!explicit.isEmpty()) {
            return explicit;
        }

        Set<ResourceLocation> discovered = new LinkedHashSet<>();
        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            if (block.defaultBlockState().is(ORES)) {
                ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
                if (id != null) {
                    discovered.add(id);
                }
            }
        }

        return new ArrayList<>(discovered);
    }

    public static boolean hasResources(ResourceLocation dimension) {
        return !getResources(dimension).isEmpty();
    }
}
