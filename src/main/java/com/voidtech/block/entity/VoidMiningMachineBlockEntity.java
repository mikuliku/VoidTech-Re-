package com.voidtech.block.entity;

import com.voidtech.menu.VoidMiningMachineMenu;
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

public class VoidMiningMachineBlockEntity extends BlockEntity implements MenuProvider {

    private static final int[] ENERGY_CAPACITY = {
            0, 100_000, 250_000, 500_000, 1_000_000, 2_500_000, 5_000_000
    };

    private static final int[] ENERGY_TRANSFER = {
            0, 2_000, 5_000, 10_000, 20_000, 40_000, 80_000
    };

    private final int tier;
    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCapability;

    public VoidMiningMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int tier
    ) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));

        this.energyStorage = new EnergyStorage(
                ENERGY_CAPACITY[this.tier],
                ENERGY_TRANSFER[this.tier],
                ENERGY_TRANSFER[this.tier]
        ) {
            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                int received = super.receiveEnergy(maxReceive, simulate);
                if (!simulate && received > 0) setChanged();
                return received;
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                int extracted = super.extractEnergy(maxExtract, simulate);
                if (!simulate && extracted > 0) setChanged();
                return extracted;
            }
        };

        this.energyCapability = LazyOptional.of(() -> this.energyStorage);
    }

    public int getTier() {
        return tier;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public int getEnergyTransferRate() {
        return ENERGY_TRANSFER[tier];
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.voidtech.void_mining_machine_t" + tier);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new VoidMiningMachineMenu(containerId, inventory, this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(
            Capability<T> capability,
            @Nullable net.minecraft.core.Direction side
    ) {
        if (capability == ForgeCapabilities.ENERGY) {
            return energyCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(tag.get("Energy"));
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
    }
}
