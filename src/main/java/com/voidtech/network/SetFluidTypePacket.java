package com.voidtech.network;

import com.voidtech.block.entity.VoidFluidMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public record SetFluidTypePacket(BlockPos pos, ResourceLocation fluid) {
    public static void handle(SetFluidTypePacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            if (!player.level().isLoaded(packet.pos())) return;
            if (!player.blockPosition().closerThan(packet.pos(), 16.0D)) return;

            var be = player.level().getBlockEntity(packet.pos());
            if (!(be instanceof VoidFluidMachineBlockEntity machine)) return;

            Fluid fluid = ForgeRegistries.FLUIDS.getValue(packet.fluid());
            if (fluid == null || fluid == Fluids.EMPTY) return;
            if (!fluid.defaultFluidState().isSource()) return;

            machine.setSelectedFluid(packet.fluid());
        });
        ctx.setPacketHandled(true);
    }
}
