package com.voidtech.registry;

import com.voidtech.VoidTech;
import com.voidtech.block.entity.VoidEnergyInterfaceBlockEntity;
import com.voidtech.block.entity.VoidItemInterfaceBlockEntity;
import com.voidtech.block.entity.VoidMiningMachineBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VoidTech.MOD_ID);

    public static final RegistryObject<BlockEntityType<VoidMiningMachineBlockEntity>> VOID_MINING_MACHINE =
            BLOCK_ENTITIES.register("void_mining_machine", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new VoidMiningMachineBlockEntity(null, pos, state,
                            ((com.voidtech.block.VoidMiningMachineBlock) state.getBlock()).getTier()),
                    ModBlocks.VOID_MINING_MACHINE_T1.get(), ModBlocks.VOID_MINING_MACHINE_T2.get(),
                    ModBlocks.VOID_MINING_MACHINE_T3.get(), ModBlocks.VOID_MINING_MACHINE_T4.get(),
                    ModBlocks.VOID_MINING_MACHINE_T5.get(), ModBlocks.VOID_MINING_MACHINE_T6.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<VoidEnergyInterfaceBlockEntity>> VOID_ENERGY_INTERFACE =
            BLOCK_ENTITIES.register("void_energy_interface", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new VoidEnergyInterfaceBlockEntity(null, pos, state,
                            ((com.voidtech.block.VoidEnergyInterfaceBlock) state.getBlock()).getTier()),
                    ModBlocks.VOID_ENERGY_INTERFACE_T1.get(), ModBlocks.VOID_ENERGY_INTERFACE_T2.get(),
                    ModBlocks.VOID_ENERGY_INTERFACE_T3.get(), ModBlocks.VOID_ENERGY_INTERFACE_T4.get(),
                    ModBlocks.VOID_ENERGY_INTERFACE_T5.get(), ModBlocks.VOID_ENERGY_INTERFACE_T6.get()
            ).build(null));

    public static final RegistryObject<BlockEntityType<VoidItemInterfaceBlockEntity>> VOID_ITEM_INTERFACE =
            BLOCK_ENTITIES.register("void_item_interface", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new VoidItemInterfaceBlockEntity(null, pos, state,
                            ((com.voidtech.block.VoidItemInterfaceBlock) state.getBlock()).getTier()),
                    ModBlocks.VOID_ITEM_INTERFACE_T1.get(), ModBlocks.VOID_ITEM_INTERFACE_T2.get(),
                    ModBlocks.VOID_ITEM_INTERFACE_T3.get(), ModBlocks.VOID_ITEM_INTERFACE_T4.get(),
                    ModBlocks.VOID_ITEM_INTERFACE_T5.get(), ModBlocks.VOID_ITEM_INTERFACE_T6.get()
            ).build(null));

    private ModBlockEntities() {}
}
