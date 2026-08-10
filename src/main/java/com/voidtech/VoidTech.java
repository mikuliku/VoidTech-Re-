package com.voidtech;

import com.voidtech.registry.ModBlockEntities;
import com.voidtech.registry.ModBlocks;
import com.voidtech.registry.ModItems;
import com.voidtech.registry.ModMenus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VoidTech.MOD_ID)
public class VoidTech {

    public static final String MOD_ID = "voidtech";

    public VoidTech() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
    }
}
