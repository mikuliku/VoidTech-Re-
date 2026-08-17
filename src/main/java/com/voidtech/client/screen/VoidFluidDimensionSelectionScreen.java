package com.voidtech.client.screen;

import com.voidtech.fluid.VoidFluidDimensionCatalog;
import com.voidtech.network.SetVoidFluidDimensionPacket;
import com.voidtech.network.VoidTechNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VoidFluidDimensionSelectionScreen extends Screen {
    private static final int PAGE_SIZE = 8;

    private final Screen parent;
    private final BlockPos machinePos;
    private final List<ResourceLocation> dimensions = new ArrayList<>();
    private int page;

    public VoidFluidDimensionSelectionScreen(Screen parent, BlockPos machinePos) {
        super(Component.translatable("gui.voidtech.dimension_selection"));
        this.parent = parent;
        this.machinePos = machinePos;
    }

    @Override
    protected void init() {
        rebuildDimensionList();
        rebuildButtons();
    }

    private void rebuildDimensionList() {
        dimensions.clear();

        Set<ResourceLocation> ids = new LinkedHashSet<>();
        ids.addAll(VoidFluidDimensionCatalog.getKnownDimensions());

        if (Minecraft.getInstance().level != null) {
            Minecraft.getInstance().level.registryAccess()
                    .registry(Registries.LEVEL_STEM)
                    .ifPresent(registry -> ids.addAll(registry.keySet()));
        }

        dimensions.addAll(ids);
        dimensions.sort(Comparator.comparing(ResourceLocation::toString));
        page = Math.max(0, Math.min(page, maxPage()));
    }

    private int maxPage() {
        return Math.max(0, (dimensions.size() - 1) / PAGE_SIZE);
    }

    private void rebuildButtons() {
        clearWidgets();

        int buttonWidth = 220;
        int left = (this.width - buttonWidth) / 2;
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, dimensions.size());

        for (int i = start; i < end; i++) {
            ResourceLocation id = dimensions.get(i);
            int row = i - start;

            addRenderableWidget(Button.builder(
                    Component.literal(id.toString()),
                    button -> {
                        VoidTechNetwork.CHANNEL.sendToServer(
                                new SetVoidFluidDimensionPacket(machinePos, id));
                        Minecraft.getInstance().setScreen(parent);
                    }
            ).bounds(left, 35 + row * 23, buttonWidth, 20).build());
        }

        int navY = height - 25;

        if (page > 0) {
            addRenderableWidget(Button.builder(
                    Component.translatable("gui.voidtech.previous"),
                    button -> {
                        page--;
                        rebuildButtons();
                    }).bounds(left, navY, 70, 20).build());
        }

        addRenderableWidget(Button.builder(
                Component.translatable("gui.voidtech.back"),
                button -> Minecraft.getInstance().setScreen(parent))
                .bounds((this.width - 70) / 2, navY, 70, 20)
                .build());

        if (page < maxPage()) {
            addRenderableWidget(Button.builder(
                    Component.translatable("gui.voidtech.next"),
                    button -> {
                        page++;
                        rebuildButtons();
                    }).bounds(left + buttonWidth - 70, navY, 70, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, title, this.width / 2, 15, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
