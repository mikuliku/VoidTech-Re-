package com.voidtech.block.entity;

import com.voidtech.menu.VoidMiningMachineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VoidMiningMachineBlockEntity extends BlockEntity implements MenuProvider {

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

    @Override
    public Component getDisplayName() {
        return Component.translatable(
                "block.voidtech.void_mining_machine_t" + tier
        );
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory,
            Player player
    ) {
        return new VoidMiningMachineMenu(
                containerId,
                inventory,
                tier
        );
    }
}
