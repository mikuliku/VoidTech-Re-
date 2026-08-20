package com.voidtech.block.entity;

import com.voidtech.fluid.VoidFluidCatalog;
import com.voidtech.menu.VoidFluidMachineMenu;
import com.voidtech.multiblock.VoidFluidStructure;
import com.voidtech.multiblock.VoidMiningStructure;
import com.voidtech.registry.ModItems;
import com.voidtech.upgrade.UpgradeEffects;
import com.voidtech.upgrade.UpgradeState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class VoidFluidMachineBlockEntity extends BlockEntity implements MenuProvider {
    private static final int[] ENERGY_CAPACITY = {0, 100000, 250000, 500000, 1000000, 2500000, 5000000};
    private static final int[] ENERGY_TRANSFER = {0, 2000, 5000, 10000, 20000, 40000, 80000};
    private static final int[] INTERVAL = {0, 200, 170, 140, 110, 85, 60};
    private static final int[] FLUID_PER_OPERATION = {0, 250, 350, 500, 750, 1000, 1500};
    private static final int[] ENERGY_PER_OPERATION = {0, 100, 180, 300, 450, 650, 900};
    private static final int FLUID_TRANSFER_PER_INTERFACE = 4000;

    private final int tier;
    private int progress;
    private boolean structureValid;
    private ResourceLocation selectedFluid = new ResourceLocation("minecraft", "water");
    private ResourceLocation targetDimension;

    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCapability;
    private final FluidTank tank;
    private final LazyOptional<IFluidHandler> fluidCapability;

    private final ItemStackHandler upgrades = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }

        @Override public int getSlotLimit(int slot) { return 6; }

        @Override public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            return slot == 0 && stack.is(ModItems.SPEED_UPGRADE.get())
                    || slot == 1 && stack.is(ModItems.YIELD_UPGRADE.get())
                    || slot == 2 && stack.is(ModItems.PRECISION_UPGRADE.get())
                    || slot == 3 && stack.is(ModItems.DIMENSION_UPGRADE.get());
        }
    };

    private final LazyOptional<IItemHandler> upgradeCapability = LazyOptional.of(() -> upgrades);

    public VoidFluidMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));

        energyStorage = new EnergyStorage(
                ENERGY_CAPACITY[this.tier],
                ENERGY_TRANSFER[this.tier],
                ENERGY_TRANSFER[this.tier]
        ) {
            @Override public int receiveEnergy(int amount, boolean simulate) {
                int result = super.receiveEnergy(amount, simulate);
                if (!simulate && result > 0) setChanged();
                return result;
            }
            @Override public int extractEnergy(int amount, boolean simulate) {
                int result = super.extractEnergy(amount, simulate);
                if (!simulate && result > 0) setChanged();
                return result;
            }
        };
        energyCapability = LazyOptional.of(() -> energyStorage);

        tank = new FluidTank(capacityFor(this.tier)) {
            @Override protected void onContentsChanged() { setChanged(); }
        };
        fluidCapability = LazyOptional.of(() -> tank);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                   VoidFluidMachineBlockEntity machine) {
        if (level.getGameTime() % 10L == 0L) {
            machine.structureValid = VoidFluidStructure.isValid(level, pos, machine.tier);
        }
        if (!machine.structureValid) return;

        machine.pushFluidToInterfaces(level, pos);

        if (++machine.progress < machine.getEffectiveFluidInterval()) return;
        machine.progress = 0;

        int energyCost = ENERGY_PER_OPERATION[machine.tier];
        int amount = UpgradeEffects.outputAmount(
                FLUID_PER_OPERATION[machine.tier],
                machine.getYieldUpgradeLevel());

        if (machine.energyStorage.getEnergyStored() < energyCost) return;

        Fluid fluid = machine.getSelectedFluid();
        if (fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource()) return;

        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
        if (id == null || !VoidFluidCatalog.canProduce(id, machine.tier)) return;

        if (machine.hasDimensionUpgrade() && machine.getTargetLevel() == null) return;

        if (machine.tank.fill(new FluidStack(fluid, amount),
                IFluidHandler.FluidAction.SIMULATE) != amount) return;

        machine.energyStorage.extractEnergy(energyCost, false);
        machine.tank.fill(new FluidStack(fluid, amount),
                IFluidHandler.FluidAction.EXECUTE);
        machine.setChanged();
        machine.pushFluidToInterfaces(level, pos);
    }

    private void pushFluidToInterfaces(Level level, BlockPos controllerPos) {
        if (tank.isEmpty()) return;

        int radius = VoidMiningStructure.radiusFor(tier);
        int height = VoidMiningStructure.heightFor(tier);

        for (int y = 0; y < height && !tank.isEmpty(); y++) {
            int layerRadius = radius - y;
            if (layerRadius < 1) continue;

            for (int x = -layerRadius; x <= layerRadius && !tank.isEmpty(); x++) {
                for (int z = -layerRadius; z <= layerRadius && !tank.isEmpty(); z++) {
                    if (Math.abs(x) != layerRadius && Math.abs(z) != layerRadius) continue;

                    BlockEntity target = level.getBlockEntity(controllerPos.offset(x, y + 1, z));
                    if (!(target instanceof VoidFluidInterfaceBlockEntity interfaceEntity)) continue;
                    if (interfaceEntity.getTier() > tier) continue;

                    FluidStack toSend = tank.getFluid().copy();
                    toSend.setAmount(Math.min(toSend.getAmount(), FLUID_TRANSFER_PER_INTERFACE));

                    IFluidHandler handler = interfaceEntity.getTank();
                    int accepted = handler.fill(toSend, IFluidHandler.FluidAction.SIMULATE);
                    if (accepted <= 0) continue;

                    FluidStack extracted = tank.drain(
                            Math.min(accepted, toSend.getAmount()),
                            IFluidHandler.FluidAction.EXECUTE);

                    if (!extracted.isEmpty()) handler.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    public boolean hasDimensionUpgrade() {
        return UpgradeState.dimension(upgrades) > 0;
    }

    public void setTargetDimension(ResourceLocation id) {
        if (!hasDimensionUpgrade() || id == null || level == null || level.isClientSide()) return;

        MinecraftServer server = level.getServer();
        if (server == null) return;

        ResourceKey<Level> key = ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION, id);
        if (server.getLevel(key) == null) return;

        targetDimension = id;
        setChanged();
    }

    public ResourceLocation getTargetDimension() {
        if (!hasDimensionUpgrade() || targetDimension == null) {
            return level == null ? new ResourceLocation("minecraft", "overworld")
                    : level.dimension().location();
        }
        return targetDimension;
    }

    public ServerLevel getTargetLevel() {
        if (level == null || level.isClientSide()) return null;
        MinecraftServer server = level.getServer();
        if (server == null) return null;

        ResourceLocation id = getTargetDimension();
        ResourceKey<Level> key = ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION, id);
        return server.getLevel(key);
    }

    public Fluid getSelectedFluid() {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(selectedFluid);
        return fluid == null ? Fluids.WATER : fluid;
    }

    public ResourceLocation getSelectedFluidId() { return selectedFluid; }

    public void setSelectedFluid(ResourceLocation id) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(id);
        if (fluid == null || fluid == Fluids.EMPTY || !fluid.defaultFluidState().isSource()) return;
        if (!VoidFluidCatalog.canProduce(id, tier)) return;
        if (!tank.isEmpty() && tank.getFluid().getFluid() != fluid) return;

        selectedFluid = id;
        setChanged();
    }

    public int getUpgradeLevel(int slot) { return UpgradeState.level(upgrades, slot); }
    public int getSpeedUpgradeLevel() { return UpgradeState.speed(upgrades); }
    public int getYieldUpgradeLevel() { return UpgradeState.yield(upgrades); }
    public int getPrecisionUpgradeLevel() { return UpgradeState.precision(upgrades); }

    public int getEffectiveFluidInterval() {
        return UpgradeEffects.fluidInterval(INTERVAL[tier], getSpeedUpgradeLevel());
    }

    public int getTier() { return tier; }
    public int getProgressPercent() {
        return Math.min(100, progress * 100 / Math.max(1, getEffectiveFluidInterval()));
    }
    public int getEnergyStored() { return energyStorage.getEnergyStored(); }
    public int getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }
    public boolean isStructureValid() { return structureValid; }
    public FluidTank getTank() { return tank; }
    public ItemStackHandler getUpgradeInventory() { return upgrades; }

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

    @Override public Component getDisplayName() {
        return Component.translatable("menu.voidtech.void_fluid_machine");
    }

    @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        ContainerData data = new ContainerData() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> energyStorage.getEnergyStored();
                    case 1 -> energyStorage.getMaxEnergyStored();
                    case 2 -> getProgressPercent();
                    case 3 -> structureValid ? 1 : 0;
                    case 4 -> tank.getFluidAmount();
                    case 5 -> hasDimensionUpgrade() ? 1 : 0;
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) {}
            @Override public int getCount() { return 6; }
        };

        return new VoidFluidMachineMenu(id, inv, tier, data, upgrades, worldPosition);
    }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> capability,
            @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.ENERGY) return energyCapability.cast();
        if (capability == ForgeCapabilities.FLUID_HANDLER) return fluidCapability.cast();
        if (capability == ForgeCapabilities.ITEM_HANDLER) return upgradeCapability.cast();
        return super.getCapability(capability, side);
    }

    @Override protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("Fluid", tank.writeToNBT(new CompoundTag()));
        tag.put("Upgrades", upgrades.serializeNBT());
        tag.putInt("Progress", progress);
        tag.putBoolean("StructureValid", structureValid);
        tag.putString("SelectedFluid", selectedFluid.toString());
        if (targetDimension != null) tag.putString("TargetDimension", targetDimension.toString());
    }

    @Override public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy")) energyStorage.deserializeNBT(tag.get("Energy"));
        if (tag.contains("Fluid")) tank.readFromNBT(tag.getCompound("Fluid"));
        if (tag.contains("Upgrades")) upgrades.deserializeNBT(tag.getCompound("Upgrades"));

        progress = tag.getInt("Progress");
        structureValid = tag.getBoolean("StructureValid");

        if (tag.contains("SelectedFluid")) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("SelectedFluid"));
            if (id != null) selectedFluid = id;
        }
        if (tag.contains("TargetDimension")) {
            targetDimension = ResourceLocation.tryParse(tag.getString("TargetDimension"));
        }
    }

    @Override public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
        fluidCapability.invalidate();
        upgradeCapability.invalidate();
    }
}
