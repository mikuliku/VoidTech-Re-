package com.voidtech.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class VoidFluidCraftingRecipeSerializer
        implements RecipeSerializer<VoidFluidCraftingRecipe> {

    @Override
    public VoidFluidCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("fluid") || !json.has("result"))
            throw new JsonParseException("VoidTech fluid_crafting recipe requires fluid and result.");

        JsonObject fluid = json.getAsJsonObject("fluid");
        ResourceLocation fluidId = ResourceLocation.tryParse(fluid.get("fluid").getAsString());
        int amount = fluid.has("amount") ? fluid.get("amount").getAsInt() : 1000;

        if (fluidId == null || amount <= 0)
            throw new JsonParseException("Invalid VoidTech fluid requirement.");

        List<Ingredient> ingredients = new ArrayList<>();
        if (json.has("ingredients")) {
            for (var element : json.getAsJsonArray("ingredients"))
                ingredients.add(Ingredient.fromJson(element));
        }
        if (ingredients.isEmpty())
            throw new JsonParseException("VoidTech fluid_crafting needs item ingredients.");

        JsonObject resultJson = json.getAsJsonObject("result");
        ResourceLocation itemId = ResourceLocation.tryParse(resultJson.get("item").getAsString());
        int count = resultJson.has("count") ? resultJson.get("count").getAsInt() : 1;
        var item = itemId == null ? null : ForgeRegistries.ITEMS.getValue(itemId);

        if (item == null || count <= 0)
            throw new JsonParseException("Invalid VoidTech recipe result.");

        return new VoidFluidCraftingRecipe(
                id, ingredients, fluidId, amount, new ItemStack(item, count));
    }

    @Override
    public VoidFluidCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Ingredient> ingredients = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            ingredients.add(Ingredient.fromNetwork(buf));

        return new VoidFluidCraftingRecipe(
                id,
                ingredients,
                buf.readResourceLocation(),
                buf.readVarInt(),
                buf.readItem());
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, VoidFluidCraftingRecipe recipe) {
        buf.writeVarInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients())
            ingredient.toNetwork(buf);

        buf.writeResourceLocation(recipe.getFluidId());
        buf.writeVarInt(recipe.getFluidAmount());
        buf.writeItem(recipe.getResultItem(null));
    }
}
