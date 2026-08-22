package com.voidtech.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public final class VoidFluidCraftingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> ingredients;
    private final ResourceLocation fluidId;
    private final int fluidAmount;
    private final ItemStack result;

    public VoidFluidCraftingRecipe(ResourceLocation id, List<Ingredient> ingredients,
                                   ResourceLocation fluidId, int fluidAmount, ItemStack result) {
        this.id = id;
        this.ingredients = NonNullList.create();
        this.ingredients.addAll(ingredients);
        this.fluidId = fluidId;
        this.fluidAmount = fluidAmount;
        this.result = result.copy();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return matchesItems(container);
    }

    public boolean matchesItems(Container container) {
        boolean[] used = new boolean[container.getContainerSize()];
        for (Ingredient ingredient : ingredients) {
            boolean found = false;
            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                if (!used[slot] && ingredient.test(container.getItem(slot))) {
                    used[slot] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public boolean matchesFluid(FluidStack supplied) {
        if (supplied == null || supplied.isEmpty()) return false;
        var fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
        return fluid != null && supplied.getFluid() == fluid
                && supplied.getAmount() >= fluidAmount;
    }

    public ResourceLocation getFluidId() { return fluidId; }
    public int getFluidAmount() { return fluidAmount; }
    public NonNullList<Ingredient> getIngredients() { return ingredients; }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= ingredients.size();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FLUID_CRAFTING.get();
    }

    @Override
    public net.minecraft.world.item.crafting.RecipeType<?> getType() {
        return ModRecipeTypes.FLUID_CRAFTING;
    }
}
