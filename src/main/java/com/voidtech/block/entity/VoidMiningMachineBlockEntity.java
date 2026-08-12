package com.voidtech.block.entity;

import com.voidtech.menu.VoidMiningMachineMenu;
import com.voidtech.multiblock.VoidMiningStructure;
import com.voidtech.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VoidMiningMachineBlockEntity extends BlockEntity implements MenuProvider {

    private static final int[] ENERGY_CAPACITY = {
            0, 100_000, 250_000, 500_000, 1_000_000, 2_500_000, 5_000_000
    };
    private static final int[] ENERGY_TRANSFER = {
            0, 2_000, 5_000, 10_000, 20_000, 40_000, 80_000
    };
    private static final int[] MINING_INTERVAL = {
            0, 200, 170, 140, 110, 85, 60
    };
    private static final int[] MINING_ENERGY = {
            0, 100, 180, 300, 450, 650, 900
    };

    private final int tier;
    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCapability;

    private final ItemStackHandler outputInventory = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler upgradeInventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isValidUpgrade(slot, stack);
        }
    };

    private final LazyOptional<IItemHandler> itemCapability =
            LazyOptional.of(() -> new IItemHandler() {
                @Override
                public int getSlots() {
                    return outputInventory.getSlots();
                }

                @Override
                public ItemStack getStackInSlot(int slot) {
                    return outputInventory.getStackInSlot(slot);
                }

                @Override
                public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                    return outputInventory.insertItem(slot, stack, simulate);
                }

                @Override
                public ItemStack extractItem(int slot, int amount, boolean simulate) {
                    return outputInventory.extractItem(slot, amount, simulate);
                }

                @Override
                public int getSlotLimit(int slot) {
                    return outputInventory.getSlotLimit(slot);
                }

                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                    return outputInventory.isItemValid(slot, stack);
                }
            });

    private boolean structureValid;
    private int miningProgress;
    private final Random random = new Random();

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

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            VoidMiningMachineBlockEntity machine
    ) {
        if (level.getGameTime() % 10L == 0L) {
            boolean valid = VoidMiningStructure.isValid(level, pos, machine.tier);
            if (valid != machine.structureValid) {
                machine.structureValid = valid;
                machine.setChanged();
            }
        }

        if (!machine.structureValid) {
            machine.miningProgress = 0;
            return;
        }

        machine.miningProgress++;
        if (machine.miningProgress < machine.getEffectiveMiningInterval()) return;
        machine.miningProgress = 0;

        int energyCost = machine.getEffectiveEnergyCost();
        if (machine.energyStorage.getEnergyStored() < energyCost) return;

        ItemStack result = machine.createMiningResult();
        if (result.isEmpty() || !machine.canInsert(result)) return;

        machine.energyStorage.extractEnergy(energyCost, false);
        machine.insertResult(result);
        machine.setChanged();
    }

    private ItemStack createMiningResult() {
        List<Block> ores = new ArrayList<>();
        for (Block block : net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValues()) {
            if (block.defaultBlockState().is(Tags.Blocks.ORES)
                    && block.asItem() != net.minecraft.world.item.Items.AIR) {
                ores.add(block);
            }
        }

        if (ores.isEmpty()) return ItemStack.EMPTY;

        Block selected = ores.get(random.nextInt(ores.size()));
        int count = 1;

        int yieldLevel = getUpgradeLevel(1);
        if (yieldLevel > 0) {
            count += yieldLevel;
            if (random.nextFloat() < 0.25f * yieldLevel) count++;
        }

        return new ItemStack(selected.asItem(), count);
    }

    private boolean canInsert(ItemStack stack) {
        return ItemHandlerHelper.insertItem(outputInventory, stack.copy(), true).isEmpty();
    }

    private void insertResult(ItemStack stack) {
        ItemHandlerHelper.insertItem(outputInventory, stack, false);
    }

    private static boolean isValidUpgrade(int slot, ItemStack stack) {
        if (stack.isEmpty()) return false;
        return switch (slot) {
            case 0 -> stack.is(ModItems.SPEED_UPGRADE.get());
            case 1 -> stack.is(ModItems.YIELD_UPGRADE.get());
            case 2 -> stack.is(ModItems.PRECISION_UPGRADE.get());
            default -> false;
        };
    }

    public int getUpgradeLevel(int slot) {
        return upgradeInventory.getStackInSlot(slot).isEmpty() ? 0 : 1;
    }

    public int getSpeedUpgradeLevel() {
        return getUpgradeLevel(0);
    }

    public int getYieldUpgradeLevel() {
        return getUpgradeLevel(1);
    }

    public int getPrecisionUpgradeLevel() {
        return getUpgradeLevel(2);
    }

    public int getEffectiveMiningInterval() {
        int base = MINING_INTERVAL[tier];
        int speed = getSpeedUpgradeLevel();
        return Math.max(10, base - speed * Math.max(5, base / 10));
    }

    public int getEffectiveEnergyCost() {
        return MINING_ENERGY[tier];
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

    public int getMiningProgress() {
        return miningProgress;
    }

    public int getMiningInterval() {
        return getEffectiveMiningInterval();
    }

    public boolean isStructureValid() {
        return structureValid;
    }

    public ItemStackHandler getOutputInventory() {
        return outputInventory;
    }

    public ItemStackHandler getUpgradeInventory() {
        return upgradeInventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.voidtech.void_mining_machine_t" + tier);
    }

    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory,
            Player player
    ) {
        ContainerData data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> getEnergyStored();
                    case 1 -> getMaxEnergyStored();
                    case 2 -> isStructureValid() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        return new VoidMiningMachineMenu(
                containerId, inventory, tier, data, outputInventory, upgradeInventory);
    }

    @Override
    public <T> LazyOptional<T> getCapability(
            Capability<T> capability,
            @Nullable net.minecraft.core.Direction side
    ) {
        if (capability == ForgeCapabilities.ENERGY) return energyCapability.cast();
        if (capability == ForgeCapabilities.ITEM_HANDLER) return itemCapability.cast();
        return super.getCapability(capability, side);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("OutputInventory", outputInventory.serializeNBT());
        tag.put("UpgradeInventory", upgradeInventory.serializeNBT());
        tag.putBoolean("StructureValid", structureValid);
        tag.putInt("MiningProgress", miningProgress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("Energy")) energyStorage.deserializeNBT(tag.get("Energy"));
        if (tag.contains("OutputInventory")) {
            outputInventory.deserializeNBT(tag.getCompound("OutputInventory"));
        }
        if (tag.contains("UpgradeInventory")) {
            upgradeInventory.deserializeNBT(tag.getCompound("UpgradeInventory"));
        }

        structureValid = tag.getBoolean("StructureValid");
        miningProgress = Math.max(0, tag.getInt("MiningProgress"));
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCapability.invalidate();
        itemCapability.invalidate();
    }
}
