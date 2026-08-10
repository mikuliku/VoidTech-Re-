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
            "void_frame",
            () -> new BlockItem(
                    ModBlocks.VOID_FRAME.get(),
                    new Item.Properties()
            )
    );

    private ModItems() {
    }
}
