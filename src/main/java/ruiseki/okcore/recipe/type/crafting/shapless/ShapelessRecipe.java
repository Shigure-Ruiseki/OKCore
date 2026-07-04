package ruiseki.okcore.recipe.type.crafting.shapless;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
        return RecipeRegistry.SHAPELESS_SERIALIZER;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShapelessRecipe that)) return false;
        if (!Objects.equals(this.id, that.id)) return false;
        if (!ItemStack.areItemStacksEqual(this.output, that.output)) return false;
        if (this.ingredients == null && that.ingredients == null) return true;
        if (this.ingredients == null || that.ingredients == null) return false;
        if (this.ingredients.size() != that.ingredients.size()) return false;
        List<CompoundItemMaterial> copyIngredients = new ArrayList<>(that.ingredients);
        for (CompoundItemMaterial ingredient : this.ingredients) {
            if (!copyIngredients.remove(ingredient)) {
                return false;
            }
        }

        return copyIngredients.isEmpty();
    }

    @Override
    public int hashCode() {
        int ingredientsHash = 0;
        if (this.ingredients != null) {
            for (CompoundItemMaterial ingredient : this.ingredients) {
                ingredientsHash += (ingredient != null ? ingredient.hashCode() : 0);
            }
        }

        int result = Objects.hash(id, ingredientsHash);

        if (output != null && output.getItem() != null) {
            result = 31 * result + Objects.hash(output.getItem(), output.getItemDamage(), output.stackSize);
            if (output.hasTagCompound()) {
                result = 31 * result + output.getTagCompound()
                    .hashCode();
            }
        }

        return result;
    }
}
