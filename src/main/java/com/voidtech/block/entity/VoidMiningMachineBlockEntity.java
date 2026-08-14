package com.voidtech.block.entity;

import com.voidtech.menu.VoidMiningMachineMenu;
import com.voidtech.multiblock.VoidMiningStructure;
import com.voidtech.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class VoidMiningMachineBlockEntity extends BlockEntity implements MenuProvider {
    private static final int[] ENERGY_CAPACITY = {0, 100000, 250000, 500000, 1000000, 2500000, 5000000};
    private static final int[] ENERGY_TRANSFER = {0, 2000, 5000, 10000, 20000, 40000, 80000};
    private static final int[] INTERVAL = {0, 200, 170, 140, 110, 85, 60};
    private static final int[] COST = {0, 100, 180, 300, 450, 650, 900};

    private final int tier;
    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCap;

    private final ItemStackHandler output = new ItemStackHandler(9) {
        protected void onContentsChanged(int s) {
            setChanged();
        }
    };

    private final ItemStackHandler upgrades = new ItemStackHandler(4) {
        protected void onContentsChanged(int s) {
            setChanged();
        }

        public int getSlotLimit(int s) {
            return 1;
        }

        public boolean isItemValid(int s, ItemStack st) {
            return switch (s) {
                case 0 -> st.is(ModItems.SPEED_UPGRADE.get());
                case 1 -> st.is(ModItems.YIELD_UPGRADE.get());
                case 2 -> st.is(ModItems.PRECISION_UPGRADE.get());
                case 3 -> st.is(ModItems.DIMENSION_UPGRADE.get());
                default -> false;
            };
        }
    };

    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> output);

    private boolean structureValid;
    private int progress;
    private ResourceLocation miningDimension;

    /*
     * The ore pool is deliberately cached per machine/dimension.
     * It is built from real blocks in the selected ServerLevel instead of
     * using one global list for every dimension.
     */
    private ResourceLocation cachedOrePoolDimension;
    private List<Block> cachedOrePool = List.of();

    private final Random random = new Random();

    public VoidMiningMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int tier) {
        super(type, pos, state);
        this.tier = Math.max(1, Math.min(6, tier));

        energyStorage = new EnergyStorage(
                ENERGY_CAPACITY[this.tier],
                ENERGY_TRANSFER[this.tier],
                ENERGY_TRANSFER[this.tier]
        ) {
            public int receiveEnergy(int a, boolean s) {
                int r = super.receiveEnergy(a, s);
                if (!s && r > 0) setChanged();
                return r;
            }

            public int extractEnergy(int a, boolean s) {
                int r = super.extractEnergy(a, s);
                if (!s && r > 0) setChanged();
                return r;
            }
        };

        energyCap = LazyOptional.of(() -> energyStorage);
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            VoidMiningMachineBlockEntity machine
    ) {
        if (level.getGameTime() % 10L == 0L) {
            machine.structureValid =
                    VoidMiningStructure.isValid(level, pos, machine.tier);
        }

        if (!machine.structureValid) return;

        if (++machine.progress < machine.getEffectiveMiningInterval()) return;
        machine.progress = 0;

        int cost = machine.getEffectiveEnergyCost();
        if (machine.energyStorage.getEnergyStored() < cost) return;

        ItemStack result = machine.createMiningResult();
        if (result.isEmpty() || !machine.canInsert(result)) return;

        machine.energyStorage.extractEnergy(cost, false);
        machine.insertResult(result);
        machine.setChanged();
    }

    private ItemStack createMiningResult() {
        ServerLevel targetLevel = getMiningLevel();
        if (targetLevel == null) return ItemStack.EMPTY;

        List<Block> ores = getOrePool(targetLevel);
        if (ores.isEmpty()) return ItemStack.EMPTY;

        Block selected = ores.get(random.nextInt(ores.size()));
        int count = 1;

        int yield = getUpgradeLevel(1);
        if (yield > 0) {
            count += yield;
            if (random.nextFloat() < 0.25f * yield) count++;
        }

        return new ItemStack(selected.asItem(), count);
    }

    /**
     * Returns the actual server dimension being mined.
     * Without the dimension upgrade, this is always the machine's own dimension.
     * With the upgrade installed, it is the dimension selected by the player.
     */
    @Nullable
    private ServerLevel getMiningLevel() {
        if (level == null || !level.isClientSide()) return null;

        if (!(level.getServer() instanceof net.minecraft.server.MinecraftServer server)) {
            return null;
        }

        ResourceLocation id = getMiningDimension();
        ResourceKey<Level> key =
                ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, id);

        return server.getLevel(key);
    }

    /**
     * Builds a dimension-specific ore pool by sampling the real blocks around
     * the machine's X/Z coordinate in the target dimension.
     *
     * This keeps the system compatible with vanilla and modded ores without
     * maintaining a hard-coded list of every possible mod dimension.
     */
    private List<Block> getOrePool(ServerLevel targetLevel) {
        ResourceLocation dimension = targetLevel.dimension().location();

        if (dimension.equals(cachedOrePoolDimension) && !cachedOrePool.isEmpty()) {
            return cachedOrePool;
        }

        Set<Block> found = new LinkedHashSet<>();

        int centerChunkX = pos.getX() >> 4;
        int centerChunkZ = pos.getZ() >> 4;

        int minY = Math.max(targetLevel.getMinBuildHeight(), -64);
        int maxY = Math.min(targetLevel.getMaxBuildHeight(), 320);

        /*
         * Sample a 3x3 chunk area. Four-block spacing keeps the first scan
         * reasonably cheap while still finding common ore veins.
         */
        for (int chunkX = centerChunkX - 1; chunkX <= centerChunkX + 1; chunkX++) {
            for (int chunkZ = centerChunkZ - 1; chunkZ <= centerChunkZ + 1; chunkZ++) {
                int baseX = chunkX << 4;
                int baseZ = chunkZ << 4;

                for (int x = baseX; x < baseX + 16; x += 4) {
                    for (int z = baseZ; z < baseZ + 16; z += 4) {
                        for (int y = minY; y < maxY; y += 4) {
                            BlockState state =
                                    targetLevel.getBlockState(new BlockPos(x, y, z));

                            Block block = state.getBlock();

                            if (block.asItem() != net.minecraft.world.item.Items.AIR
                                    && state.is(Tags.Blocks.ORES)) {
                                found.add(block);
                            }
                        }
                    }
                }
            }
        }

        /*
         * A custom dimension can have unusual generation around the sampled
         * location. If nothing was found, fall back to the registry's ore
         * blocks rather than making the machine completely unusable.
         */
        if (found.isEmpty()) {
            for (Block block :
                    net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValues()) {
                if (block.defaultBlockState().is(Tags.Blocks.ORES)
                        && block.asItem() != net.minecraft.world.item.Items.AIR) {
                    found.add(block);
                }
            }
        }

        cachedOrePoolDimension = dimension;
        cachedOrePool = new ArrayList<>(found);
        return cachedOrePool;
    }

    private boolean canInsert(ItemStack stack) {
        return ItemHandlerHelper.insertItem(output, stack.copy(), true).isEmpty();
    }

    private void insertResult(ItemStack stack) {
        ItemHandlerHelper.insertItem(output, stack, false);
    }

    public int getUpgradeLevel(int slot) {
        return upgrades.getStackInSlot(slot).isEmpty() ? 0 : 1;
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

    public boolean hasDimensionUpgrade() {
        return getUpgradeLevel(3) > 0;
    }

    public ResourceLocation getMiningDimension() {
        return miningDimension == null && level != null
                ? level.dimension().location()
                : miningDimension;
    }

    public void setMiningDimension(ResourceLocation id) {
        if (!hasDimensionUpgrade()) return;

        miningDimension = id;

        // Force the next mining operation to rebuild the pool for the new dimension.
        cachedOrePoolDimension = null;
        cachedOrePool = List.of();

        setChanged();
    }

    public int getEffectiveMiningInterval() {
        int base = INTERVAL[tier];
        return Math.max(
                10,
                base - getSpeedUpgradeLevel() * Math.max(5, base / 10)
        );
    }

    public int getEffectiveEnergyCost() {
        return COST[tier];
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

    public boolean isStructureValid() {
        return structureValid;
    }

    public ItemStackHandler getOutputInventory() {
        return output;
    }

    public ItemStackHandler getUpgradeInventory() {
        return upgrades;
    }

    public Component getDisplayName() {
        return Component.translatable(
                "block.voidtech.void_mining_machine_t" + tier
        );
    }

    public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) {
        ContainerData data = new ContainerData() {
            public int get(int i) {
                return switch (i) {
                    case 0 -> getEnergyStored();
                    case 1 -> getMaxEnergyStored();
                    case 2 -> isStructureValid() ? 1 : 0;
                    case 3 -> hasDimensionUpgrade() ? 1 : 0;
                    default -> 0;
                };
            }

            public void set(int i, int v) {
            }

            public int getCount() {
                return 4;
            }
        };

        return new VoidMiningMachineMenu(
                id,
                inv,
                tier,
                data,
                output,
                upgrades,
                getBlockPos()
        );
    }

    public <T> LazyOptional<T> getCapability(
            Capability<T> capability,
            @Nullable net.minecraft.core.Direction side
    ) {
        if (capability == ForgeCapabilities.ENERGY) {
            return energyCap.cast();
        }

        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }

        return super.getCapability(capability, side);
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("OutputInventory", output.serializeNBT());
        tag.put("UpgradeInventory", upgrades.serializeNBT());
        tag.putBoolean("StructureValid", structureValid);
        tag.putInt("MiningProgress", progress);

        if (miningDimension != null) {
            tag.putString("MiningDimension", miningDimension.toString());
        }
    }

    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("Energy")) {
            energyStorage.deserializeNBT(tag.get("Energy"));
        }

        if (tag.contains("OutputInventory")) {
            output.deserializeNBT(tag.getCompound("OutputInventory"));
        }

        if (tag.contains("UpgradeInventory")) {
            upgrades.deserializeNBT(tag.getCompound("UpgradeInventory"));
        }

        structureValid = tag.getBoolean("StructureValid");
        progress = tag.getInt("MiningProgress");

        if (tag.contains("MiningDimension")) {
            miningDimension =
                    ResourceLocation.tryParse(tag.getString("MiningDimension"));
        }

        cachedOrePoolDimension = null;
        cachedOrePool = List.of();
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
        itemCap.invalidate();
    }
}
