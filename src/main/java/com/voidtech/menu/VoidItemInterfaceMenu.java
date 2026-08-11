package com.voidtech.menu;

import com.voidtech.block.entity.VoidItemInterfaceBlockEntity;
import com.voidtech.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VoidItemInterfaceMenu extends AbstractContainerMenu {
    private final int tier;
    private final int machineSlots;
    private final BlockPos blockPos;

    public VoidItemInterfaceMenu(int containerId, Inventory inventory, int tier,
                                 ItemStackHandler handler, BlockPos blockPos) {
        super(ModMenus.VOID_ITEM_INTERFACE.get(), containerId);
        this.tier = Math.max(1, Math.min(6, tier));
        this.machineSlots = VoidItemInterfaceBlockEntity.slotsFor(this.tier);
        this.blockPos = blockPos;

        int columns = 9;
        int rows = (machineSlots + columns - 1) / columns;

        for (int i = 0; i < machineSlots; i++) {
            int row = i / columns;
            int col = i % columns;
            addSlot(new SlotItemHandler(handler, i, 8 + col * 18, 18 + row * 18));
        }

        int playerStartY = 24 + rows * 18;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9,
                        8 + col * 18, playerStartY + row * 18));
            }
        }

        int hotbarY = playerStartY + 58;
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, hotbarY));
        }
    }

    public static VoidItemInterfaceMenu fromNetwork(int containerId, Inventory inventory,
                                                      FriendlyByteBuf buf) {
        int tier = buf.readVarInt();
        BlockPos pos = buf.readBlockPos();

        if (inventory.player.level().getBlockEntity(pos)
                instanceof VoidItemInterfaceBlockEntity be) {
            return new VoidItemInterfaceMenu(containerId, inventory, tier,
                    be.getItemHandler(), pos);
        }

        return new VoidItemInterfaceMenu(containerId, inventory, tier,
                new ItemStackHandler(VoidItemInterfaceBlockEntity.slotsFor(tier)), pos);
    }

    public int getTier() {
        return tier;
    }

    public int getMachineSlots() {
        return machineSlots;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if (index < machineSlots) {
                if (!moveItemStackTo(stack, machineSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack, 0, machineSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                blockPos.getX() + 0.5D,
                blockPos.getY() + 0.5D,
                blockPos.getZ() + 0.5D) <= 64.0D;
    }
}
