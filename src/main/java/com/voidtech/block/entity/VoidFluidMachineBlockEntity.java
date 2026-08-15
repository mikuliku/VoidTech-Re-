package com.voidtech.block.entity;

import com.voidtech.multiblock.VoidFluidStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class VoidFluidMachineBlockEntity extends BlockEntity {
    private static final int[] ENERGY_CAPACITY =
            {0, 100000, 250000, 500000, 1000000, 2500000, 5000000};

    private static final int[] ENERGY_TRANSFER =
            {0, 2000, 5000, 10000, 20000, 40000, 80000};

    private static final int[] INTERVAL =
            {0, 200, 170, 140, 110, 85, 60};

    private static final int[] FLUID_PER_OPERATION =
            {0, 250, 350, 500, 750, 1000, 1500};

    private static final int[] ENERGY_PER_OPERATION =
            {0, 100, 180, 300, 450, 650, 900};

    private final int tier;
    private int progress;
    private boolean structureValid;

    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCapability;

    private final FluidTank tank;
    private final LazyOptional<IFluidHandler> fluidCapability;

    public VoidFluidMachineBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state,
            int tier) {
        super(type, pos, state);

        this.tier = Math.max(1, Math.min(6, tier));

        this.energyStorage = new EnergyStorage(
                ENERGY_CAPACITY[this.tier],
                ENERGY_TRANSFER[this.tier],
                ENERGY_TRANSFER[this.tier]
        ) {
            @Override
            public int receiveEnergy(int amount, boolean simulate) {
                int received = super.receiveEnergy(amount, simulate);
                if (!simulate && received > 0) {
                    setChanged();
                }
                return received;
            }

            @Override
            public int extractEnergy(int amount, boolean simulate) {
                int extracted = super.extractEnergy(amount, simulate);
                if (!simulate && extracted > 0) {
                    setChanged();
                }
                return extracted;
            }
        };

        this.energyCapability = LazyOptional.of(() -> energyStorage);

        this.tank = new FluidTank(capacityFor(this.tier)) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };

        this.fluidCapability = LazyOptional.of(() -> tank);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            VoidFluidMachineBlockEntity machine) {

        if (level.getGameTime() % 10L == 0L) {
            machine.structureValid =
                    VoidFluidStructure.isValid(level, pos, machine.tier);
        }

        if (!machine.structureValid) {
            return;
        }

        if (++machine.progress < INTERVAL[machine.tier]) {
            return;
        }

        machine.progress = 0;

        int energyCost = ENERGY_PER_OPERATION[machine.tier];
        int amount = FLUID_PER_OPERATION[machine.tier];

        if (machine.energyStorage.getEnergyStored() < energyCost) {
            return;
        }

        if (machine.tank.fill(
                new FluidStack(Fluids.WATER, amount),
                IFluidHandler.FluidAction.SIMULATE) != amount) {
            return;
        }

        machine.energyStorage.extractEnergy(energyCost, false);

        machine.tank.fill(
                new FluidStack(Fluids.WATER, amount),
                IFluidHandler.FluidAction.EXECUTE);

        machine.setChanged();
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

    public int getTier() {
        return tier;
    }

    public int getProgress() {
        return progress;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public boolean isStructureValid() {
        return structureValid;
    }

    public FluidTank getTank() {
        return tank;
    }

    public <T> LazyOptional<T> getCapability(
            Capability<T> capability,
            @Nullable net.minecraft.core.Direction side) {

        if (capability == ForgeCapabilities.ENERGY) {
            return energyCapability.cast();
        }

        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return fluidCapability.cast();
        }

        return super.getCapability(capability, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Fluid", tank.writeToNBT(new CompoundTag()));
        tag.putInt("Progress", progress);
        tag.putBoolean("StructureValid", structureValid);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(tag.get("Energy"));
        }

        if (tag.contains("Fluid")) {
            tank.readFromNBT(tag.getCompound("Fluid"));
        }

        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
        fluidCapability.invalidate();
    }
}
