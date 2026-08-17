package com.voidtech.menu;

import com.voidtech.registry.ModItems;
import com.voidtech.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VoidFluidMachineMenu extends AbstractContainerMenu {
    private final int tier;
    private final ContainerData data;
    private final BlockPos machinePos;

    public VoidFluidMachineMenu(int id, Inventory inv, int tier, ContainerData data,
                                IItemHandler upgrades, BlockPos machinePos) {
        super(ModMenus.VOID_FLUID_MACHINE.get(), id);
        this.tier = Math.max(1, Math.min(6, tier));
        this.data = data;
        this.machinePos = machinePos;
        addDataSlots(data);

        for (int slot = 0; slot < 4; slot++) {
            final int upgradeSlot = slot;
            addSlot(new SlotItemHandler(upgrades, slot, 8 + slot * 20, 18) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return switch (upgradeSlot) {
                        case 0 -> stack.is(ModItems.SPEED_UPGRADE.get());
                        case 1 -> stack.is(ModItems.YIELD_UPGRADE.get());
                        case 2 -> stack.is(ModItems.PRECISION_UPGRADE.get());
                        case 3 -> stack.is(ModItems.DIMENSION_UPGRADE.get());
                        default -> false;
                    };
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }
    }

    private VoidFluidMachineMenu(int id, Inventory inv, int tier, BlockPos pos) {
        this(id, inv, tier, new net.minecraft.world.inventory.SimpleContainerData(6),
                new net.minecraftforge.items.ItemStackHandler(4), pos);
    }

    public static VoidFluidMachineMenu fromNetwork(int id, Inventory inv, FriendlyByteBuf buf) {
        return new VoidFluidMachineMenu(id, inv, buf.readVarInt(), buf.readBlockPos());
    }

    public int getTier() { return tier; }
    public BlockPos getMachinePos() { return machinePos; }
    public int getEnergyStored() { return data.get(0); }
    public int getMaxEnergyStored() { return Math.max(1, data.get(1)); }
    public int getProgress() { return data.get(2); }
    public boolean isStructureValid() { return data.get(3) == 1; }
    public int getFluidAmount() { return data.get(4); }
    public boolean hasDimensionUpgrade() { return data.get(5) == 1; }

    public int getFluidCapacity() {
        return switch (tier) {
            case 1 -> 16000;
            case 2 -> 32000;
            case 3 -> 64000;
            case 4 -> 128000;
            case 5 -> 256000;
            default -> 512000;
        };
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index >= 0 && index < 4) {
            ItemStack stack = getSlot(index).getItem().copy();
            if (stack.isEmpty()) return ItemStack.EMPTY;
            if (moveItemStackTo(stack, 4, slots.size(), true)) {
                getSlot(index).set(ItemStack.EMPTY);
                return stack;
            }
            return ItemStack.EMPTY;
        }

        if (index >= 4 && index < slots.size()) {
            ItemStack stack = getSlot(index).getItem().copy();
            if (stack.isEmpty()) return ItemStack.EMPTY;

            for (int slot = 0; slot < 4; slot++) {
                if (getSlot(slot).mayPlace(stack)
                        && moveItemStackTo(stack, slot, slot + 1, false)) {
                    getSlot(index).set(stack);
                    return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
