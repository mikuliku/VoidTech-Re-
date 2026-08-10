package com.voidtech.registry;

import com.voidtech.VoidTech;
import com.voidtech.block.VoidMiningMachineBlock;
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
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.METAL))
    );

    public static final RegistryObject<Block> REINFORCED_VOID_FRAME = BLOCKS.register(
            "reinforced_void_frame",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(5.0F, 10.0F)
                    .sound(SoundType.METAL))
    );

    public static final RegistryObject<Block> VOID_CRYSTAL_ORE = BLOCKS.register(
            "void_crystal_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0F, 3.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE))
    );

    public static final RegistryObject<Block> VOID_MINING_MACHINE_T1 = registerMachine("void_mining_machine_t1", 1);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T2 = registerMachine("void_mining_machine_t2", 2);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T3 = registerMachine("void_mining_machine_t3", 3);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T4 = registerMachine("void_mining_machine_t4", 4);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T5 = registerMachine("void_mining_machine_t5", 5);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T6 = registerMachine("void_mining_machine_t6", 6);

    private static RegistryObject<Block> registerMachine(String id, int tier) {
        return BLOCKS.register(id, () -> new VoidMiningMachineBlock(
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(5.0F, 10.0F)
                        .sound(SoundType.METAL),
                tier
        ));
    }

    private ModBlocks() {
    }
}
