package com.voidtech.client.screen;

import com.voidtech.fluid.VoidFluidCatalog;
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
    private int page;

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

        rebuildButtons();
    }

    private void rebuildButtons() {
        clearWidgets();

        int width = 260;
        int left = (this.width - width) / 2;
        int startY = 38;
        int maxRows = Math.min(10, fluids.size());
        int start = page * 10;
        int end = Math.min(start + maxRows, fluids.size());

        for (int i = start; i < end; i++) {
            ResourceLocation id = fluids.get(i);
            int row = i - start;
            boolean unlocked = VoidFluidCatalog.canProduce(id, getMachineTier());

            Component name = VoidFluidCatalog.isVoidTechFluid(id)
                    ? Component.translatable("fluid.voidtech." + id.getPath())
                    : Component.literal(id.toString());

            Button button = Button.builder(name, b -> {
                VoidTechNetwork.CHANNEL.sendToServer(new SetFluidTypePacket(machinePos, id));
                Minecraft.getInstance().setScreen(parent);
            }).bounds(left, startY + row * 23, width, 20).build();

            button.active = unlocked;
            addRenderableWidget(button);
        }

        if (page > 0) {
            addRenderableWidget(Button.builder(Component.translatable("gui.voidtech.previous"),
                    b -> { page--; rebuildButtons(); })
                    .bounds(left, height - 45, 105, 20).build());
        }

        if ((page + 1) * 10 < fluids.size()) {
            addRenderableWidget(Button.builder(Component.translatable("gui.voidtech.next"),
                    b -> { page++; rebuildButtons(); })
                    .bounds(left + 155, height - 45, 105, 20).build());
        }

        addRenderableWidget(Button.builder(Component.translatable("gui.voidtech.back"),
                b -> Minecraft.getInstance().setScreen(parent))
                .bounds(left, height - 45, 105, 20).build());
    }

    private int getMachineTier() {
        if (parent instanceof VoidFluidMachineScreen screen) {
            return screen.getMenu().getTier();
        }
        return 1;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        graphics.drawCenteredString(font, title, width / 2, 16, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
