package com.voidtech.block;

import com.voidtech.block.entity.VoidEnergyInterfaceBlockEntity;
import com.voidtech.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class VoidEnergyInterfaceBlock extends Block implements EntityBlock {

    private final int tier;

    public VoidEnergyInterfaceBlock(Properties properties, int tier) {
        super(properties);
        this.tier = Math.max(1, Math.min(6, tier));
    }

    public int getTier() {
        return tier;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidEnergyInterfaceBlockEntity(
                ModBlockEntities.VOID_ENERGY_INTERFACE.get(),
                pos,
                state,
                tier
        );
    }
}
