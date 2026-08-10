package com.voidtech.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VoidMiningMachineBlockEntity extends BlockEntity {

    private final int tier;

    public VoidMiningMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int tier
    ) {
        super(type, pos, state);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }
}
