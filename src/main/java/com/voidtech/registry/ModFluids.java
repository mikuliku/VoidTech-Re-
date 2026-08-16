package com.voidtech.registry;

import com.voidtech.VoidTech;
import com.voidtech.fluid.VoidFluidTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;
import java.util.function.Supplier;

public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, VoidTech.MOD_ID);

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, VoidTech.MOD_ID);

    // ForgeRegistries.BLOCKS is a registry of Block, not LiquidBlock.
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

    private static final FluidEntry ORIGINAL =
            registerFluid("void_original", VOID_ORIGINAL_TYPE, 4, 1);
    private static final FluidEntry IGNITED =
            registerFluid("void_ignited", VOID_IGNITED_TYPE, 4, 1);
    private static final FluidEntry METAL_MELT =
            registerFluid("void_metal_melt", VOID_METAL_TYPE, 2, 2);
    private static final FluidEntry ESSENCE =
            registerFluid("void_essence", VOID_ESSENCE_TYPE, 4, 1);
    private static final FluidEntry ENERGY =
            registerFluid("void_energy", VOID_ENERGY_TYPE, 4, 1);
    private static final FluidEntry CONCENTRATE =
            registerFluid("void_concentrate", VOID_CONCENTRATE_TYPE, 2, 2);

    public static final RegistryObject<ForgeFlowingFluid.Source> VOID_ORIGINAL = ORIGINAL.source;
    public static final RegistryObject<ForgeFlowingFluid.Flowing> VOID_ORIGINAL_FLOWING = ORIGINAL.flowing;
    public static final RegistryObject<Block> VOID_ORIGINAL_BLOCK = ORIGINAL.block;
    public static final ForgeFlowingFluid.Properties VOID_ORIGINAL_PROPERTIES = ORIGINAL.properties;

    public static final RegistryObject<ForgeFlowingFluid.Source> VOID_IGNITED = IGNITED.source;
    public static final RegistryObject<ForgeFlowingFluid.Flowing> VOID_IGNITED_FLOWING = IGNITED.flowing;
    public static final RegistryObject<Block> VOID_IGNITED_BLOCK = IGNITED.block;
    public static final ForgeFlowingFluid.Properties VOID_IGNITED_PROPERTIES = IGNITED.properties;

    public static final RegistryObject<ForgeFlowingFluid.Source> VOID_METAL_MELT = METAL_MELT.source;
    public static final RegistryObject<ForgeFlowingFluid.Flowing> VOID_METAL_MELT_FLOWING = METAL_MELT.flowing;
    public static final RegistryObject<Block> VOID_METAL_MELT_BLOCK = METAL_MELT.block;
    public static final ForgeFlowingFluid.Properties VOID_METAL_PROPERTIES = METAL_MELT.properties;

    public static final RegistryObject<ForgeFlowingFluid.Source> VOID_ESSENCE = ESSENCE.source;
    public static final RegistryObject<ForgeFlowingFluid.Flowing> VOID_ESSENCE_FLOWING = ESSENCE.flowing;
    public static final RegistryObject<Block> VOID_ESSENCE_BLOCK = ESSENCE.block;
    public static final ForgeFlowingFluid.Properties VOID_ESSENCE_PROPERTIES = ESSENCE.properties;

    public static final RegistryObject<ForgeFlowingFluid.Source> VOID_ENERGY = ENERGY.source;
    public static final RegistryObject<ForgeFlowingFluid.Flowing> VOID_ENERGY_FLOWING = ENERGY.flowing;
    public static final RegistryObject<Block> VOID_ENERGY_BLOCK = ENERGY.block;
    public static final ForgeFlowingFluid.Properties VOID_ENERGY_PROPERTIES = ENERGY.properties;

    public static final RegistryObject<ForgeFlowingFluid.Source> VOID_CONCENTRATE = CONCENTRATE.source;
    public static final RegistryObject<ForgeFlowingFluid.Flowing> VOID_CONCENTRATE_FLOWING = CONCENTRATE.flowing;
    public static final RegistryObject<Block> VOID_CONCENTRATE_BLOCK = CONCENTRATE.block;
    public static final ForgeFlowingFluid.Properties VOID_CONCENTRATE_PROPERTIES = CONCENTRATE.properties;

    private static FluidEntry registerFluid(
            String name,
            RegistryObject<FluidType> fluidType,
            int slopeFindDistance,
            int levelDecreasePerBlock) {

        SupplierWrapper<ForgeFlowingFluid.Source> sourceWrapper = new SupplierWrapper<>();
        SupplierWrapper<ForgeFlowingFluid.Flowing> flowingWrapper = new SupplierWrapper<>();
        SupplierWrapper<LiquidBlock> blockWrapper = new SupplierWrapper<>();

        ForgeFlowingFluid.Properties properties =
                new ForgeFlowingFluid.Properties(fluidType, sourceWrapper, flowingWrapper)
                        .slopeFindDistance(slopeFindDistance)
                        .levelDecreasePerBlock(levelDecreasePerBlock)
                        .block(blockWrapper);

        RegistryObject<ForgeFlowingFluid.Source> source =
                FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(properties));

        RegistryObject<ForgeFlowingFluid.Flowing> flowing =
                FLUIDS.register(name + "_flowing",
                        () -> new ForgeFlowingFluid.Flowing(properties));

        RegistryObject<Block> block =
                FLUID_BLOCKS.register(name, () -> new LiquidBlock(
                        source.get(),
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.COLOR_LIGHT_BLUE)
                                .replaceable()
                                .noCollission()
                                .strength(100.0F)
                                .noLootTable()));

        sourceWrapper.set(source);
        flowingWrapper.set(flowing);
        // RegistryObject<Block> is not a Supplier<LiquidBlock>; bridge it explicitly.
        blockWrapper.set(() -> (LiquidBlock) block.get());

        return new FluidEntry(source, flowing, block, properties);
    }

    private static final class FluidEntry {
        private final RegistryObject<ForgeFlowingFluid.Source> source;
        private final RegistryObject<ForgeFlowingFluid.Flowing> flowing;
        private final RegistryObject<Block> block;
        private final ForgeFlowingFluid.Properties properties;

        private FluidEntry(
                RegistryObject<ForgeFlowingFluid.Source> source,
                RegistryObject<ForgeFlowingFluid.Flowing> flowing,
                RegistryObject<Block> block,
                ForgeFlowingFluid.Properties properties) {
            this.source = source;
            this.flowing = flowing;
            this.block = block;
            this.properties = properties;
        }
    }

    private static final class SupplierWrapper<T> implements Supplier<T> {
        private Supplier<T> supplier;

        private void set(Supplier<T> supplier) {
            this.supplier = Objects.requireNonNull(supplier);
        }

        @Override
        public T get() {
            return Objects.requireNonNull(
                    supplier, "Fluid registry supplier has not been initialized").get();
        }
    }

    private ModFluids() {}
}
