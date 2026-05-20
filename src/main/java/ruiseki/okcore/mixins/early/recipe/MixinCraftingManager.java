package ruiseki.okcore.mixins.early.recipe;

import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;
import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType.SHAPELESS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistries;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipesOK;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipesOK;

@Mixin(CraftingManager.class)
public class MixinCraftingManager {

    @Unique private static List<IRecipe> okcore$cachedCustomRecipes = new ArrayList<>();
    @Unique private static List<IRecipe> okcore$finalCombinedList = null;
    @Unique private static Object okcore$lastShapedCollection = null;
    @Unique private static Object okcore$lastShapelessCollection = null;
    @Unique private static int okcore$lastVanillaSize = -1;

    @Inject(method = "findMatchingRecipe", at = @At("HEAD"), cancellable = true)
    @SuppressWarnings("unchecked")
    private void onFindMatchingRecipe(InventoryCrafting inv, World world, CallbackInfoReturnable<ItemStack> cir) {
        IRecipeType<ShapedRecipesOK> shaped = (IRecipeType<ShapedRecipesOK>) RecipeRegistries.getType(SHAPED);
        RecipeManager.getManager()
            .getRecipeFor(shaped, inv, world)
            .ifPresent(recipe -> cir.setReturnValue(recipe.getCraftingResult(inv)));

        IRecipeType<ShapelessRecipesOK> shapeless = (IRecipeType<ShapelessRecipesOK>) RecipeRegistries
            .getType(SHAPELESS);
        RecipeManager.getManager()
            .getRecipeFor(shapeless, inv, world)
            .ifPresent(recipe -> cir.setReturnValue(recipe.getCraftingResult(inv)));
    }

    @Inject(method = "getRecipeList", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings("unchecked")
    private void okcore$onGetRecipeList(CallbackInfoReturnable<List<IRecipe>> cir) {
        List<IRecipe> vanillaList = cir.getReturnValue();
        if (vanillaList == null) return;

        Collection<ShapedRecipesOK> currentShaped = RecipeManager.getManager().getShapedRecipes();
        Collection<ShapelessRecipesOK> currentShapeless = RecipeManager.getManager().getShapelessRecipes();

        boolean recipesChanged = (currentShaped != okcore$lastShapedCollection || currentShapeless != okcore$lastShapelessCollection);
        boolean vanillaChanged = (vanillaList.size() != okcore$lastVanillaSize);

        if (recipesChanged || vanillaChanged || okcore$finalCombinedList == null) {
            synchronized (MixinCraftingManager.class) {
                if (vanillaList.size() != okcore$lastVanillaSize || currentShaped != okcore$lastShapedCollection || currentShapeless != okcore$lastShapelessCollection || okcore$finalCombinedList == null) {
                    if (recipesChanged) {
                        okcore$cachedCustomRecipes = new ArrayList<>();
                        if (currentShaped != null) {
                            for (ShapedRecipesOK recipe : currentShaped) {
                                if (recipe == null || recipe.getRecipeOutput() == null || recipe.getRecipeOutput().getItem() == null) continue;
                                okcore$cachedCustomRecipes.add(recipe);
                            }
                        }

                        if (currentShapeless != null) {
                            for (ShapelessRecipesOK recipe : currentShapeless) {
                                if (recipe == null || recipe.getRecipeOutput() == null || recipe.getRecipeOutput().getItem() == null) continue;
                                okcore$cachedCustomRecipes.add(recipe);
                            }
                        }
                        okcore$lastShapedCollection = currentShaped;
                        okcore$lastShapelessCollection = currentShapeless;
                    }

                    List<IRecipe> newCombined = new ArrayList<>(vanillaList.size() + okcore$cachedCustomRecipes.size());
                    newCombined.addAll(vanillaList);
                    newCombined.addAll(okcore$cachedCustomRecipes);

                    okcore$finalCombinedList = newCombined;
                    okcore$lastVanillaSize = vanillaList.size();
                }
            }
        }

        cir.setReturnValue(okcore$finalCombinedList);
    }
}
