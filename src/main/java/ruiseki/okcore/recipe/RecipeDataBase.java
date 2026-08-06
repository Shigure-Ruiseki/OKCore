package ruiseki.okcore.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class RecipeDataBase implements IRecipeOK<IInventory> {

    protected final ResourceLocation id;

    public RecipeDataBase(ResourceLocation id) {
        this.id = id;

        if (this.getSerializer() == null) {
            throw new IllegalStateException(
                "No serializer found for " + this.getClass()
                    .getName());
        }

        if (this.getType() == null) {
            throw new IllegalStateException(
                "No recipe type found for " + this.getClass()
                    .getName());
        }
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean matchesOK(IInventory crafting, World world) {
        return false;
    }

    @Override
    public ItemStack assemble(IInventory crafting) {
        return null;
    }

    @Override
    public int getRecipeSize() {
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }
}
