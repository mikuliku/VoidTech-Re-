package com.voidtech.network;

import com.voidtech.block.entity.VoidFluidMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record SetVoidFluidDimensionPacket(BlockPos pos, ResourceLocation dimension) {
    public static void handle(SetVoidFluidDimensionPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            if (!player.level().isLoaded(packet.pos)) return;
            if (!player.blockPosition().closerThan(packet.pos, 16.0D)) return;

            var be = player.level().getBlockEntity(packet.pos);
            if (!(be instanceof VoidFluidMachineBlockEntity machine)) return;
            if (!machine.hasDimensionUpgrade()) return;

            ResourceKey<Level> key = ResourceKey.create(
                    net.minecraft.core.registries.Registries.DIMENSION,
                    packet.dimension
            );

            if (player.server.getLevel(key) == null) return;

            machine.setTargetDimension(packet.dimension);
        });
        ctx.setPacketHandled(true);
    }
}
