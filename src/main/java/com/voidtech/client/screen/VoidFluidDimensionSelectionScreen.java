package com.voidtech.client.screen;

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
import java.util.List;

public class VoidFluidDimensionSelectionScreen extends Screen {
    private final Screen parent;
    private final BlockPos machinePos;
    private final List<ResourceLocation> dimensions = new ArrayList<>();
    private int page;

    public VoidFluidDimensionSelectionScreen(Screen parent, BlockPos machinePos) {
        super(Component.literal("选择目标维度"));
        this.parent = parent;
        this.machinePos = machinePos;
    }

    @Override
    protected void init() {
        dimensions.clear();

        if (Minecraft.getInstance().level != null) {
            dimensions.addAll(
                    Minecraft.getInstance().level.registryAccess()
                            .registryOrThrow(Registries.LEVEL_STEM)
                            .keySet()
            );
        }

        dimensions.sort(Comparator.comparing(ResourceLocation::toString));
        page = Math.max(0, Math.min(page, maxPage()));
        rebuildButtons();
    }

    private int maxPage() {
        return Math.max(0, (dimensions.size() - 1) / 8);
    }

    private void rebuildButtons() {
        clearWidgets();

        int left = width / 2 - 110;
        int start = page * 8;
        int end = Math.min(start + 8, dimensions.size());

        for (int i = start; i < end; i++) {
            ResourceLocation id = dimensions.get(i);
            int row = i - start;

            addRenderableWidget(Button.builder(
                    Component.literal(id.toString()),
                    b -> {
                        VoidTechNetwork.CHANNEL.sendToServer(
                                new SetVoidFluidDimensionPacket(machinePos, id));
                        Minecraft.getInstance().setScreen(parent);
                    }
            ).bounds(left, 35 + row * 23, 220, 20).build());
        }

        if (page > 0) {
            addRenderableWidget(Button.builder(Component.literal("上一页"), b -> {
                page--;
                rebuildButtons();
            }).bounds(left, height - 25, 70, 20).build());
        }

        addRenderableWidget(Button.builder(Component.literal("返回"),
                b -> Minecraft.getInstance().setScreen(parent))
                .bounds(width / 2 - 35, height - 25, 70, 20).build());

        if (page < maxPage()) {
            addRenderableWidget(Button.builder(Component.literal("下一页"), b -> {
                page++;
                rebuildButtons();
            }).bounds(left + 150, height - 25, 70, 20).build());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, title, width / 2, 15, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
