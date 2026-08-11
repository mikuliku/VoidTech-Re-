package com.voidtech.menu;

import com.voidtech.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class VoidMiningMachineMenu extends AbstractContainerMenu {

    private final int tier;

    public VoidMiningMachineMenu(
            int containerId,
            Inventory inventory,
            int tier
    ) {
        super(ModMenus.VOID_MINING_MACHINE.get(), containerId);
        this.tier = Math.max(1, Math.min(6, tier));
    }

    public static VoidMiningMachineMenu fromNetwork(
            int containerId,
            Inventory inventory,
            FriendlyByteBuf data
    ) {
        return new VoidMiningMachineMenu(
                containerId,
                inventory,
                data.readVarInt()
        );
    }

    public int getTier() {
        return tier;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
