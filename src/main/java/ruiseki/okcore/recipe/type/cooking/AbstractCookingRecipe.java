package ruiseki.okcore.recipe.type.cooking;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public abstract class AbstractCookingRecipe implements IRecipeOK<IInventory> {

    protected final IRecipeType<?> type;
    protected final ResourceLocation id;
    protected final Ingredient ingredient;
    protected final ItemStack result;
    protected final float experience;
    protected final int cookingTime;

    public AbstractCookingRecipe(IRecipeType<?> type, ResourceLocation id, Ingredient ingredient, ItemStack result,
        float experience, int cookingTime) {
        this.type = type;
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matchesOK(IInventory inventory, World world) {
        return this.ingredient.test(inventory.getStackInSlot(0));
    }

    @Override
    public ItemStack assemble(IInventory p_77572_1_) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    public float getExperience() {
        return this.experience;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeType<?> getType() {
        return this.type;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }
}
