package com.voidtech.recipe;

import com.voidtech.VoidTech;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, VoidTech.MOD_ID);

    public static final RegistryObject<RecipeSerializer<VoidFluidCraftingRecipe>> FLUID_CRAFTING =
            SERIALIZERS.register("fluid_crafting",
                    VoidFluidCraftingRecipeSerializer::new);

    private ModRecipeSerializers() {}
}
