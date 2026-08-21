package com.voidtech.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * Small helpers used by machine implementations.
 */
public final class VoidRecipeHelper {
    private VoidRecipeHelper() {}

    public static boolean matches(
            VoidFluidCraftingRecipe recipe,
            List<ItemStack> items,
            FluidStack fluid) {

        if (!recipe.matchesFluid(fluid)) return false;

        boolean[] used = new boolean[items.size()];

        for (Ingredient ingredient : recipe.getIngredients()) {
            boolean found = false;

            for (int i = 0; i < items.size(); i++) {
                if (used[i]) continue;
                if (ingredient.test(items.get(i))) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }

            if (!found) return false;
        }

        return true;
    }
}
