package ruiseki.okcore.recipe.type.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class SpecialRecipe implements ICraftingRecipe {

    private final ResourceLocation id;

    public SpecialRecipe(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public boolean isSpecial() {
        return true;
    }

    public ItemStack getResultItem() {
        return null;
    }
}
