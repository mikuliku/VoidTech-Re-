package com.voidtech.fluid;

import net.minecraft.resources.ResourceLocation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class VoidFluidDimensionOreCatalog {
    private static final ResourceLocation OVERWORLD =
            new ResourceLocation("minecraft", "overworld");
    private static final ResourceLocation NETHER =
            new ResourceLocation("minecraft", "the_nether");
    private static final ResourceLocation END =
            new ResourceLocation("minecraft", "the_end");

    private static final Map<ResourceLocation, List<ResourceLocation>> POOLS =
            new LinkedHashMap<>();

    static {
        POOLS.put(OVERWORLD, List.of(
                new ResourceLocation("minecraft", "coal_ore"),
                new ResourceLocation("minecraft", "iron_ore"),
                new ResourceLocation("minecraft", "copper_ore"),
                new ResourceLocation("minecraft", "gold_ore"),
                new ResourceLocation("minecraft", "redstone_ore"),
                new ResourceLocation("minecraft", "lapis_ore"),
                new ResourceLocation("minecraft", "diamond_ore"),
                new ResourceLocation("minecraft", "emerald_ore")
        ));
        POOLS.put(NETHER, List.of(
                new ResourceLocation("minecraft", "nether_quartz_ore"),
                new ResourceLocation("minecraft", "nether_gold_ore"),
                new ResourceLocation("minecraft", "ancient_debris")
        ));
        POOLS.put(END, List.of(
                new ResourceLocation("minecraft", "chorus_flower"),
                new ResourceLocation("minecraft", "end_stone"),
                new ResourceLocation("minecraft", "diamond_ore")
        ));
    }

    private VoidFluidDimensionOreCatalog() {}

    public static List<ResourceLocation> getPool(ResourceLocation dimension) {
        List<ResourceLocation> pool = POOLS.get(dimension);
        return pool == null ? Collections.emptyList() : Collections.unmodifiableList(pool);
    }

    public static boolean hasPool(ResourceLocation dimension) {
        return !getPool(dimension).isEmpty();
    }

    public static boolean contains(ResourceLocation dimension, ResourceLocation resource) {
        return getPool(dimension).contains(resource);
    }

    public static Map<ResourceLocation, List<ResourceLocation>> getPools() {
        return Collections.unmodifiableMap(POOLS);
    }
}
