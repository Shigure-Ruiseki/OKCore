package ruiseki.okcore.recipe.type.crafting.shapless;

import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeSerializer.SHAPELESS_RECIPE;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeRegistry;

public class ShapelessRecipesOK extends ShapelessOreRecipe implements IShapelessRecipe<InventoryCrafting> {

    private final ResourceLocation id;
    private final List<Object> ingredients;

    public ShapelessRecipesOK(ResourceLocation id, ItemStack result, List<Object> ingredients) {
        super(result, ingredients.toArray());
        this.id = id;
        this.ingredients = ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public List<Object> getIngredients() {
        return ingredients;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.getSerializer(SHAPELESS_RECIPE);
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return super.matches(inventory, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        return super.getCraftingResult(inventory);
    }
}
