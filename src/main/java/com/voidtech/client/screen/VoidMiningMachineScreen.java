package com.voidtech.client.screen;

import com.voidtech.menu.VoidMiningMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VoidMiningMachineScreen
        extends AbstractContainerScreen<VoidMiningMachineMenu> {

    public VoidMiningMachineScreen(
            VoidMiningMachineMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        int left = this.leftPos;
        int top = this.topPos;

        graphics.fill(
                left,
                top,
                left + imageWidth,
                top + imageHeight,
                0xFF15202A
        );

        graphics.fill(
                left + 5,
                top + 5,
                left + imageWidth - 5,
                top + 27,
                0xFF28506B
        );

        graphics.fill(
                left + 8,
                top + 34,
                left + 168,
                top + 68,
                0xFF0D151C
        );

        graphics.fill(
                left + 8,
                top + 76,
                left + 168,
                top + 111,
                0xFF0D151C
        );

        // Progress bar placeholder.
        graphics.fill(
                left + 20,
                top + 98,
                left + 116,
                top + 104,
                0xFF253641
        );
        graphics.fill(
                left + 20,
                top + 98,
                left + 72,
                top + 104,
                0xFF56BCE8
        );
    }

    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        graphics.drawString(
                font,
                Component.translatable(
                        "block.voidtech.void_mining_machine_t"
                                + menu.getTier()
                ),
                8,
                10,
                0xE8F8FF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.voidtech.status"),
                12,
                39,
                0xBBD9E8,
                false
        );

        graphics.drawString(
                font,
                "待机",
                95,
                39,
                0xE8F8FF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.voidtech.energy"),
                12,
                82,
                0xBBD9E8,
                false
        );

        graphics.drawString(
                font,
                "0 / 0 FE",
                95,
                82,
                0xE8F8FF,
                false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.voidtech.progress"),
                12,
                99,
                0xBBD9E8,
                false
        );
    }
}
