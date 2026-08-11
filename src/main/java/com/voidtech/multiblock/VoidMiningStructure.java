package com.voidtech.multiblock;

import com.voidtech.block.VoidMiningMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class VoidMiningStructure {

    public static final int MIN_TIER = 1;
    public static final int MAX_TIER = 6;

    private VoidMiningStructure() {
    }

    public static boolean isValid(Level level, BlockPos controllerPos, int tier) {
        if (tier < MIN_TIER || tier > MAX_TIER) {
            return false;
        }

        // Batch-08 establishes the central controller footprint.
        // The complete hollow-pyramid validation will be added after
        // the frame and interface blocks are registered.
        BlockPos center = controllerPos.above();
        return level.getBlockState(controllerPos).getBlock() instanceof VoidMiningMachineBlock
                && level.getBlockState(center).isAir();
    }

    public static int getTierFromController(Level level, BlockPos pos) {
        if (level.getBlockState(pos).getBlock() instanceof VoidMiningMachineBlock block) {
            return block.getTier();
        }
        return 0;
    }
}
