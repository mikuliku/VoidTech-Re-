package com.voidtech.registry;

import com.voidtech.VoidTech;
import com.voidtech.block.VoidEnergyInterfaceBlock;
import com.voidtech.block.VoidFluidInterfaceBlock;
import com.voidtech.block.VoidItemInterfaceBlock;
import com.voidtech.block.VoidMiningMachineBlock;
import com.voidtech.block.VoidFabricatorBlock;
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

    public static final RegistryObject<Block> VOID_FRAME = BLOCKS.register("void_frame",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(3.0F, 6.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> REINFORCED_VOID_FRAME = BLOCKS.register("reinforced_void_frame",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(5.0F, 10.0F).sound(SoundType.METAL)));

    public static final RegistryObject<Block> VOID_CRYSTAL_ORE = BLOCKS.register("void_crystal_ore",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                    .strength(3.0F, 3.0F).requiresCorrectToolForDrops().sound(SoundType.STONE)));

    public static final RegistryObject<Block> VOID_MINING_MACHINE_T1 = registerMachine("void_mining_machine_t1", 1);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T2 = registerMachine("void_mining_machine_t2", 2);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T3 = registerMachine("void_mining_machine_t3", 3);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T4 = registerMachine("void_mining_machine_t4", 4);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T5 = registerMachine("void_mining_machine_t5", 5);
    public static final RegistryObject<Block> VOID_MINING_MACHINE_T6 = registerMachine("void_mining_machine_t6", 6);

    public static final RegistryObject<Block> VOID_FLUID_MACHINE_T1 = registerFluidMachine("void_fluid_machine_t1", 1);
    public static final RegistryObject<Block> VOID_FLUID_MACHINE_T2 = registerFluidMachine("void_fluid_machine_t2", 2);
    public static final RegistryObject<Block> VOID_FLUID_MACHINE_T3 = registerFluidMachine("void_fluid_machine_t3", 3);
    public static final RegistryObject<Block> VOID_FLUID_MACHINE_T4 = registerFluidMachine("void_fluid_machine_t4", 4);
    public static final RegistryObject<Block> VOID_FLUID_MACHINE_T5 = registerFluidMachine("void_fluid_machine_t5", 5);
    public static final RegistryObject<Block> VOID_FLUID_MACHINE_T6 = registerFluidMachine("void_fluid_machine_t6", 6);

    public static final RegistryObject<Block> VOID_FABRICATOR_T1 = registerFabricator("void_fabricator_t1", 1);
    public static final RegistryObject<Block> VOID_FABRICATOR_T2 = registerFabricator("void_fabricator_t2", 2);
    public static final RegistryObject<Block> VOID_FABRICATOR_T3 = registerFabricator("void_fabricator_t3", 3);
    public static final RegistryObject<Block> VOID_FABRICATOR_T4 = registerFabricator("void_fabricator_t4", 4);
    public static final RegistryObject<Block> VOID_FABRICATOR_T5 = registerFabricator("void_fabricator_t5", 5);
    public static final RegistryObject<Block> VOID_FABRICATOR_T6 = registerFabricator("void_fabricator_t6", 6);

    public static final RegistryObject<Block> VOID_ENERGY_INTERFACE_T1 = registerEnergyInterface("void_energy_interface_t1", 1);
    public static final RegistryObject<Block> VOID_ENERGY_INTERFACE_T2 = registerEnergyInterface("void_energy_interface_t2", 2);
    public static final RegistryObject<Block> VOID_ENERGY_INTERFACE_T3 = registerEnergyInterface("void_energy_interface_t3", 3);
    public static final RegistryObject<Block> VOID_ENERGY_INTERFACE_T4 = registerEnergyInterface("void_energy_interface_t4", 4);
    public static final RegistryObject<Block> VOID_ENERGY_INTERFACE_T5 = registerEnergyInterface("void_energy_interface_t5", 5);
    public static final RegistryObject<Block> VOID_ENERGY_INTERFACE_T6 = registerEnergyInterface("void_energy_interface_t6", 6);

    public static final RegistryObject<Block> VOID_ITEM_INTERFACE_T1 = registerItemInterface("void_item_interface_t1", 1);
    public static final RegistryObject<Block> VOID_ITEM_INTERFACE_T2 = registerItemInterface("void_item_interface_t2", 2);
    public static final RegistryObject<Block> VOID_ITEM_INTERFACE_T3 = registerItemInterface("void_item_interface_t3", 3);
    public static final RegistryObject<Block> VOID_ITEM_INTERFACE_T4 = registerItemInterface("void_item_interface_t4", 4);
    public static final RegistryObject<Block> VOID_ITEM_INTERFACE_T5 = registerItemInterface("void_item_interface_t5", 5);
    public static final RegistryObject<Block> VOID_ITEM_INTERFACE_T6 = registerItemInterface("void_item_interface_t6", 6);

    public static final RegistryObject<Block> VOID_FLUID_INTERFACE_T1 = registerFluidInterface("void_fluid_interface_t1", 1);
    public static final RegistryObject<Block> VOID_FLUID_INTERFACE_T2 = registerFluidInterface("void_fluid_interface_t2", 2);
    public static final RegistryObject<Block> VOID_FLUID_INTERFACE_T3 = registerFluidInterface("void_fluid_interface_t3", 3);
    public static final RegistryObject<Block> VOID_FLUID_INTERFACE_T4 = registerFluidInterface("void_fluid_interface_t4", 4);
    public static final RegistryObject<Block> VOID_FLUID_INTERFACE_T5 = registerFluidInterface("void_fluid_interface_t5", 5);
    public static final RegistryObject<Block> VOID_FLUID_INTERFACE_T6 = registerFluidInterface("void_fluid_interface_t6", 6);

    private static RegistryObject<Block> registerFluidMachine(String id, int tier) {
        return BLOCKS.register(id, () -> new com.voidtech.block.VoidFluidMachineBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(5.0F, 10.0F).sound(SoundType.METAL), tier));
    }

    private static RegistryObject<Block> registerMachine(String id, int tier) {
        return BLOCKS.register(id, () -> new VoidMiningMachineBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(5.0F, 10.0F).sound(SoundType.METAL), tier));
    }

    private static RegistryObject<Block> registerFabricator(String id, int tier) {
        return BLOCKS.register(id, () -> new VoidFabricatorBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(5.0F, 10.0F).sound(SoundType.METAL), tier));
    }

    private static RegistryObject<Block> registerEnergyInterface(String id, int tier) {
        return BLOCKS.register(id, () -> new VoidEnergyInterfaceBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(4.0F, 8.0F).sound(SoundType.METAL), tier));
    }

    private static RegistryObject<Block> registerItemInterface(String id, int tier) {
        return BLOCKS.register(id, () -> new VoidItemInterfaceBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(4.0F, 8.0F).sound(SoundType.METAL), tier));
    }

    private static RegistryObject<Block> registerFluidInterface(String id, int tier) {
        return BLOCKS.register(id, () -> new VoidFluidInterfaceBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .strength(4.0F, 8.0F).sound(SoundType.METAL), tier));
    }

    private ModBlocks() {}
}
