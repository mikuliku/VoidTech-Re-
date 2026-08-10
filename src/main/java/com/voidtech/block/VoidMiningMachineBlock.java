package com.voidtech.block;

import com.voidtech.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VoidMiningMachineBlock extends Block implements EntityBlock {

    private final int tier;

    public VoidMiningMachineBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new com.voidtech.block.entity.VoidMiningMachineBlockEntity(
                ModBlockEntities.VOID_MINING_MACHINE.get(), pos, state, tier
        );
    }
}
