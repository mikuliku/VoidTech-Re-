package com.voidtech.client.screen;

import com.voidtech.menu.VoidItemInterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VoidItemInterfaceScreen extends AbstractContainerScreen<VoidItemInterfaceMenu> {
    public VoidItemInterfaceScreen(VoidItemInterfaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        int rows = (menu.getMachineSlots() + 8) / 9;
        this.imageHeight = 24 + rows * 18 + 76;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int l = leftPos;
        int t = topPos;
        graphics.fill(l, t, l + imageWidth, t + imageHeight, 0xFF15202A);
        graphics.fill(l + 5, t + 5, l + imageWidth - 5, t + 25, 0xFF28506B);
        graphics.fill(l + 5, t + 28, l + imageWidth - 5, t + imageHeight - 5, 0xFF0D151C);

        int rows = (menu.getMachineSlots() + 8) / 9;
        for (int i = 0; i < menu.getMachineSlots(); i++) {
            int row = i / 9;
            int col = i % 9;
            int x = l + 7 + col * 18;
            int y = t + 17 + row * 18;
            graphics.fill(x, y, x + 18, y + 18, 0xFF394955);
        }

        int playerStart = t + 24 + rows * 18;
        graphics.fill(l + 7, playerStart - 1, l + 169, playerStart + 53, 0xFF303C44);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font,
                Component.translatable("block.voidtech.void_item_interface_t" + menu.getTier()),
                8, 8, 0xE8F8FF, false);
    }
}
