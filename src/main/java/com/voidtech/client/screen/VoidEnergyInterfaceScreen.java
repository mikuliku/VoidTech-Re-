package com.voidtech.client.screen;

import com.voidtech.menu.VoidEnergyInterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VoidEnergyInterfaceScreen extends AbstractContainerScreen<VoidEnergyInterfaceMenu> {
    public VoidEnergyInterfaceScreen(VoidEnergyInterfaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 120;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int l = leftPos, t = topPos;
        graphics.fill(l, t, l + imageWidth, t + imageHeight, 0xFF15202A);
        graphics.fill(l + 5, t + 5, l + imageWidth - 5, t + 27, 0xFF28506B);
        graphics.fill(l + 8, t + 35, l + 168, t + 102, 0xFF0D151C);

        int x = l + 20, y = t + 70, width = 136, height = 8;
        graphics.fill(x, y, x + width, y + height, 0xFF253641);
        int max = Math.max(1, menu.getMaxEnergyStored());
        int stored = Math.max(0, Math.min(max, menu.getEnergyStored()));
        int filled = (int)((long) width * stored / max);
        if (filled > 0) graphics.fill(x, y, x + filled, y + height, 0xFF56BCE8);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font,
                Component.translatable("block.voidtech.void_energy_interface_t" + menu.getTier()),
                8, 10, 0xE8F8FF, false);
        graphics.drawString(font, Component.translatable("gui.voidtech.status"),
                12, 42, 0xBBD9E8, false);
        graphics.drawString(font, "已连接", 96, 42, 0xE8F8FF, false);
        graphics.drawString(font, Component.translatable("gui.voidtech.energy"),
                12, 57, 0xBBD9E8, false);
        graphics.drawString(font,
                menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " FE",
                76, 57, 0xE8F8FF, false);
    }
}
