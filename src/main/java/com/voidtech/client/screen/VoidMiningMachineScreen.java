package com.voidtech.client.screen;

import com.voidtech.menu.VoidMiningMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class VoidMiningMachineScreen extends AbstractContainerScreen<VoidMiningMachineMenu> {

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

        graphics.fill(left, top, left + imageWidth, top + imageHeight, 0xFF15202A);
        graphics.fill(left + 5, top + 5, left + imageWidth - 5, top + 27, 0xFF28506B);
        graphics.fill(left + 8, top + 34, left + 168, top + 68, 0xFF0D151C);
        graphics.fill(left + 8, top + 76, left + 168, top + 111, 0xFF0D151C);

        int barLeft = left + 20;
        int barTop = top + 98;
        int barWidth = 96;
        int barHeight = 6;

        graphics.fill(
                barLeft, barTop, barLeft + barWidth, barTop + barHeight, 0xFF253641
        );

        int maxEnergy = Math.max(1, menu.getMaxEnergyStored());
        int storedEnergy = Math.max(0, Math.min(maxEnergy, menu.getEnergyStored()));
        int filledWidth = (int) ((long) barWidth * storedEnergy / maxEnergy);

        if (filledWidth > 0) {
            graphics.fill(
                    barLeft, barTop, barLeft + filledWidth, barTop + barHeight, 0xFF56BCE8
            );
        }
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
                        "block.voidtech.void_mining_machine_t" + menu.getTier()
                ),
                8, 10, 0xE8F8FF, false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.voidtech.status"),
                12, 39, 0xBBD9E8, false
        );

        Component status = Component.translatable(
                menu.isStructureValid()
                        ? "gui.voidtech.structure_valid"
                        : "gui.voidtech.structure_invalid"
        );

        graphics.drawString(
                font,
                status,
                72, 39, menu.isStructureValid() ? 0x8FF0A8 : 0xFF8A8A, false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.voidtech.energy"),
                12, 82, 0xBBD9E8, false
        );

        graphics.drawString(
                font,
                menu.getEnergyStored() + " / " + menu.getMaxEnergyStored() + " FE",
                76, 82, 0xE8F8FF, false
        );

        graphics.drawString(
                font,
                Component.translatable("gui.voidtech.progress"),
                12, 99, 0xBBD9E8, false
        );
    }
}
