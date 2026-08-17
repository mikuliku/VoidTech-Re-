package com.voidtech.client.screen;

import com.voidtech.menu.VoidFluidMachineMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VoidFluidMachineScreen extends AbstractContainerScreen<VoidFluidMachineMenu> {
    public VoidFluidMachineScreen(VoidFluidMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 140;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(Button.builder(
                Component.translatable("gui.voidtech.select_fluid"),
                b -> Minecraft.getInstance().setScreen(
                        new VoidFluidSelectionScreen(this, menu.getMachinePos())))
                .bounds(leftPos + 38, topPos + 106, 100, 20)
                .build());

        if (menu.hasDimensionUpgrade()) {
            addRenderableWidget(Button.builder(
                    Component.translatable("gui.voidtech.select_dimension"),
                    b -> Minecraft.getInstance().setScreen(
                            new VoidFluidDimensionSelectionScreen(this, menu.getMachinePos())))
                    .bounds(leftPos + 38, topPos + 83, 100, 20)
                    .build());
        }
    }

    public int getMachineTier() {
        return menu.getTier();
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xFF101820);
        g.fill(x + 4, y + 4, x + imageWidth - 4, y + imageHeight - 4, 0xFF182A35);

        int eMax = Math.max(1, menu.getMaxEnergyStored());
        int e = Math.max(0, Math.min(menu.getEnergyStored(), eMax));
        int eh = 72 * e / eMax;
        g.fill(x + 12, y + 20, x + 24, y + 92, 0xFF26343B);
        g.fill(x + 12, y + 92 - eh, x + 24, y + 92, 0xFF4DB6FF);

        int fMax = Math.max(1, menu.getFluidCapacity());
        int f = Math.max(0, Math.min(menu.getFluidAmount(), fMax));
        int fh = 72 * f / fMax;
        g.fill(x + 152, y + 20, x + 164, y + 92, 0xFF26343B);
        g.fill(x + 152, y + 92 - fh, x + 164, y + 92, 0xFF58A6FF);

        int p = Math.max(0, Math.min(menu.getProgress(), 100));
        g.fill(x + 38, y + 52, x + 138, y + 62, 0xFF26343B);
        g.fill(x + 38, y + 52, x + 38 + p, y + 62, 0xFF6CC8FF);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        g.drawString(font, Component.translatable("menu.voidtech.void_fluid_machine"),
                8, 6, 0xFFFFFF, false);
        g.drawString(font, Component.literal("T" + menu.getTier()),
                38, 20, 0xD7EFFF, false);
    }
}
