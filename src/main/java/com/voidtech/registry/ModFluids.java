package com.voidtech.registry;

import com.voidtech.VoidTech;
import com.voidtech.fluid.VoidFluidTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, VoidTech.MOD_ID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, VoidTech.MOD_ID);

    public static final DeferredRegister<Block> FLUID_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, VoidTech.MOD_ID);

    public static final RegistryObject<FluidType> VOID_ORIGINAL_TYPE =
            FLUID_TYPES.register("void_original", () -> VoidFluidTypes.PROPERTIES);

    public static final RegistryObject<FluidType> VOID_IGNITED_TYPE =
            FLUID_TYPES.register("void_ignited", () -> VoidFluidTypes.HOT_PROPERTIES);

    public static final RegistryObject<FluidType> VOID_METAL_TYPE =
            FLUID_TYPES.register("void_metal_melt", () -> VoidFluidTypes.METAL_PROPERTIES);

    public static final RegistryObject<FluidType> VOID_ESSENCE_TYPE =
            FLUID_TYPES.register("void_essence", () -> VoidFluidTypes.ESSENCE_PROPERTIES);

    public static final RegistryObject<FluidType> VOID_ENERGY_TYPE =
            FLUID_TYPES.register("void_energy", () -> VoidFluidTypes.ENERGY_PROPERTIES);

    public static final RegistryObject<FluidType> VOID_CONCENTRATE_TYPE =
            FLUID_TYPES.register("void_concentrate", () -> VoidFluidTypes.CONCENTRATE_PROPERTIES);

    public static final RegistryObject<ForgeFlowingFluid> VOID_ORIGINAL =
            FLUIDS.register("void_original",
                    () -> new ForgeFlowingFluid.Source(VOID_ORIGINAL_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_ORIGINAL_FLOWING =
            FLUIDS.register("void_original_flowing",
                    () -> new ForgeFlowingFluid.Flowing(VOID_ORIGINAL_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_IGNITED =
            FLUIDS.register("void_ignited",
                    () -> new ForgeFlowingFluid.Source(VOID_IGNITED_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_IGNITED_FLOWING =
            FLUIDS.register("void_ignited_flowing",
                    () -> new ForgeFlowingFluid.Flowing(VOID_IGNITED_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_METAL_MELT =
            FLUIDS.register("void_metal_melt",
                    () -> new ForgeFlowingFluid.Source(VOID_METAL_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_METAL_MELT_FLOWING =
            FLUIDS.register("void_metal_melt_flowing",
                    () -> new ForgeFlowingFluid.Flowing(VOID_METAL_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_ESSENCE =
            FLUIDS.register("void_essence",
                    () -> new ForgeFlowingFluid.Source(VOID_ESSENCE_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_ESSENCE_FLOWING =
            FLUIDS.register("void_essence_flowing",
                    () -> new ForgeFlowingFluid.Flowing(VOID_ESSENCE_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_ENERGY =
            FLUIDS.register("void_energy",
                    () -> new ForgeFlowingFluid.Source(VOID_ENERGY_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_ENERGY_FLOWING =
            FLUIDS.register("void_energy_flowing",
                    () -> new ForgeFlowingFluid.Flowing(VOID_ENERGY_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_CONCENTRATE =
            FLUIDS.register("void_concentrate",
                    () -> new ForgeFlowingFluid.Source(VOID_CONCENTRATE_PROPERTIES));

    public static final RegistryObject<ForgeFlowingFluid> VOID_CONCENTRATE_FLOWING =
            FLUIDS.register("void_concentrate_flowing",
                    () -> new ForgeFlowingFluid.Flowing(VOID_CONCENTRATE_PROPERTIES));

    public static final RegistryObject<Block> VOID_ORIGINAL_BLOCK =
            registerFluidBlock("void_original", VOID_ORIGINAL);

    public static final RegistryObject<Block> VOID_IGNITED_BLOCK =
            registerFluidBlock("void_ignited", VOID_IGNITED);

    public static final RegistryObject<Block> VOID_METAL_MELT_BLOCK =
            registerFluidBlock("void_metal_melt", VOID_METAL_MELT);

    public static final RegistryObject<Block> VOID_ESSENCE_BLOCK =
            registerFluidBlock("void_essence", VOID_ESSENCE);

    public static final RegistryObject<Block> VOID_ENERGY_BLOCK =
            registerFluidBlock("void_energy", VOID_ENERGY);

    public static final RegistryObject<Block> VOID_CONCENTRATE_BLOCK =
            registerFluidBlock("void_concentrate", VOID_CONCENTRATE);

    public static final ForgeFlowingFluid.Properties VOID_ORIGINAL_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    VOID_ORIGINAL_TYPE,
                    VOID_ORIGINAL,
                    VOID_ORIGINAL_FLOWING)
                    .slopeFindDistance(4)
                    .levelDecreasePerBlock(1)
                    .block(VOID_ORIGINAL_BLOCK);

    public static final ForgeFlowingFluid.Properties VOID_IGNITED_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    VOID_IGNITED_TYPE,
                    VOID_IGNITED,
                    VOID_IGNITED_FLOWING)
                    .slopeFindDistance(4)
                    .levelDecreasePerBlock(1)
                    .block(VOID_IGNITED_BLOCK);

    public static final ForgeFlowingFluid.Properties VOID_METAL_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    VOID_METAL_TYPE,
                    VOID_METAL_MELT,
                    VOID_METAL_MELT_FLOWING)
                    .slopeFindDistance(2)
                    .levelDecreasePerBlock(2)
                    .block(VOID_METAL_MELT_BLOCK);

    public static final ForgeFlowingFluid.Properties VOID_ESSENCE_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    VOID_ESSENCE_TYPE,
                    VOID_ESSENCE,
                    VOID_ESSENCE_FLOWING)
                    .slopeFindDistance(4)
                    .levelDecreasePerBlock(1)
                    .block(VOID_ESSENCE_BLOCK);

    public static final ForgeFlowingFluid.Properties VOID_ENERGY_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    VOID_ENERGY_TYPE,
                    VOID_ENERGY,
                    VOID_ENERGY_FLOWING)
                    .slopeFindDistance(4)
                    .levelDecreasePerBlock(1)
                    .block(VOID_ENERGY_BLOCK);

    public static final ForgeFlowingFluid.Properties VOID_CONCENTRATE_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    VOID_CONCENTRATE_TYPE,
                    VOID_CONCENTRATE,
                    VOID_CONCENTRATE_FLOWING)
                    .slopeFindDistance(2)
                    .levelDecreasePerBlock(2)
                    .block(VOID_CONCENTRATE_BLOCK);

    private static RegistryObject<Block> registerFluidBlock(
            String name,
            RegistryObject<ForgeFlowingFluid> source) {
        return FLUID_BLOCKS.register(name,
                () -> new LiquidBlock(
                        source.get(),
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                .replaceable()
                                .noCollission()
                                .strength(100.0F)
                                .noLootTable()));
    }

    private ModFluids() {
    }
}
