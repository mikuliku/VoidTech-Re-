package com.voidtech.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializer for VoidTech's item + fluid recipes.
 *
 * JSON format:
 * {
 *   "type": "voidtech:fluid_crafting",
 *   "ingredients": [
 *     { "item": "voidtech:void_alloy" },
 *     { "item": "minecraft:diamond" }
 *   ],
 *   "fluid": {
 *     "fluid": "voidtech:void_ignited",
 *     "amount": 1000
 *   },
 *   "result": {
 *     "item": "voidtech:void_mining_machine_t2",
 *     "count": 1
 *   }
 * }
 */
public class VoidFluidCraftingRecipeSerializer
        implements RecipeSerializer<VoidFluidCraftingRecipe> {

    @Override
    public VoidFluidCraftingRecipe fromJson(ResourceLocation id, JsonObject json) {
        if (!json.has("fluid")) {
            throw new JsonParseException("VoidTech fluid_crafting recipe is missing 'fluid'.");
        }

        JsonObject fluidJson = json.getAsJsonObject("fluid");
        ResourceLocation fluidId = ResourceLocation.tryParse(
                fluidJson.get("fluid").getAsString());

        if (fluidId == null) {
            throw new JsonParseException("Invalid fluid id.");
        }

        int fluidAmount = fluidJson.has("amount")
                ? fluidJson.get("amount").getAsInt()
                : 1000;

        if (fluidAmount <= 0) {
            throw new JsonParseException("Fluid amount must be > 0.");
        }

        List<Ingredient> ingredients = new ArrayList<>();
        if (json.has("ingredients")) {
            for (var element : json.getAsJsonArray("ingredients")) {
                ingredients.add(Ingredient.fromJson(element));
            }
        }

        if (ingredients.isEmpty()) {
            throw new JsonParseException("VoidTech fluid_crafting recipe needs at least one item ingredient.");
        }

        ItemStack result = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
        if (result.isEmpty()) {
            throw new JsonParseException("VoidTech fluid_crafting recipe has an empty result.");
        }

        return new VoidFluidCraftingRecipe(id, ingredients, fluidId, fluidAmount, result);
    }

    @Override
    public VoidFluidCraftingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        int ingredientCount = buf.readVarInt();
        List<Ingredient> ingredients = new ArrayList<>(ingredientCount);

        for (int i = 0; i < ingredientCount; i++) {
            ingredients.add(Ingredient.fromNetwork(buf));
        }

        ResourceLocation fluidId = buf.readResourceLocation();
        int fluidAmount = buf.readVarInt();
        ItemStack result = buf.readItem();

        return new VoidFluidCraftingRecipe(
                id, ingredients, fluidId, fluidAmount, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, VoidFluidCraftingRecipe recipe) {
        buf.writeVarInt(recipe.getIngredients().size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buf);
        }

        buf.writeResourceLocation(recipe.getFluidId());
        buf.writeVarInt(recipe.getFluidAmount());
        buf.writeItem(recipe.getResultItem());
    }
}
