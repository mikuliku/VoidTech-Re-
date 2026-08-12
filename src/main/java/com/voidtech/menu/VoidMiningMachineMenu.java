package com.voidtech.menu;

import com.voidtech.registry.ModItems;
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
            IItemHandler outputInventory,
            IItemHandler upgradeInventory
    ) {
        super(ModMenus.VOID_MINING_MACHINE.get(), containerId);
        this.tier = Math.max(1, Math.min(6, tier));
        this.data = data;
        addDataSlots(this.data);

        // Upgrade slots: speed, yield, precision.
        for (int i = 0; i < 3; i++) {
            final int upgradeSlot = i;
            addSlot(new SlotItemHandler(upgradeInventory, i, 116 + i * 18, 10) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return switch (upgradeSlot) {
                        case 0 -> stack.is(ModItems.SPEED_UPGRADE.get());
                        case 1 -> stack.is(ModItems.YIELD_UPGRADE.get());
                        case 2 -> stack.is(ModItems.PRECISION_UPGRADE.get());
                        default -> false;
                    };
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }

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
                        8 + col * 18, 102 + row * 18
                ));
            }
        }

        // Hotbar.
        for (int col = 0; col < 9; col++) {
            addSlot(new net.minecraft.world.inventory.Slot(
                    inventory, col, 8 + col * 18, 160
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
                new net.minecraftforge.items.ItemStackHandler(9),
                new net.minecraftforge.items.ItemStackHandler(3)
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
        final int upgradeSlots = 3;
        final int outputStart = upgradeSlots;
        final int outputEnd = upgradeSlots + 9;
        final int playerStart = outputEnd;

        if (index < outputStart) {
            ItemStack stack = getSlot(index).getItem().copy();
            if (!moveItemStackTo(stack, playerStart, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            getSlot(index).set(ItemStack.EMPTY);
            return stack;
        }

        if (index < outputEnd) {
            ItemStack stack = getSlot(index).getItem().copy();
            if (!moveItemStackTo(stack, playerStart, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            getSlot(index).set(ItemStack.EMPTY);
            return stack;
        }

        ItemStack stack = getSlot(index).getItem().copy();

        if (stack.is(ModItems.SPEED_UPGRADE.get())) {
            if (moveItemStackTo(stack, 0, 1, false)) {
                getSlot(index).set(ItemStack.EMPTY);
                return stack;
            }
        } else if (stack.is(ModItems.YIELD_UPGRADE.get())) {
            if (moveItemStackTo(stack, 1, 2, false)) {
                getSlot(index).set(ItemStack.EMPTY);
                return stack;
            }
        } else if (stack.is(ModItems.PRECISION_UPGRADE.get())) {
            if (moveItemStackTo(stack, 2, 3, false)) {
                getSlot(index).set(ItemStack.EMPTY);
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
