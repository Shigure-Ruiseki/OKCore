package ruiseki.okcore.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.data.loader.recipes.IRecipeSerializer;
import ruiseki.okcore.data.loader.recipes.IRecipeType;

public abstract class RecipeDataBase implements IRecipe {

    protected final ResourceLocation id;

    public RecipeDataBase(ResourceLocation id) {
        this.id = id;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public abstract IRecipeType<?> getRecipeType();

    public abstract IRecipeSerializer<?> getSerializer();

    @Override
    public boolean matches(InventoryCrafting crafting, World world) {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting crafting) {
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
