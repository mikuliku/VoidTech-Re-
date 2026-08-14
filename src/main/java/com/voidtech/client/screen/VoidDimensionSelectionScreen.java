package com.voidtech.client.screen;

import com.voidtech.network.SetMiningDimensionPacket;
import com.voidtech.network.VoidTechNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class VoidDimensionSelectionScreen extends Screen {
    private final BlockPos machinePos;
    private final List<ResourceLocation> dimensions;
    private int page;

    public VoidDimensionSelectionScreen(BlockPos machinePos) {
        super(Component.translatable("gui.voidtech.dimension_selection"));
        this.machinePos = machinePos;
        this.dimensions = new ArrayList<>(net.minecraft.client.Minecraft.getInstance()
                .level.registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.DIMENSION)
                .keySet());
    }

    @Override protected void init() {
        super.init();
        rebuildButtons();
    }

    private void rebuildButtons() {
        clearWidgets();
        int start = page * 8;
        int end = Math.min(start + 8, dimensions.size());
        for (int i=start; i<end; i++) {
            ResourceLocation id = dimensions.get(i);
            int row=i-start;
            addRenderableWidget(Button.builder(Component.literal(id.toString()), b -> {
                VoidTechNetwork.CHANNEL.sendToServer(new SetMiningDimensionPacket(machinePos,id));
                onClose();
            }).bounds(width/2-110, 35+row*24, 220, 20).build());
        }
        if (page > 0)
            addRenderableWidget(Button.builder(Component.translatable("gui.voidtech.previous"),
                    b -> { page--; rebuildButtons(); }).bounds(width/2-110, height-45, 105, 20).build());
        if ((page+1)*8 < dimensions.size())
            addRenderableWidget(Button.builder(Component.translatable("gui.voidtech.next"),
                    b -> { page++; rebuildButtons(); }).bounds(width/2+5, height-45, 105, 20).build());
    }

    @Override public void render(GuiGraphics g,int mouseX,int mouseY,float partialTick) {
        renderBackground(g);
        g.drawCenteredString(font,title,width/2,15,0xFFFFFF);
        super.render(g,mouseX,mouseY,partialTick);
    }

    @Override public boolean isPauseScreen() { return false; }
}
