package ruiseki.okcore.recipe.type.crafting.shapless;

import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType.SHAPELESS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.json.item.IngredientMaterial;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeRegistry;

public class ShapelessRecipe implements IShapelessRecipe<InventoryCrafting> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final List<CompoundItemMaterial> ingredients;

    public ShapelessRecipe(ResourceLocation id, ItemStack output, List<CompoundItemMaterial> ingredients) {
        this.id = id;
        this.output = output.copy();
        this.ingredients = ingredients;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.getSerializer(SHAPELESS);
    }

    @Override
    public int getRecipeSize() {
        return this.ingredients.size();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public ItemStack getCraftingResultOK(InventoryCrafting inv) {
        return this.output.copy();
    }

    @Override
    public boolean matchesOK(InventoryCrafting inv, World world) {
        List<IngredientMaterial> requiredIngredients = new ArrayList<>(this.ingredients);

        for (int slotIndex = 0; slotIndex < inv.getSizeInventory(); slotIndex++) {
            ItemStack stackInSlot = inv.getStackInSlot(slotIndex);

            if (stackInSlot != null) {
                boolean matchedForSlot = false;
                Iterator<IngredientMaterial> ingredientIterator = requiredIngredients.iterator();
                while (ingredientIterator.hasNext()) {
                    IngredientMaterial ingredient = ingredientIterator.next();

                    if (ingredient.test(stackInSlot)) {
                        matchedForSlot = true;
                        ingredientIterator.remove();
                        break;
                    }
                }

                if (!matchedForSlot) {
                    return false;
                }
            }
        }

        return requiredIngredients.isEmpty();
    }

    public List<CompoundItemMaterial> getIngredients() {
        return this.ingredients;
    }
}
