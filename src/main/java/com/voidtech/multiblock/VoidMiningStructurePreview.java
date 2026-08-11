package com.voidtech.multiblock;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public final class VoidMiningStructurePreview {

    private VoidMiningStructurePreview() {
    }

    public static List<BlockPos> getFramePositions(BlockPos controllerPos, int tier) {
        List<BlockPos> positions = new ArrayList<>();

        int radius = VoidMiningStructure.radiusFor(tier);
        int height = VoidMiningStructure.heightFor(tier);

        for (int y = 0; y < height; y++) {
            int layerRadius = radius - y;
            if (layerRadius < 1) {
                continue;
            }

            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    if (Math.abs(x) == layerRadius
                            || Math.abs(z) == layerRadius) {
                        positions.add(controllerPos.offset(x, y + 1, z));
                    }
                }
            }
        }

        return positions;
    }

    public static List<BlockPos> getHollowPositions(BlockPos controllerPos, int tier) {
        List<BlockPos> positions = new ArrayList<>();

        int radius = VoidMiningStructure.radiusFor(tier);
        int height = VoidMiningStructure.heightFor(tier);

        for (int y = 0; y < height; y++) {
            int layerRadius = radius - y;

            for (int x = -layerRadius + 1; x <= layerRadius - 1; x++) {
                for (int z = -layerRadius + 1; z <= layerRadius - 1; z++) {
                    positions.add(controllerPos.offset(x, y + 1, z));
                }
            }
        }

        return positions;
    }
}
