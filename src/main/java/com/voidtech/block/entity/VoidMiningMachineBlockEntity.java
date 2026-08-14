package com.voidtech.block.entity;

import com.voidtech.menu.VoidMiningMachineMenu;
import com.voidtech.multiblock.VoidMiningStructure;
import com.voidtech.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
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
import java.util.List;
import java.util.Random;

public class VoidMiningMachineBlockEntity extends BlockEntity implements MenuProvider {
    private static final int[] ENERGY_CAPACITY={0,100000,250000,500000,1000000,2500000,5000000};
    private static final int[] ENERGY_TRANSFER={0,2000,5000,10000,20000,40000,80000};
    private static final int[] INTERVAL={0,200,170,140,110,85,60};
    private static final int[] COST={0,100,180,300,450,650,900};

    private final int tier;
    private final EnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> energyCap;
    private final ItemStackHandler output=new ItemStackHandler(9){
        protected void onContentsChanged(int s){setChanged();}
    };
    private final ItemStackHandler upgrades=new ItemStackHandler(4){
        protected void onContentsChanged(int s){setChanged();}
        public int getSlotLimit(int s){return 1;}
        public boolean isItemValid(int s,ItemStack st){return switch(s){
            case 0->st.is(ModItems.SPEED_UPGRADE.get());
            case 1->st.is(ModItems.YIELD_UPGRADE.get());
            case 2->st.is(ModItems.PRECISION_UPGRADE.get());
            case 3->st.is(ModItems.DIMENSION_UPGRADE.get());
            default->false;
        };}
    };
    private final LazyOptional<IItemHandler> itemCap=LazyOptional.of(()->output);

    private boolean structureValid;
    private int progress;
    private ResourceLocation miningDimension;
    private ResourceLocation cachedOreDimension;
    private List<Block> cachedDimensionOres=List.of();
    private final Random random=new Random();

    public VoidMiningMachineBlockEntity(BlockEntityType<?> type,BlockPos pos,BlockState state,int tier){
        super(type,pos,state);
        this.tier=Math.max(1,Math.min(6,tier));
        energyStorage=new EnergyStorage(ENERGY_CAPACITY[this.tier],
                ENERGY_TRANSFER[this.tier],ENERGY_TRANSFER[this.tier]){
            public int receiveEnergy(int a,boolean s){int r=super.receiveEnergy(a,s);if(!s&&r>0)setChanged();return r;}
            public int extractEnergy(int a,boolean s){int r=super.extractEnergy(a,s);if(!s&&r>0)setChanged();return r;}
        };
        energyCap=LazyOptional.of(()->energyStorage);
    }

    public static void serverTick(Level level,BlockPos pos,BlockState state,VoidMiningMachineBlockEntity m){
        if(level.getGameTime()%10L==0L)m.structureValid=VoidMiningStructure.isValid(level,pos,m.tier);
        if(!m.structureValid)return;
        if(++m.progress<m.getEffectiveMiningInterval())return;
        m.progress=0;

        int cost=m.getEffectiveEnergyCost();
        if(m.energyStorage.getEnergyStored()<cost)return;

        ItemStack result=m.createMiningResult(level);
        if(result.isEmpty()||!m.canInsert(result))return;

        m.energyStorage.extractEnergy(cost,false);
        m.insertResult(result);
        m.setChanged();
    }

    private ItemStack createMiningResult(Level machineLevel){
        List<Block> ores=getMiningOres(machineLevel);
        if(ores.isEmpty())return ItemStack.EMPTY;

        Block selected=ores.get(random.nextInt(ores.size()));
        int count=1;
        int y=getUpgradeLevel(1);
        if(y>0){
            count+=y;
            if(random.nextFloat()<.25f*y)count++;
        }
        return new ItemStack(selected.asItem(),count);
    }

    /**
     * Builds the ore pool from the selected dimension itself.
     *
     * The target dimension is resolved on the server, and a small set of
     * generated chunks around that dimension's spawn is inspected for blocks
     * carrying the Forge ORES tag. This makes the pool dimension-sensitive
     * instead of using one global list of every ore registered by every mod.
     *
     * If the selected dimension has no loaded/generated ore in the sampled
     * area, we fall back to the global ore registry so a custom dimension
     * cannot make the machine permanently output nothing.
     */
    private List<Block> getMiningOres(Level machineLevel){
        ResourceLocation targetId=getMiningDimension();
        if(targetId==null)return List.of();

        if(targetId.equals(cachedOreDimension)&&!cachedDimensionOres.isEmpty()){
            return cachedDimensionOres;
        }

        if(!(machineLevel instanceof ServerLevel currentServer)){
            return getGlobalOres();
        }

        ResourceKey<Level> key=ResourceKey.create(Registries.DIMENSION,targetId);
        ServerLevel target=currentServer.getServer().getLevel(key);
        if(target==null)return List.of();

        List<Block> found=new ArrayList<>();
        BlockPos center=target.getSharedSpawnPos();

        // Sample a 3x3 chunk area around the target dimension's spawn.
        int cx=center.getX()>>4;
        int cz=center.getZ()>>4;
        int minY=target.getMinBuildHeight();
        int maxY=target.getMaxBuildHeight();

        for(int dx=-1;dx<=1;dx++){
            for(int dz=-1;dz<=1;dz++){
                int baseX=(cx+dx)<<4;
                int baseZ=(cz+dz)<<4;
                for(int x=0;x<16;x++){
                    for(int z=0;z<16;z++){
                        for(int y=minY;y<maxY;y++){
                            BlockState state=target.getBlockState(new BlockPos(baseX+x,y,baseZ+z));
                            Block block=state.getBlock();
                            if(state.is(Tags.Blocks.ORES)
                                    &&block.asItem()!=net.minecraft.world.item.Items.AIR
                                    &&!found.contains(block)){
                                found.add(block);
                            }
                        }
                    }
                }
            }
        }

        if(found.isEmpty()){
            found.addAll(getGlobalOres());
        }

        cachedOreDimension=targetId;
        cachedDimensionOres=List.copyOf(found);
        return cachedDimensionOres;
    }

    private List<Block> getGlobalOres(){
        List<Block> ores=new ArrayList<>();
        for(Block b:net.minecraftforge.registries.ForgeRegistries.BLOCKS.getValues()){
            if(b.defaultBlockState().is(Tags.Blocks.ORES)
                    &&b.asItem()!=net.minecraft.world.item.Items.AIR){
                ores.add(b);
            }
        }
        return ores;
    }

    private boolean canInsert(ItemStack s){return ItemHandlerHelper.insertItem(output,s.copy(),true).isEmpty();}
    private void insertResult(ItemStack s){ItemHandlerHelper.insertItem(output,s,false);}

    public int getUpgradeLevel(int slot){return upgrades.getStackInSlot(slot).isEmpty()?0:1;}
    public int getSpeedUpgradeLevel(){return getUpgradeLevel(0);}
    public int getYieldUpgradeLevel(){return getUpgradeLevel(1);}
    public int getPrecisionUpgradeLevel(){return getUpgradeLevel(2);}
    public boolean hasDimensionUpgrade(){return getUpgradeLevel(3)>0;}

    public ResourceLocation getMiningDimension(){
        return miningDimension==null&&level!=null?level.dimension().location():miningDimension;
    }

    public void setMiningDimension(ResourceLocation id){
        if(!hasDimensionUpgrade())return;
        miningDimension=id;
        cachedOreDimension=null;
        cachedDimensionOres=List.of();
        setChanged();
    }

    public int getEffectiveMiningInterval(){
        int b=INTERVAL[tier];
        return Math.max(10,b-getSpeedUpgradeLevel()*Math.max(5,b/10));
    }

    public int getEffectiveEnergyCost(){return COST[tier];}
    public int getTier(){return tier;}
    public int getEnergyStored(){return energyStorage.getEnergyStored();}
    public int getMaxEnergyStored(){return energyStorage.getMaxEnergyStored();}
    public boolean isStructureValid(){return structureValid;}
    public ItemStackHandler getOutputInventory(){return output;}
    public ItemStackHandler getUpgradeInventory(){return upgrades;}

    public Component getDisplayName(){
        return Component.translatable("block.voidtech.void_mining_machine_t"+tier);
    }

    public AbstractContainerMenu createMenu(int id,Inventory inv,Player p){
        ContainerData data=new ContainerData(){
            public int get(int i){return switch(i){
                case 0->getEnergyStored();
                case 1->getMaxEnergyStored();
                case 2->isStructureValid()?1:0;
                case 3->hasDimensionUpgrade()?1:0;
                default->0;
            };}
            public void set(int i,int v){}
            public int getCount(){return 4;}
        };
        return new VoidMiningMachineMenu(id,inv,tier,data,output,upgrades,getBlockPos());
    }

    public <T> LazyOptional<T> getCapability(Capability<T> c,@Nullable net.minecraft.core.Direction side){
        if(c==ForgeCapabilities.ENERGY)return energyCap.cast();
        if(c==ForgeCapabilities.ITEM_HANDLER)return itemCap.cast();
        return super.getCapability(c,side);
    }

    protected void saveAdditional(CompoundTag t){
        super.saveAdditional(t);
        t.put("Energy",energyStorage.serializeNBT());
        t.put("OutputInventory",output.serializeNBT());
        t.put("UpgradeInventory",upgrades.serializeNBT());
        t.putBoolean("StructureValid",structureValid);
        t.putInt("MiningProgress",progress);
        if(miningDimension!=null)t.putString("MiningDimension",miningDimension.toString());
    }

    public void load(CompoundTag t){
        super.load(t);
        if(t.contains("Energy"))energyStorage.deserializeNBT(t.get("Energy"));
        if(t.contains("OutputInventory"))output.deserializeNBT(t.getCompound("OutputInventory"));
        if(t.contains("UpgradeInventory"))upgrades.deserializeNBT(t.getCompound("UpgradeInventory"));
        structureValid=t.getBoolean("StructureValid");
        progress=t.getInt("MiningProgress");
        if(t.contains("MiningDimension"))miningDimension=ResourceLocation.tryParse(t.getString("MiningDimension"));
        cachedOreDimension=null;
        cachedDimensionOres=List.of();
    }

    public void invalidateCaps(){
        super.invalidateCaps();
        energyCap.invalidate();
        itemCap.invalidate();
    }
}
