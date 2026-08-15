package com.voidtech.client.screen;

import com.voidtech.network.SetFluidTypePacket;
import com.voidtech.network.VoidTechNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VoidFluidSelectionScreen extends Screen {
    private final Screen parent;
    private final BlockPos machinePos;
    private final List<ResourceLocation> fluids = new ArrayList<>();

    public VoidFluidSelectionScreen(Screen parent, BlockPos machinePos) {
        super(Component.translatable("gui.voidtech.fluid_selection"));
        this.parent = parent;
        this.machinePos = machinePos;
    }

    @Override
    protected void init() {
        fluids.clear();
        ForgeRegistries.FLUIDS.getEntries().stream()
                .filter(entry -> entry.getValue().defaultFluidState().isSource())
                .map(entry -> entry.getKey().location())
                .sorted(Comparator.comparing(ResourceLocation::toString))
                .forEach(fluids::add);

        int width = 220;
        int left = (this.width - width) / 2;
        int startY = 42;
        int maxRows = Math.min(10, fluids.size());

        for (int i = 0; i < maxRows; i++) {
            ResourceLocation id = fluids.get(i);
            addRenderableWidget(Button.builder(
                    Component.literal(id.toString()),
                    button -> {
                        VoidTechNetwork.CHANNEL.sendToServer(
                                new SetFluidTypePacket(machinePos, id));
                        Minecraft.getInstance().setScreen(parent);
                    })
                    .bounds(left, startY + i * 23, width, 20)
                    .build());
        }

        addRenderableWidget(Button.builder(
                Component.translatable("gui.voidtech.back"),
                button -> Minecraft.getInstance().setScreen(parent))
                .bounds(left, startY + maxRows * 23 + 8, width, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
