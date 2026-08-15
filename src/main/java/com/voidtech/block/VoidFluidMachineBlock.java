package com.voidtech.block;

import com.voidtech.block.entity.VoidFluidMachineBlockEntity;
import com.voidtech.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VoidFluidMachineBlock extends Block implements EntityBlock {
    private final int tier;

    public VoidFluidMachineBlock(Properties properties, int tier) {
        super(properties);
        this.tier = Math.max(1, Math.min(6, tier));
    }

    public int getTier() {
        return tier;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidFluidMachineBlockEntity(
                ModBlockEntities.VOID_FLUID_MACHINE.get(), pos, state, tier);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(
                type,
                ModBlockEntities.VOID_FLUID_MACHINE.get(),
                VoidFluidMachineBlockEntity::serverTick
        );
    }
}
