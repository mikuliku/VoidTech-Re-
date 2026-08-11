package com.voidtech.multiblock;

import com.voidtech.block.VoidMiningMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class VoidMiningStructure {

    private VoidMiningStructure() {
    }

    public static boolean isValid(Level level, BlockPos controllerPos, int tier) {
        if (tier < 1 || tier > 6) {
            return false;
        }

        BlockState controller = level.getBlockState(controllerPos);
        if (!(controller.getBlock() instanceof VoidMiningMachineBlock machine)
                || machine.getTier() != tier) {
            return false;
        }

        int radius = radiusFor(tier);
        int height = heightFor(tier);

        for (int y = 0; y < height; y++) {
            int layerRadius = radius - y;
            if (layerRadius < 1) {
                continue;
            }

            for (int x = -layerRadius; x <= layerRadius; x++) {
                for (int z = -layerRadius; z <= layerRadius; z++) {
                    boolean shell = Math.abs(x) == layerRadius
                            || Math.abs(z) == layerRadius;

                    BlockPos pos = controllerPos.offset(x, y + 1, z);

                    if (shell) {
                        if (!isFrame(level, pos)) {
                            return false;
                        }
                    } else if (!level.getBlockState(pos).isAir()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static boolean isFrame(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(com.voidtech.registry.ModBlocks.VOID_FRAME.get())
                || state.is(com.voidtech.registry.ModBlocks.REINFORCED_VOID_FRAME.get());
    }

    public static int radiusFor(int tier) {
        return switch (Math.max(1, Math.min(6, tier))) {
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 5;
            case 5 -> 6;
            default -> 7;
        };
    }

    public static int heightFor(int tier) {
        return radiusFor(tier);
    }

    public static BlockPos getTopCenter(BlockPos controllerPos, int tier) {
        return controllerPos.above(heightFor(tier));
    }

    public static boolean isInsideHollow(
            BlockPos controllerPos,
            BlockPos target,
            int tier
    ) {
        int dx = Math.abs(target.getX() - controllerPos.getX());
        int dz = Math.abs(target.getZ() - controllerPos.getZ());
        int dy = target.getY() - controllerPos.getY() - 1;

        if (dy < 0 || dy >= heightFor(tier)) {
            return false;
        }

        int radius = radiusFor(tier) - dy;
        return dx < radius && dz < radius;
    }

    private static boolean isAirOrReplaceable(Level level, BlockPos pos) {
        return level.getBlockState(pos).isAir()
                || level.getBlockState(pos).canBeReplaced();
    }
}
