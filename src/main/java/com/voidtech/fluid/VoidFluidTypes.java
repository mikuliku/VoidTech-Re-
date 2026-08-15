package com.voidtech.fluid;

import net.minecraftforge.fluids.FluidType;

public final class VoidFluidTypes {
    public static final FluidType PROPERTIES = new FluidType(
            FluidType.Properties.create()
                    .density(1000)
                    .viscosity(1000)
                    .temperature(300)
    );

    public static final FluidType HOT_PROPERTIES = new FluidType(
            FluidType.Properties.create()
                    .density(1200)
                    .viscosity(1200)
                    .temperature(1300)
    );

    public static final FluidType METAL_PROPERTIES = new FluidType(
            FluidType.Properties.create()
                    .density(7000)
                    .viscosity(6000)
                    .temperature(1600)
    );

    public static final FluidType ESSENCE_PROPERTIES = new FluidType(
            FluidType.Properties.create()
                    .density(900)
                    .viscosity(700)
                    .temperature(500)
    );

    public static final FluidType ENERGY_PROPERTIES = new FluidType(
            FluidType.Properties.create()
                    .density(500)
                    .viscosity(300)
                    .temperature(1000)
    );

    public static final FluidType CONCENTRATE_PROPERTIES = new FluidType(
            FluidType.Properties.create()
                    .density(1800)
                    .viscosity(1800)
                    .temperature(900)
    );

    private VoidFluidTypes() {}
}
