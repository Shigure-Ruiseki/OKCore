package ruiseki.okcore.recipe.type.cooking.fuel;

import static ruiseki.okcore.recipe.type.cooking.fuel.FuelType.FUEL;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeDataBase;
import ruiseki.okcore.recipe.RecipeRegistry;

public class FuelRecipe extends RecipeDataBase {

    private final ItemStack input;
    private final int burnTime;

    public FuelRecipe(ResourceLocation id, ItemStack input, int burnTime) {
        super(id);
        this.input = input;
        this.burnTime = burnTime;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.getSerializer(FUEL);
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeRegistry.getType(FUEL);
    }

    public ItemStack getInput() {
        return this.input;
    }

    public int getBurnTime() {
        return this.burnTime;
    }

    public boolean matchesFuel(ItemStack currentInput) {
        if (this.input == null || currentInput == null) {
            return false;
        }
        return this.input.getItem() == currentInput.getItem()
            && (this.input.getItemDamage() == 32767 || this.input.getItemDamage() == currentInput.getItemDamage());
    }
}
