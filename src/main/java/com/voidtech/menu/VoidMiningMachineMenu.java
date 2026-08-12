package com.voidtech.menu;

import com.voidtech.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VoidMiningMachineMenu extends AbstractContainerMenu {

    private final int tier;
    private final ContainerData data;

    public VoidMiningMachineMenu(
            int containerId,
            Inventory inventory,
            int tier,
            ContainerData data,
            IItemHandler outputInventory
    ) {
        super(ModMenus.VOID_MINING_MACHINE.get(), containerId);
        this.tier = Math.max(1, Math.min(6, tier));
        this.data = data;
        addDataSlots(this.data);

        // Nine output slots.
        for (int i = 0; i < 9; i++) {
            int x = 44 + (i % 9) * 18;
            int y = 40;
            addSlot(new SlotItemHandler(outputInventory, i, x, y));
        }

        // Player inventory.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new net.minecraft.world.inventory.Slot(
                        inventory, col + row * 9 + 9,
                        8 + col * 18, 84 + row * 18
                ));
            }
        }

        // Hotbar.
        for (int col = 0; col < 9; col++) {
            addSlot(new net.minecraft.world.inventory.Slot(
                    inventory, col, 8 + col * 18, 142
            ));
        }
    }

    private VoidMiningMachineMenu(
            int containerId,
            Inventory inventory,
            int tier
    ) {
        this(
                containerId,
                inventory,
                tier,
                new SimpleContainerData(3),
                new net.minecraftforge.items.ItemStackHandler(9)
        );
    }

    public static VoidMiningMachineMenu fromNetwork(
            int containerId,
            Inventory inventory,
            FriendlyByteBuf data
    ) {
        int tier = data.readVarInt();
        return new VoidMiningMachineMenu(containerId, inventory, tier);
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

    public boolean isStructureValid() {
        return data.get(2) == 1;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 9) {
            ItemStack stack = getSlot(index).getItem().copy();
            if (!moveItemStackTo(stack, 9, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            getSlot(index).set(ItemStack.EMPTY);
            return stack;
        }

        if (index < slots.size()) {
            ItemStack stack = getSlot(index).getItem().copy();
            if (!moveItemStackTo(stack, 0, 9, false)) {
                return ItemStack.EMPTY;
            }
            getSlot(index).set(ItemStack.EMPTY);
            return stack;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
