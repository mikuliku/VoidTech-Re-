package com.voidtech.client.screen;

import com.voidtech.menu.VoidFluidInterfaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

public class VoidFluidInterfaceScreen extends AbstractContainerScreen<VoidFluidInterfaceMenu> {
    public VoidFluidInterfaceScreen(VoidFluidInterfaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int l = leftPos;
        int t = topPos;
        graphics.fill(l, t, l + imageWidth, t + imageHeight, 0xFF15202A);
        graphics.fill(l + 5, t + 5, l + imageWidth - 5, t + 25, 0xFF28506B);
        graphics.fill(l + 20, t + 35, l + 156, t + 105, 0xFF0D151C);
        graphics.fill(l + 25, t + 40, l + 151, t + 100, 0xFF27353E);

        FluidStack fluid = menu.getTank().getFluid();
        int capacity = menu.getTank().getCapacity();
        if (!fluid.isEmpty() && capacity > 0) {
            int height = Math.max(1, fluid.getAmount() * 56 / capacity);
            graphics.fill(l + 29, t + 96 - height, l + 147, t + 96, 0xFF4AA9D8);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font,
                Component.translatable("block.voidtech.void_fluid_interface_t" + menu.getTier()),
                8, 8, 0xE8F8FF, false);

        FluidStack fluid = menu.getTank().getFluid();
        String text = fluid.isEmpty()
                ? "0 / " + menu.getTank().getCapacity() + " mB"
                : fluid.getAmount() + " / " + menu.getTank().getCapacity() + " mB";
        graphics.drawString(font, text, 52, 112, 0xD8EAF2, false);
    }
}
