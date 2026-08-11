package com.voidtech.menu;

import com.voidtech.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;

public class VoidEnergyInterfaceMenu extends AbstractContainerMenu {
    private final int tier;
    private final ContainerData data;

    public VoidEnergyInterfaceMenu(int containerId, Inventory inventory, int tier, ContainerData data) {
        super(ModMenus.VOID_ENERGY_INTERFACE.get(), containerId);
        this.tier = Math.max(1, Math.min(6, tier));
        this.data = data;
        addDataSlots(data);
    }

    public static VoidEnergyInterfaceMenu fromNetwork(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        int tier = buf.readVarInt();
        return new VoidEnergyInterfaceMenu(containerId, inventory, tier, new SimpleContainerData(2));
    }

    public int getTier() { return tier; }
    public int getEnergyStored() { return data.get(0); }
    public int getMaxEnergyStored() { return Math.max(1, data.get(1)); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
