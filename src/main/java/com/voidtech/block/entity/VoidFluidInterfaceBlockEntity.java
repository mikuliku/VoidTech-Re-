package com.voidtech.block.entity;

import com.voidtech.menu.VoidFluidInterfaceMenu;
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
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class VoidFluidInterfaceBlockEntity extends BlockEntity implements MenuProvider {
    private final int tier;
    private final FluidTank tank;
    private final LazyOptional<IFluidHandler> fluidCapability;

    public VoidFluidInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));
        this.tank = new FluidTank(capacityFor(this.tier)) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };
        this.fluidCapability = LazyOptional.of(() -> tank);
    }

    public int getTier() {
        return tier;
    }

    public FluidTank getTank() {
        return tank;
    }

    public static int capacityFor(int tier) {
        return switch (Math.max(1, Math.min(6, tier))) {
            case 1 -> 16000;
            case 2 -> 32000;
            case 3 -> 64000;
            case 4 -> 128000;
            case 5 -> 256000;
            default -> 512000;
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.voidtech.void_fluid_interface_t" + tier);
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new VoidFluidInterfaceMenu(id, inventory, tier, tank, worldPosition);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability,
                                               @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return fluidCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Fluid", tank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Fluid")) {
            tank.readFromNBT(tag.getCompound("Fluid"));
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidCapability.invalidate();
    }
}
