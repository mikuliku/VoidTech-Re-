package com.voidtech.recipe;

import net.minecraft.world.item.crafting.RecipeType;

/**
 * VoidTech recipe types.
 *
 * fluid_crafting is deliberately separate from vanilla crafting:
 * it requires both item ingredients and a FluidStack requirement.
 */
public final class ModRecipeTypes {
    public static final RecipeType<VoidFluidCraftingRecipe> FLUID_CRAFTING =
            RecipeType.simple(com.voidtech.VoidTech.id("fluid_crafting"));

    private ModRecipeTypes() {}
}
