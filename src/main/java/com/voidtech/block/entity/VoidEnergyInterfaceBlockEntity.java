package com.voidtech.block.entity;

import com.voidtech.menu.VoidEnergyInterfaceMenu;
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
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

public class VoidEnergyInterfaceBlockEntity extends BlockEntity implements MenuProvider {
    private final int tier;
    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCapability;

    public VoidEnergyInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));
        this.energyStorage = new EnergyStorage(capacityFor(this.tier), transferFor(this.tier), transferFor(this.tier)) {
            @Override public int receiveEnergy(int maxReceive, boolean simulate) {
                int n = super.receiveEnergy(maxReceive, simulate);
                if (!simulate && n > 0) setChanged();
                return n;
            }
            @Override public int extractEnergy(int maxExtract, boolean simulate) {
                int n = super.extractEnergy(maxExtract, simulate);
                if (!simulate && n > 0) setChanged();
                return n;
            }
        };
        this.energyCapability = LazyOptional.of(() -> energyStorage);
    }

    public int getTier() { return tier; }
    public int getEnergyStored() { return energyStorage.getEnergyStored(); }
    public int getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }

    public static int capacityFor(int tier) {
        return switch (Math.max(1, Math.min(6, tier))) {
            case 1 -> 100_000; case 2 -> 250_000; case 3 -> 500_000;
            case 4 -> 1_000_000; case 5 -> 2_500_000; default -> 5_000_000;
        };
    }

    public static int transferFor(int tier) {
        return switch (Math.max(1, Math.min(6, tier))) {
            case 1 -> 2_000; case 2 -> 5_000; case 3 -> 10_000;
            case 4 -> 20_000; case 5 -> 40_000; default -> 80_000;
        };
    }

    @Override public Component getDisplayName() {
        return Component.translatable("block.voidtech.void_energy_interface_t" + tier);
    }

    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new VoidEnergyInterfaceMenu(id, inventory, tier, createContainerData());
    }

    private net.minecraft.world.inventory.ContainerData createContainerData() {
        return new net.minecraft.world.inventory.ContainerData() {
            @Override public int get(int index) {
                return index == 0 ? getEnergyStored() : getMaxEnergyStored();
            }
            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 2; }
        };
    }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.ENERGY) return energyCapability.cast();
        return super.getCapability(capability, side);
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) energyStorage.deserializeNBT(tag.get("Energy"));
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
    }
}
