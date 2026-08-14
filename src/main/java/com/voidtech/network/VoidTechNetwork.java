package com.voidtech.network;

import com.voidtech.VoidTech;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class VoidTechNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(VoidTech.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    private static int id;

    public static void register() {
        CHANNEL.registerMessage(id++, SetMiningDimensionPacket.class,
                (packet, buffer) -> {
                    buffer.writeBlockPos(packet.pos());
                    buffer.writeResourceLocation(packet.dimension());
                },
                buffer -> new SetMiningDimensionPacket(buffer.readBlockPos(), buffer.readResourceLocation()),
                SetMiningDimensionPacket::handle);
    }

    private VoidTechNetwork() {}
}
