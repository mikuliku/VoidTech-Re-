package com.voidtech.menu;

import com.voidtech.block.entity.VoidFluidInterfaceBlockEntity;
import com.voidtech.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class VoidFluidInterfaceMenu extends AbstractContainerMenu {
    private final int tier;
    private final FluidTank tank;
    private final BlockPos blockPos;

    public VoidFluidInterfaceMenu(int containerId, Inventory inventory, int tier,
                                  FluidTank tank, BlockPos blockPos) {
        super(ModMenus.VOID_FLUID_INTERFACE.get(), containerId);
        this.tier = Math.max(1, Math.min(6, tier));
        this.tank = tank;
        this.blockPos = blockPos;
    }

    public static VoidFluidInterfaceMenu fromNetwork(int containerId, Inventory inventory,
                                                      FriendlyByteBuf buf) {
        int tier = buf.readVarInt();
        BlockPos pos = buf.readBlockPos();
        if (inventory.player.level().getBlockEntity(pos)
                instanceof VoidFluidInterfaceBlockEntity be) {
            return new VoidFluidInterfaceMenu(containerId, inventory, tier, be.getTank(), pos);
        }
        return new VoidFluidInterfaceMenu(containerId, inventory, tier,
                new FluidTank(VoidFluidInterfaceBlockEntity.capacityFor(tier)), pos);
    }

    public int getTier() {
        return tier;
    }

    public FluidTank getTank() {
        return tank;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(
                blockPos.getX() + 0.5D,
                blockPos.getY() + 0.5D,
                blockPos.getZ() + 0.5D) <= 64.0D;
    }
}
