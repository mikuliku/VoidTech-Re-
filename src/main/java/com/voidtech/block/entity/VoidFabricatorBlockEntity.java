package com.voidtech.block.entity;

import com.voidtech.menu.VoidFabricatorMenu;
import com.voidtech.recipe.VoidFluidCraftingRecipe;
import com.voidtech.recipe.VoidRecipeHelper;
import com.voidtech.recipe.ModRecipeTypes;
import com.voidtech.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class VoidFabricatorBlockEntity extends BlockEntity implements MenuProvider {
    private static final int[] ENERGY_CAPACITY = {0, 100000, 250000, 500000, 1000000, 2500000, 5000000};
    private static final int[] ENERGY_TRANSFER = {0, 2000, 5000, 10000, 20000, 40000, 80000};
    private static final int[] TANK_CAPACITY = {0, 4000, 8000, 16000, 32000, 64000, 128000};
    private static final int[] PROCESS_TIME = {0, 200, 170, 140, 110, 85, 60};
    private static final int[] ENERGY_PER_TICK = {0, 50, 100, 200, 400, 800, 1600};

    private final int tier;
    private int progress;
    private ItemStack pendingResult = ItemStack.EMPTY;

    private final EnergyStorage energy;
    private final LazyOptional<IEnergyStorage> energyCap;

    private final ItemStackHandler input = new ItemStackHandler(9) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    private final ItemStackHandler output = new ItemStackHandler(1) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }
    };

    private final ItemStackHandler upgrades = new ItemStackHandler(4) {
        @Override protected void onContentsChanged(int slot) { setChanged(); }

        @Override public int getSlotLimit(int slot) {
            return slot < 2 ? 6 : 1;
        }

        @Override public boolean isItemValid(int slot, ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(ModItems.SPEED_UPGRADE.get());
                case 1 -> stack.is(ModItems.YIELD_UPGRADE.get());
                case 2 -> stack.is(ModItems.PRECISION_UPGRADE.get());
                case 3 -> stack.is(ModItems.DIMENSION_UPGRADE.get());
                default -> false;
            };
        }
    };

    private final FluidTank fluidTank;
    private final LazyOptional<IFluidHandler> fluidCap;

    private final LazyOptional<IItemHandler> itemCap =
            LazyOptional.of(() -> new CombinedInvWrapper(input, output, upgrades));

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> PROCESS_TIME[tier];
                case 2 -> energy.getEnergyStored();
                case 3 -> ENERGY_CAPACITY[tier];
                case 4 -> fluidTank.getFluidAmount();
                case 5 -> fluidTank.getCapacity();
                default -> 0;
            };
        }

        @Override public void set(int index, int value) {
            if (index == 0) progress = value;
        }

        @Override public int getCount() { return 6; }
    };

    public VoidFabricatorBlockEntity(
            BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));

        energy = new EnergyStorage(
                ENERGY_CAPACITY[this.tier],
                ENERGY_TRANSFER[this.tier],
                ENERGY_TRANSFER[this.tier]);

        energyCap = LazyOptional.of(() -> energy);

        fluidTank = new FluidTank(TANK_CAPACITY[this.tier]) {
            @Override protected void onContentsChanged() { setChanged(); }
        };
        fluidCap = LazyOptional.of(() -> fluidTank);
    }

    public static void serverTick(
            Level level, BlockPos pos, BlockState state, VoidFabricatorBlockEntity machine) {

        if (level.isClientSide) return;

        // 6-2A: recipe discovery, fluid requirement, energy consumption,
        // processing progress, item consumption and output insertion.

        if (machine.progress > 0) {
            if (machine.pendingResult.isEmpty()) {
                machine.progress = 0;
                machine.setChanged();
                return;
            }

            int energyCost = ENERGY_PER_TICK[machine.tier];
            if (machine.energy.getEnergyStored() < energyCost) {
                return;
            }

            machine.energy.extractEnergy(energyCost, false);
            machine.progress--;

            if (machine.progress <= 0) {
                ItemStack result = machine.pendingResult.copy();

                if (machine.canInsertResult(result)) {
                    machine.insertResult(result);
                    machine.pendingResult = ItemStack.EMPTY;
                    machine.setChanged();
                } else {
                    // Keep the result pending until the output slot has room.
                    machine.progress = 1;
                }
            } else {
                machine.setChanged();
            }
            return;
        }

        VoidFluidCraftingRecipe recipe = machine.findMatchingRecipe(level);
        if (recipe == null) return;

        int totalEnergy = PROCESS_TIME[machine.tier] * ENERGY_PER_TICK[machine.tier];
        if (machine.energy.getEnergyStored() < totalEnergy) return;

        ItemStack result = recipe.getResultItem(level.registryAccess());
        if (result.isEmpty() || !machine.canInsertResult(result)) return;

        // Consume the item ingredients once when processing starts.
        if (!machine.consumeIngredients(recipe)) return;

        // Consume the required void fluid once when processing starts.
        FluidStack drained = machine.fluidTank.drain(
                recipe.getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
        if (drained.getAmount() < recipe.getFluidAmount()) {
            // This should normally be impossible because findMatchingRecipe()
            // already checked the fluid requirement.
            return;
        }

        machine.pendingResult = result.copy();
        machine.progress = PROCESS_TIME[machine.tier];
        machine.setChanged();
    }

    @Nullable
    private VoidFluidCraftingRecipe findMatchingRecipe(Level level) {
        List<ItemStack> items = new ArrayList<>();
        for (int slot = 0; slot < input.getSlots(); slot++) {
            ItemStack stack = input.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }

        FluidStack suppliedFluid = fluidTank.getFluid();
        for (VoidFluidCraftingRecipe recipe :
                level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.FLUID_CRAFTING)) {
            if (VoidRecipeHelper.matches(recipe, items, suppliedFluid)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean consumeIngredients(VoidFluidCraftingRecipe recipe) {
        boolean[] used = new boolean[input.getSlots()];

        for (var ingredient : recipe.getIngredients()) {
            boolean found = false;
            for (int slot = 0; slot < input.getSlots(); slot++) {
                if (!used[slot] && ingredient.test(input.getStackInSlot(slot))) {
                    used[slot] = true;
                    input.extractItem(slot, 1, false);
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private boolean canInsertResult(ItemStack result) {
        ItemStack current = output.getStackInSlot(0);
        if (current.isEmpty()) return result.getCount() <= output.getSlotLimit(0);

        if (!ItemStack.isSameItemSameTags(current, result)) return false;

        return current.getCount() + result.getCount() <=
                Math.min(current.getMaxStackSize(), output.getSlotLimit(0));
    }

    private void insertResult(ItemStack result) {
        ItemStack current = output.getStackInSlot(0);
        if (current.isEmpty()) {
            output.setStackInSlot(0, result.copy());
        } else {
            current.grow(result.getCount());
            output.setStackInSlot(0, current);
        }
    }

    public int getTier() { return tier; }
    public ItemStackHandler getInput() { return input; }
    public ItemStackHandler getOutput() { return output; }
    public ItemStackHandler getUpgrades() { return upgrades; }
    public FluidTank getFluidTank() { return fluidTank; }
    public ContainerData getContainerData() { return data; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.voidtech.void_fabricator_t" + tier);
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new VoidFabricatorMenu(
                id, inv, tier, data, input, output, upgrades, getBlockPos());
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("Tier", tier);
        tag.putInt("Progress", progress);
        tag.put("Input", input.serializeNBT());
        tag.put("Output", output.serializeNBT());
        tag.put("Upgrades", upgrades.serializeNBT());
        tag.put("Fluid", fluidTank.writeToNBT(new CompoundTag()));
        tag.putInt("Energy", energy.getEnergyStored());

        if (!pendingResult.isEmpty()) {
            tag.put("PendingResult", pendingResult.save(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("Progress");
        input.deserializeNBT(tag.getCompound("Input"));
        output.deserializeNBT(tag.getCompound("Output"));
        upgrades.deserializeNBT(tag.getCompound("Upgrades"));
        fluidTank.readFromNBT(tag.getCompound("Fluid"));
        energy.receiveEnergy(tag.getInt("Energy"), false);

        if (tag.contains("PendingResult")) {
            pendingResult = ItemStack.of(tag.getCompound("PendingResult"));
        } else {
            pendingResult = ItemStack.EMPTY;
        }

        if (progress > 0 && pendingResult.isEmpty()) {
            progress = 0;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(
            Capability<T> capability, @Nullable net.minecraft.core.Direction side) {
        if (capability == ForgeCapabilities.ENERGY) return energyCap.cast();
        if (capability == ForgeCapabilities.FLUID_HANDLER) return fluidCap.cast();
        if (capability == ForgeCapabilities.ITEM_HANDLER) return itemCap.cast();
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
        fluidCap.invalidate();
        itemCap.invalidate();
    }
}
