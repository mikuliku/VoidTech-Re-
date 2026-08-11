package com.voidtech.menu;

import com.voidtech.block.entity.VoidMiningMachineBlockEntity;
import com.voidtech.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class VoidMiningMachineMenu extends AbstractContainerMenu {

    private final int tier;
    private final ContainerData data;

    public VoidMiningMachineMenu(
            int containerId,
            Inventory inventory,
            VoidMiningMachineBlockEntity blockEntity
    ) {
        super(ModMenus.VOID_MINING_MACHINE.get(), containerId);

        this.tier = blockEntity.getTier();
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> blockEntity.getEnergyStored();
                    case 1 -> blockEntity.getMaxEnergyStored();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                // Energy is controlled by the machine's Forge Energy storage.
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        addDataSlots(this.data);
    }

    private VoidMiningMachineMenu(
            int containerId,
            Inventory inventory,
            int tier,
            ContainerData data
    ) {
        super(ModMenus.VOID_MINING_MACHINE.get(), containerId);
        this.tier = Math.max(1, Math.min(6, tier));
        this.data = data;
        addDataSlots(this.data);
    }

    public static VoidMiningMachineMenu fromNetwork(
            int containerId,
            Inventory inventory,
            FriendlyByteBuf data
    ) {
        int tier = data.readVarInt();
        return new VoidMiningMachineMenu(
                containerId,
                inventory,
                tier,
                new SimpleContainerData(2)
        );
    }

    public int getTier() {
        return tier;
    }

    public int getEnergyStored() {
        return data.get(0);
    }

    public int getMaxEnergyStored() {
        return Math.max(1, data.get(1));
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
