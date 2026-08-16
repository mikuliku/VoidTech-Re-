package com.voidtech.multiblock;

import com.voidtech.block.VoidEnergyInterfaceBlock;
import com.voidtech.block.VoidFluidInterfaceBlock;
import com.voidtech.block.VoidFluidMachineBlock;
import com.voidtech.block.VoidItemInterfaceBlock;
import com.voidtech.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

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
                        if (!isFrame(level, check, tier)) {
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

    private static boolean isFrame(Level level, BlockPos pos, int machineTier) {
        Block block = level.getBlockState(pos).getBlock();

        if (block == ModBlocks.VOID_FRAME.get()
                || block == ModBlocks.REINFORCED_VOID_FRAME.get()) {
            return true;
        }

        if (block instanceof VoidFluidInterfaceBlock interfaceBlock) {
            return interfaceBlock.getTier() == machineTier;
        }

        if (block instanceof VoidEnergyInterfaceBlock interfaceBlock) {
            return interfaceBlock.getTier() == machineTier;
        }

        if (block instanceof VoidItemInterfaceBlock interfaceBlock) {
            return interfaceBlock.getTier() == machineTier;
        }

        return false;
    }
}
