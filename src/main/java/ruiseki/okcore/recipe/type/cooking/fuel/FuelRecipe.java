package ruiseki.okcore.recipe.type.cooking.fuel;

import java.util.Objects;

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
    public IRecipeSerializer<FuelRecipe> getSerializer() {
        return RecipeRegistry.FUEL_SERIALIZER;
    }

    @Override
    public IRecipeType<FuelRecipe> getType() {
        return RecipeRegistry.FUEL_TYPE;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuelRecipe that)) return false;
        if (!Objects.equals(this.getId(), that.getId())) return false;
        if (this.burnTime != that.burnTime) return false;
        return ItemStack.areItemStacksEqual(this.input, that.input);
    }

    @Override
    public int hashCode() {
        int resultHash = Objects.hash(this.getId(), burnTime);
        if (this.input != null && this.input.getItem() != null) {
            resultHash = 31 * resultHash
                + Objects.hash(this.input.getItem(), this.input.getItemDamage(), this.input.stackSize);
            if (this.input.hasTagCompound()) {
                resultHash = 31 * resultHash + this.input.getTagCompound()
                    .hashCode();
            }
        }
        return resultHash;
    }
}
