package com.voidtech.block.entity;

import com.voidtech.fluid.VoidFluidCatalog;
import com.voidtech.menu.VoidFluidMachineMenu;
import com.voidtech.multiblock.VoidFluidStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class VoidFluidMachineBlockEntity extends BlockEntity implements MenuProvider {
    private static final int[] ENERGY_CAPACITY = {0, 100000, 250000, 500000, 1000000, 2500000, 5000000};
    private static final int[] ENERGY_TRANSFER = {0, 2000, 5000, 10000, 20000, 40000, 80000};
    private static final int[] INTERVAL = {0, 200, 170, 140, 110, 85, 60};
    private static final int[] FLUID_PER_OPERATION = {0, 250, 350, 500, 750, 1000, 1500};
    private static final int[] ENERGY_PER_OPERATION = {0, 100, 180, 300, 450, 650, 900};

    private final int tier;
    private int progress;
    private boolean structureValid;
    private ResourceLocation selectedFluid = new ResourceLocation("minecraft", "water");

    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCapability;
    private final FluidTank tank;
    private final LazyOptional<IFluidHandler> fluidCapability;

    public VoidFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));

        this.energyStorage = new EnergyStorage(ENERGY_CAPACITY[this.tier], ENERGY_TRANSFER[this.tier], ENERGY_TRANSFER[this.tier]) {
            @Override public int receiveEnergy(int amount, boolean simulate) {
                int received = super.receiveEnergy(amount, simulate);
                if (!simulate && received > 0) setChanged();
                return received;
            }
            @Override public int extractEnergy(int amount, boolean simulate) {
                int extracted = super.extractEnergy(amount, simulate);
                if (!simulate && extracted > 0) setChanged();
                return extracted;
            }
        };
        this.energyCapability = LazyOptional.of(() -> energyStorage);

        this.tank = new FluidTank(capacityFor(this.tier)) {
            @Override protected void onContentsChanged() { setChanged(); }
        };
        this.fluidCapability = LazyOptional.of(() -> tank);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, VoidFluidMachineBlockEntity machine) {
        if (level.getGameTime() % 10L == 0L) {
            machine.structureValid = VoidFluidStructure.isValid(level, pos, machine.tier);
        }
        if (!machine.structureValid) return;
        if (++machine.progress < INTERVAL[machine.tier]) return;
        machine.progress = 0;

        int energyCost = ENERGY_PER_OPERATION[machine.tier];
        int amount = FLUID_PER_OPERATION[machine.tier];
        if (machine.energyStorage.getEnergyStored() < energyCost) return;

        Fluid fluid = machine.getSelectedFluid();
        if (fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource()) return;

        ResourceLocation selectedId = ForgeRegistries.FLUIDS.getKey(fluid);
        if (selectedId == null || !VoidFluidCatalog.canProduce(selectedId, machine.tier)) return;

        if (machine.tank.fill(new FluidStack(fluid, amount), IFluidHandler.FluidAction.SIMULATE) != amount) return;

        machine.energyStorage.extractEnergy(energyCost, false);
        machine.tank.fill(new FluidStack(fluid, amount), IFluidHandler.FluidAction.EXECUTE);
        machine.setChanged();
    }

    public Fluid getSelectedFluid() {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(selectedFluid);
        return fluid == null ? Fluids.WATER : fluid;
    }

    public ResourceLocation getSelectedFluidId() {
        return selectedFluid;
    }

    public boolean canProduceSelectedFluid() {
        return VoidFluidCatalog.canProduce(selectedFluid, tier);
    }

    public void setSelectedFluid(ResourceLocation id) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        if (fluid == null || fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource()) return;
        if (!VoidFluidCatalog.canProduce(id, tier)) return;
        if (!tank.isEmpty() && tank.getFluid().getFluid() != fluid) return;

        selectedFluid = id;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
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

    public int getTier() { return tier; }
    public int getProgress() { return progress; }
    public int getProgressPercent() { return Math.min(100, progress * 100 / Math.max(1, INTERVAL[tier])); }
    public int getEnergyStored() { return energyStorage.getEnergyStored(); }
    public int getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }
    public boolean isStructureValid() { return structureValid; }
    public FluidTank getTank() { return tank; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("menu.voidtech.void_fluid_machine");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ContainerData data = new ContainerData() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> energyStorage.getEnergyStored();
                    case 1 -> energyStorage.getMaxEnergyStored();
                    case 2 -> getProgressPercent();
                    case 3 -> structureValid ? 1 : 0;
                    case 4 -> tank.getFluidAmount();
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 5; }
        };
        return new VoidFluidMachineMenu(id, inv, tier, data, worldPosition);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.ENERGY) return energyCapability.cast();
        if (capability == ForgeCapabilities.FLUID_HANDLER) return fluidCapability.cast();
        return super.getCapability(capability, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Fluid", tank.writeToNBT(new CompoundTag()));
        tag.putInt("Progress", progress);
        tag.putBoolean("StructureValid", structureValid);
        tag.putString("SelectedFluid", selectedFluid.toString());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) energyStorage.deserializeNBT(tag.get("Energy"));
        if (tag.contains("Fluid")) tank.readFromNBT(tag.getCompound("Fluid"));
        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");
        if (tag.contains("SelectedFluid")) {
            ResourceLocation parsed = ResourceLocation.tryParse(tag.getString("SelectedFluid"));
            if (parsed != null) selectedFluid = parsed;
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
        fluidCapability.invalidate();
    }
}
