package ruiseki.okcore.mixins.early.recipe;

import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;
import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType.SHAPELESS;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;

@Mixin(CraftingManager.class)
public class MixinCraftingManager {

    @Inject(method = "findMatchingRecipe", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings("unchecked")
    private void okcore$onFindMatchingRecipe(InventoryCrafting inv, World world,
        CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() != null) return;

        IRecipeType<ShapedRecipe> shaped = RecipeRegistry.getType(SHAPED);
        RecipeManager.getManager()
            .getRecipeFor(shaped, inv, world)
            .ifPresent(recipe -> cir.setReturnValue(recipe.getCraftingResult(inv)));

        if (cir.getReturnValue() != null) return;

        IRecipeType<ShapelessRecipe> shapeless = RecipeRegistry.getType(SHAPELESS);
        RecipeManager.getManager()
            .getRecipeFor(shapeless, inv, world)
            .ifPresent(recipe -> cir.setReturnValue(recipe.getCraftingResult(inv)));
    }

    @Inject(method = "getRecipeList", at = @At("RETURN"), cancellable = true)
    private void okcore$onGetRecipeList(CallbackInfoReturnable<List<IRecipe>> cir) {
        List<IRecipe> vanillaList = cir.getReturnValue();
        if (vanillaList == null) return;

        cir.setReturnValue(new AbstractList<IRecipe>() {

            private Object[] getCustomRecipes() {
                Collection<ShapedRecipe> currentShaped = RecipeManager.getManager()
                    .getShapedRecipes();
                Collection<ShapelessRecipe> currentShapeless = RecipeManager.getManager()
                    .getShapelessRecipes();

                int size = (currentShaped != null ? currentShaped.size() : 0)
                    + (currentShapeless != null ? currentShapeless.size() : 0);
                if (size == 0) return new Object[0];

                Object[] result = new Object[size];
                int idx = 0;
                if (currentShaped != null) {
                    for (ShapedRecipe r : currentShaped) {
                        if (r != null && r.getRecipeOutput() != null
                            && r.getRecipeOutput()
                                .getItem() != null) {
                            result[idx++] = r;
                        }
                    }
                }
                if (currentShapeless != null) {
                    for (ShapelessRecipe r : currentShapeless) {
                        if (r != null && r.getRecipeOutput() != null
                            && r.getRecipeOutput()
                                .getItem() != null) {
                            result[idx++] = r;
                        }
                    }
                }

                if (idx < size) {
                    Object[] trimmed = new Object[idx];
                    System.arraycopy(result, 0, trimmed, 0, idx);
                    return trimmed;
                }
                return result;
            }

            @Override
            public IRecipe get(int index) {
                int vanillaSize = vanillaList.size();
                if (index < vanillaSize) {
                    return vanillaList.get(index);
                }

                Object[] custom = getCustomRecipes();
                int customIndex = index - vanillaSize;
                if (customIndex < custom.length) {
                    return (IRecipe) custom[customIndex];
                }
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
            }

            @Override
            public int size() {
                return vanillaList.size() + getCustomRecipes().length;
            }

            @Override
            public boolean add(IRecipe recipe) {
                return vanillaList.add(recipe);
            }

            @Override
            public IRecipe remove(int index) {
                int vanillaSize = vanillaList.size();
                if (index < vanillaSize) {
                    return vanillaList.remove(index);
                }
                return null;
            }
        });
    }
}
