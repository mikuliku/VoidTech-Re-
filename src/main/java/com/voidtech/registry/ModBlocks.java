package com.voidtech.registry;

import com.voidtech.VoidTech;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, VoidTech.MOD_ID);

    public static final RegistryObject<Block> VOID_FRAME = BLOCKS.register(
            "void_frame",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_BLUE)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.METAL)
            )
    );

    public static final RegistryObject<Block> REINFORCED_VOID_FRAME = BLOCKS.register(
            "reinforced_void_frame",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_LIGHT_BLUE)
                            .strength(5.0F, 10.0F)
                            .sound(SoundType.METAL)
            )
    );

    private ModBlocks() {
    }
}
