package ruiseki.okcore.recipe.type.cooking;

import java.util.Objects;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.recipe.IRecipeOK;

public abstract class AbstractCookingRecipe implements IRecipeOK<IInventory> {

    protected final ResourceLocation id;
    protected final ItemStack result;
    protected final CompoundItemMaterial ingredient;
    protected final float experience;
    protected final int cookingTime;

    protected AbstractCookingRecipe(ResourceLocation id, ItemStack result, CompoundItemMaterial ingredient,
        float experience, int cookingTime) {
        this.id = id;
        this.result = result;
        this.ingredient = ingredient;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean matchesOK(IInventory inventory, World world) {
        if (inventory == null || inventory.getSizeInventory() == 0) {
            return false;
        }

        ItemStack inputStack = inventory.getStackInSlot(0);
        if (inputStack == null) {
            return false;
        }

        if (this.ingredient == null || this.ingredient.isEmpty()) {
            return false;
        }

        return this.ingredient.test(inputStack);
    }

    @Override
    public ItemStack getCraftingResultOK(IInventory inventory) {
        return this.result.copy();
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.result;
    }

    public float getExperience() {
        return this.experience;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    public CompoundItemMaterial getIngredient() {
        return this.ingredient;
    }

    public boolean matchesExperience(ItemStack currentInput) {
        if (this.result == null || currentInput == null) {
            return false;
        }
        return this.result.getItem() == currentInput.getItem()
            && (this.result.getItemDamage() == 32767 || this.result.getItemDamage() == currentInput.getItemDamage());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        AbstractCookingRecipe that = (AbstractCookingRecipe) o;
        if (!Objects.equals(this.id, that.id)) return false;
        if (this.cookingTime != that.cookingTime) return false;
        if (Float.compare(that.experience, this.experience) != 0) return false;
        if (!Objects.equals(this.ingredient, that.ingredient)) return false;
        return ItemStack.areItemStacksEqual(this.result, that.result);
    }

    @Override
    public int hashCode() {
        int resultHash = Objects.hash(id, ingredient, experience, cookingTime);
        if (result != null && result.getItem() != null) {
            resultHash = 31 * resultHash + Objects.hash(result.getItem(), result.getItemDamage(), result.stackSize);
            if (result.hasTagCompound()) {
                resultHash = 31 * resultHash + result.getTagCompound()
                    .hashCode();
            }
        }
        return resultHash;
    }
}
