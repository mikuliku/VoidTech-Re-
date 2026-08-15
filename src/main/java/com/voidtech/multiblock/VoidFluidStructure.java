package com.voidtech.multiblock;

import com.voidtech.block.VoidFluidMachineBlock;
import com.voidtech.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class VoidFluidStructure {
    private VoidFluidStructure() {
    }

    public static boolean isValid(Level level, BlockPos controllerPos, int tier) {
        if (tier < 1 || tier > 6) {
            return false;
        }

        if (!(level.getBlockState(controllerPos).getBlock()
                instanceof VoidFluidMachineBlock machine)
                || machine.getTier() != tier) {
            return false;
        }

        int radius = VoidMiningStructure.radiusFor(tier);
        int height = VoidMiningStructure.heightFor(tier);

        for (int y = 0; y < height; y++) {
            int layerRadius = radius - y;
            if (layerRadius < 1) {
                continue;
            }

            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    boolean shell =
                            Math.abs(x) == layerRadius
                                    || Math.abs(z) == layerRadius;

                    BlockPos check = controllerPos.offset(x, y + 1, z);

                    if (shell) {
                        if (!isFrame(level, check)) {
                            return false;
                        }
                    } else if (!level.getBlockState(check).isAir()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean isFrame(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(ModBlocks.VOID_FRAME.get())
                || level.getBlockState(pos).is(ModBlocks.REINFORCED_VOID_FRAME.get());
    }
}
