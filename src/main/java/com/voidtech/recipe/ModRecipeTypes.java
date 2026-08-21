package com.voidtech.recipe;

import net.minecraft.world.item.crafting.RecipeType;

public final class ModRecipeTypes {
    public static final RecipeType<VoidFluidCraftingRecipe> FLUID_CRAFTING =
            RecipeType.simple(new net.minecraft.resources.ResourceLocation(
                    com.voidtech.VoidTech.MOD_ID, "fluid_crafting"));

    private ModRecipeTypes() {}
}
