package com.voidtech.client.screen;

import com.voidtech.menu.VoidMiningMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VoidMiningMachineScreen extends AbstractContainerScreen<VoidMiningMachineMenu> {
    public VoidMiningMachineScreen(VoidMiningMachineMenu menu,Inventory inv,Component title){
        super(menu,inv,title);
        imageWidth=176;
        imageHeight=184;
    }

    @Override protected void init(){
        super.init();
        if(menu.hasDimensionUpgrade()){
            addRenderableWidget(Button.builder(
                    Component.translatable("gui.voidtech.select_dimension"),
                    b->minecraft.setScreen(
                            new VoidDimensionSelectionScreen(menu.getMachinePos())))
                    .bounds(leftPos+8,topPos+68,160,20).build());
        }
    }

    @Override protected void renderBg(GuiGraphics g,float pt,int mx,int my){
        int l=leftPos,t=topPos;
        g.fill(l,t,l+imageWidth,t+imageHeight,0xFF15202A);
        g.fill(l+5,t+5,l+imageWidth-5,t+27,0xFF28506B);
        int bl=l+20,bt=t+98,bw=96,bh=6;
        g.fill(bl,bt,bl+bw,bt+bh,0xFF253641);
        int max=Math.max(1,menu.getMaxEnergyStored());
        int cur=Math.max(0,Math.min(max,menu.getEnergyStored()));
        int fw=(int)((long)bw*cur/max);
        if(fw>0)g.fill(bl,bt,bl+fw,bt+bh,0xFF56BCE8);
    }

    @Override protected void renderLabels(GuiGraphics g,int mx,int my){
        g.drawString(font,Component.translatable(
                "block.voidtech.void_mining_machine_t"+menu.getTier()),8,10,0xE8F8FF,false);
        g.drawString(font,Component.translatable("gui.voidtech.upgrades"),
                90,1,0xE8F8FF,false);
        g.drawString(font,Component.translatable(
                menu.isStructureValid()?"gui.voidtech.structure_valid":
                        "gui.voidtech.structure_invalid"),
                12,39,menu.isStructureValid()?0x8FF0A8:0xFF8A8A,false);
        g.drawString(font,menu.getEnergyStored()+" / "+
                menu.getMaxEnergyStored()+" FE",76,82,0xE8F8FF,false);
    }
}
