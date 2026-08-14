package com.voidtech.menu;

import com.voidtech.registry.ModItems;
import com.voidtech.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class VoidMiningMachineMenu extends AbstractContainerMenu {
    private final int tier;
    private final ContainerData data;
    private final BlockPos machinePos;

    public VoidMiningMachineMenu(int id, Inventory inv, int tier, ContainerData data,
                                 IItemHandler output, IItemHandler upgrades, BlockPos machinePos) {
        super(ModMenus.VOID_MINING_MACHINE.get());
        this.tier=Math.max(1,Math.min(6,tier));
        this.data=data;
        this.machinePos=machinePos;
        addDataSlots(data);

        for(int i=0;i<4;i++){
            final int s=i;
            addSlot(new SlotItemHandler(upgrades,i,98+i*18,10){
                public boolean mayPlace(ItemStack st){
                    return switch(s){
                        case 0->st.is(ModItems.SPEED_UPGRADE.get());
                        case 1->st.is(ModItems.YIELD_UPGRADE.get());
                        case 2->st.is(ModItems.PRECISION_UPGRADE.get());
                        case 3->st.is(ModItems.DIMENSION_UPGRADE.get());
                        default->false;
                    };
                }
                public int getMaxStackSize(){return 1;}
            });
        }
        for(int i=0;i<9;i++) addSlot(new SlotItemHandler(output,i,44+i*18,40));
        for(int r=0;r<3;r++) for(int c=0;c<9;c++) addSlot(new Slot(inv,c+r*9+9,8+c*18,102+r*18));
        for(int c=0;c<9;c++) addSlot(new Slot(inv,c,8+c*18,160));
    }

    private VoidMiningMachineMenu(int id,Inventory inv,int tier,BlockPos pos){
        this(id,inv,tier,new SimpleContainerData(4),
                new net.minecraftforge.items.ItemStackHandler(9),
                new net.minecraftforge.items.ItemStackHandler(4),pos);
    }

    public static VoidMiningMachineMenu fromNetwork(int id,Inventory inv,FriendlyByteBuf buf){
        int tier=buf.readVarInt();
        BlockPos pos=buf.readBlockPos();
        return new VoidMiningMachineMenu(id,inv,tier,pos);
    }

    public int getTier(){return tier;}
    public BlockPos getMachinePos(){return machinePos;}
    public int getEnergyStored(){return data.get(0);}
    public int getMaxEnergyStored(){return Math.max(1,data.get(1));}
    public boolean isStructureValid(){return data.get(2)==1;}
    public boolean hasDimensionUpgrade(){return data.get(3)==1;}

    @Override public ItemStack quickMoveStack(Player p,int i){
        int up=4,out=up+9;
        ItemStack st=getSlot(i).getItem().copy();
        if(st.isEmpty())return ItemStack.EMPTY;
        if(i<out){
            if(!moveItemStackTo(st,out,slots.size(),true))return ItemStack.EMPTY;
            getSlot(i).set(ItemStack.EMPTY);
            return st;
        }
        int target=st.is(ModItems.SPEED_UPGRADE.get())?0:
                st.is(ModItems.YIELD_UPGRADE.get())?1:
                st.is(ModItems.PRECISION_UPGRADE.get())?2:
                st.is(ModItems.DIMENSION_UPGRADE.get())?3:-1;
        if(target>=0&&moveItemStackTo(st,target,target+1,false)){
            getSlot(i).set(ItemStack.EMPTY);
            return st;
        }
        return ItemStack.EMPTY;
    }

    @Override public boolean stillValid(Player p){return true;}
}
