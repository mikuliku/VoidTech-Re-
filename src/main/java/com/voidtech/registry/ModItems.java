package com.voidtech.registry;

import com.voidtech.VoidTech;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VoidTech.MOD_ID);

    public static final RegistryObject<Item> VOID_FRAME = ITEMS.register(
            "void_frame", () -> new BlockItem(ModBlocks.VOID_FRAME.get(), new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_VOID_FRAME = ITEMS.register(
            "reinforced_void_frame", () -> new BlockItem(ModBlocks.REINFORCED_VOID_FRAME.get(), new Item.Properties()));
    public static final RegistryObject<Item> VOID_CRYSTAL_ORE = ITEMS.register(
            "void_crystal_ore", () -> new BlockItem(ModBlocks.VOID_CRYSTAL_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> VOID_CRYSTAL = ITEMS.register(
            "void_crystal", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VOID_ALLOY = ITEMS.register(
            "void_alloy", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VOIDIUM_INGOT = ITEMS.register(
            "voidium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RESONANT_VOID_ALLOY = ITEMS.register(
            "resonant_void_alloy", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DIMENSIONAL_ALLOY = ITEMS.register(
            "dimensional_alloy", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> VOID_SINGULARITY = ITEMS.register(
            "void_singularity", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> VOID_MINING_MACHINE_T1 = registerMachineItem(
            "void_mining_machine_t1", ModBlocks.VOID_MINING_MACHINE_T1);
    public static final RegistryObject<Item> VOID_MINING_MACHINE_T2 = registerMachineItem(
            "void_mining_machine_t2", ModBlocks.VOID_MINING_MACHINE_T2);
    public static final RegistryObject<Item> VOID_MINING_MACHINE_T3 = registerMachineItem(
            "void_mining_machine_t3", ModBlocks.VOID_MINING_MACHINE_T3);
    public static final RegistryObject<Item> VOID_MINING_MACHINE_T4 = registerMachineItem(
            "void_mining_machine_t4", ModBlocks.VOID_MINING_MACHINE_T4);
    public static final RegistryObject<Item> VOID_MINING_MACHINE_T5 = registerMachineItem(
            "void_mining_machine_t5", ModBlocks.VOID_MINING_MACHINE_T5);
    public static final RegistryObject<Item> VOID_MINING_MACHINE_T6 = registerMachineItem(
            "void_mining_machine_t6", ModBlocks.VOID_MINING_MACHINE_T6);

    public static final RegistryObject<Item> VOID_ENERGY_INTERFACE_T1 = registerMachineItem(
            "void_energy_interface_t1", ModBlocks.VOID_ENERGY_INTERFACE_T1);
    public static final RegistryObject<Item> VOID_ENERGY_INTERFACE_T2 = registerMachineItem(
            "void_energy_interface_t2", ModBlocks.VOID_ENERGY_INTERFACE_T2);
    public static final RegistryObject<Item> VOID_ENERGY_INTERFACE_T3 = registerMachineItem(
            "void_energy_interface_t3", ModBlocks.VOID_ENERGY_INTERFACE_T3);
    public static final RegistryObject<Item> VOID_ENERGY_INTERFACE_T4 = registerMachineItem(
            "void_energy_interface_t4", ModBlocks.VOID_ENERGY_INTERFACE_T4);
    public static final RegistryObject<Item> VOID_ENERGY_INTERFACE_T5 = registerMachineItem(
            "void_energy_interface_t5", ModBlocks.VOID_ENERGY_INTERFACE_T5);
    public static final RegistryObject<Item> VOID_ENERGY_INTERFACE_T6 = registerMachineItem(
            "void_energy_interface_t6", ModBlocks.VOID_ENERGY_INTERFACE_T6);

    private static RegistryObject<Item> registerMachineItem(
            String id, RegistryObject<net.minecraft.world.level.block.Block> block) {
        return ITEMS.register(id, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private ModItems() {
    }
}
