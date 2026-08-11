package com.voidtech.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VoidEnergyInterfaceBlockEntity extends BlockEntity {

    private final int tier;

    public VoidEnergyInterfaceBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int tier
    ) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));
    }

    public int getTier() {
        return tier;
    }
}
