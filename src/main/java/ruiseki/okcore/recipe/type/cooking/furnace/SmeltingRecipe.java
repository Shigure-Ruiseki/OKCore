package ruiseki.okcore.recipe.type.cooking.furnace;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.type.cooking.AbstractCookingRecipe;

public class SmeltingRecipe extends AbstractCookingRecipe {

    protected SmeltingRecipe(ResourceLocation id, ItemStack result, CompoundItemMaterial ingredient, float experience,
        int cookingTime) {
        super(id, result, ingredient, experience, cookingTime);
    }

    @Override
    public IRecipeSerializer<SmeltingRecipe> getSerializer() {
        return RecipeRegistry.SMELTING_SERIALIZER;
    }

    @Override
    public IRecipeType<SmeltingRecipe> getType() {
        return RecipeRegistry.SMELTING_TYPE;
    }
}
