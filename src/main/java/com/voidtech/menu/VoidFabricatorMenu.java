package com.voidtech.menu;

import com.voidtech.registry.ModItems;
import com.voidtech.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public final class VoidFabricatorMenu extends AbstractContainerMenu {
    private final int tier;
    private final ContainerData data;
    private final BlockPos machinePos;

    public VoidFabricatorMenu(
            int id, Inventory inv, int tier, ContainerData data,
            IItemHandler input, IItemHandler output,
            IItemHandler upgrades, BlockPos machinePos) {

        super(ModMenus.VOID_FABRICATOR.get(), id);
        this.tier = Math.max(1, Math.min(6, tier));
        this.data = data;
        this.machinePos = machinePos;
        addDataSlots(data);

        for (int i = 0; i < 9; i++) {
            addSlot(new SlotItemHandler(input, i, 44 + (i % 3) * 18, 18 + (i / 3) * 18));
        }

        addSlot(new SlotItemHandler(output, 0, 143, 36) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });

        for (int i = 0; i < 4; i++) {
            final int slot = i;
            addSlot(new SlotItemHandler(upgrades, i, 44 + i * 18, 76) {
                @Override public boolean mayPlace(ItemStack stack) {
                    return switch (slot) {
                        case 0 -> stack.is(ModItems.SPEED_UPGRADE.get());
                        case 1 -> stack.is(ModItems.YIELD_UPGRADE.get());
                        case 2 -> stack.is(ModItems.PRECISION_UPGRADE.get());
                        case 3 -> stack.is(ModItems.DIMENSION_UPGRADE.get());
                        default -> false;
                    };
                }

                @Override public int getMaxStackSize() {
                    return slot < 2 ? 6 : 1;
                }
            });
        }

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlot(new Slot(inv, c + r * 9 + 9, 8 + c * 18, 112 + r * 18));

        for (int c = 0; c < 9; c++)
            addSlot(new Slot(inv, c, 8 + c * 18, 170));
    }

    public static VoidFabricatorMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
        int tier = buf.readVarInt();
        BlockPos pos = buf.readBlockPos();
        return new VoidFabricatorMenu(
                id, inv, tier, new net.minecraft.world.inventory.SimpleContainerData(6),
                new net.minecraftforge.items.ItemStackHandler(9),
                new net.minecraftforge.items.ItemStackHandler(1),
                new net.minecraftforge.items.ItemStackHandler(4), pos);
    }

    public int getTier() { return tier; }
    public BlockPos getMachinePos() { return machinePos; }
    public int getProgress() { return data.get(0); }
    public int getMaxProgress() { return Math.max(1, data.get(1)); }
    public int getEnergyStored() { return data.get(2); }
    public int getMaxEnergyStored() { return Math.max(1, data.get(3)); }
    public int getFluidStored() { return data.get(4); }
    public int getFluidCapacity() { return Math.max(1, data.get(5)); }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = getSlot(index).getItem().copy();
        if (stack.isEmpty()) return ItemStack.EMPTY;

        int machineEnd = 14;
        if (index < machineEnd) {
            if (!moveItemStackTo(stack, machineEnd, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, 9, false)) {
            return ItemStack.EMPTY;
        }

        getSlot(index).set(ItemStack.EMPTY);
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
