package com.voidtech;

import com.voidtech.network.VoidTechNetwork;
import com.voidtech.registry.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VoidTech.MOD_ID)
public class VoidTech {
    public static final String MOD_ID = "voidtech";

    public VoidTech() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenus.MENUS.register(bus);
        ModFluids.FLUID_TYPES.register(bus);
        ModFluids.FLUIDS.register(bus);
        ModFluids.FLUID_BLOCKS.register(bus);
        VoidTechNetwork.register();
    }
}
