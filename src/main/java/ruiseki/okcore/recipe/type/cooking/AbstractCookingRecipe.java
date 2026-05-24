package ruiseki.okcore.recipe.type.cooking;

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
}
