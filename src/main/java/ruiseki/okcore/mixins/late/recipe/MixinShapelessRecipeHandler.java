package ruiseki.okcore.mixins.late.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;

@Mixin(value = ShapelessRecipeHandler.class, remap = false)
public abstract class MixinShapelessRecipeHandler extends TemplateRecipeHandler {

    @Inject(
        method = "loadCraftingRecipes(Ljava/lang/String;[Ljava/lang/Object;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/crafting/CraftingManager;getRecipeList()Ljava/util/List;",
            shift = At.Shift.AFTER))
    private void injectLoadCraftingRecipes(String outputId, Object[] results, CallbackInfo ci) {
        ShapelessRecipeHandler handler = (ShapelessRecipeHandler) (Object) this;

        if (outputId.equals("crafting") && handler.getClass() == ShapelessRecipeHandler.class) {
            for (IRecipe irecipe : CraftingManager.getInstance()
                .getRecipeList()) {
                if (irecipe instanceof ShapelessRecipe shapeless) {
                    ShapelessRecipeHandler.CachedShapelessRecipe recipe = okcore$createNeiCachedRecipe(
                        handler,
                        shapeless);
                    if (recipe != null) {
                        handler.arecipes.add(recipe);
                    }
                }
            }
        } else if (outputId.equals("crafting2x2") && handler.getClass() == ShapelessRecipeHandler.class) {
            for (IRecipe irecipe : CraftingManager.getInstance()
                .getRecipeList()) {
                if (irecipe instanceof ShapelessRecipe shapeless && shapeless.getRecipeSize() <= 4) {
                    ShapelessRecipeHandler.CachedShapelessRecipe recipe = okcore$createNeiCachedRecipe(
                        handler,
                        shapeless);
                    if (recipe != null) {
                        handler.arecipes.add(recipe);
                    }
                }
            }
        }
    }

    @Inject(method = "loadCraftingRecipes(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private void injectLoadCraftingRecipesByResult(ItemStack result, CallbackInfo ci) {
        ShapelessRecipeHandler handler = (ShapelessRecipeHandler) (Object) this;

        for (IRecipe irecipe : CraftingManager.getInstance()
            .getRecipeList()) {
            if (irecipe instanceof ShapelessRecipe shapeless) {
                if (NEIServerUtils.areStacksSameTypeCrafting(shapeless.getRecipeOutput(), result)) {
                    ShapelessRecipeHandler.CachedShapelessRecipe recipe = okcore$createNeiCachedRecipe(
                        handler,
                        shapeless);
                    if (recipe != null) {
                        handler.arecipes.add(recipe);
                    }
                }
            }
        }
    }

    @Inject(method = "loadUsageRecipes(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private void injectLoadUsageRecipes(ItemStack ingredient, CallbackInfo ci) {
        ShapelessRecipeHandler handler = (ShapelessRecipeHandler) (Object) this;

        for (IRecipe irecipe : CraftingManager.getInstance()
            .getRecipeList()) {
            if (irecipe instanceof ShapelessRecipe shapeless) {
                ShapelessRecipeHandler.CachedShapelessRecipe recipe = okcore$createNeiCachedRecipe(handler, shapeless);
                if (recipe == null || !recipe.contains(recipe.ingredients, ingredient.getItem())) continue;

                if (recipe.contains(recipe.ingredients, ingredient)) {
                    recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                    handler.arecipes.add(recipe);
                }
            }
        }
    }

    @Unique
    private ShapelessRecipeHandler.CachedShapelessRecipe okcore$createNeiCachedRecipe(ShapelessRecipeHandler handler,
        ShapelessRecipe recipe) {
        try {
            List<CompoundItemMaterial> ingredients = recipe.getIngredients();
            if (ingredients == null || ingredients.isEmpty()) {
                return null;
            }

            List<Object> rawInputs = new ArrayList<>();

            for (CompoundItemMaterial ingredient : ingredients) {
                if (ingredient.isEmpty()) continue;

                List<ItemStack> displayStacks = ingredient.toStacks();
                if (displayStacks.isEmpty()) {
                    return null;
                }

                rawInputs.add((displayStacks.size() == 1) ? displayStacks.getFirst() : displayStacks);
            }

            return handler.new CachedShapelessRecipe(rawInputs, recipe.getRecipeOutput());

        } catch (Exception e) {
            NEIClientConfig.logger.error("Error injecting custom OKCore shapeless recipe into NEI: ", e);
            return null;
        }
    }
}
