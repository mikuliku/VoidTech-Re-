package com.voidtech.block.entity;

import com.voidtech.menu.VoidItemInterfaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class VoidItemInterfaceBlockEntity extends BlockEntity implements MenuProvider {
    private final int tier;
    private final ItemStackHandler itemHandler;
    private final LazyOptional<IItemHandler> itemCapability;

    public VoidItemInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));
        this.itemHandler = new ItemStackHandler(slotsFor(this.tier)) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
        this.itemCapability = LazyOptional.of(() -> itemHandler);
    }

    public int getTier() {
        return tier;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public static int slotsFor(int tier) {
        return switch (Math.max(1, Math.min(6, tier))) {
            case 1 -> 9;
            case 2 -> 18;
            case 3 -> 27;
            case 4 -> 36;
            case 5 -> 45;
            default -> 54;
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.voidtech.void_item_interface_t" + tier);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new VoidItemInterfaceMenu(id, inventory, tier, itemHandler, worldPosition);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability,
                                               @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", itemHandler.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Items")) {
            itemHandler.deserializeNBT(tag.getCompound("Items"));
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCapability.invalidate();
    }
}
