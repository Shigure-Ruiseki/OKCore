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
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.recipe.ingredient.Ingredient;
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
        if (okcore$getThis().getClass() != ShapelessRecipeHandler.class) return;
        boolean crafting = "crafting".equals(outputId);
        boolean crafting2x2 = "crafting2x2".equals(outputId);
        if (!crafting && !crafting2x2) return;
        ShapelessRecipeHandler handler = (ShapelessRecipeHandler) (Object) this;
        for (IRecipe recipe : CraftingManager.getInstance()
            .getRecipeList()) {
            if (!(recipe instanceof ShapelessRecipe shapeless)) continue;
            if (crafting2x2 && shapeless.getRecipeSize() > 4) continue;
            ShapelessRecipeHandler.CachedShapelessRecipe cached = okcore$createNeiCachedRecipe(handler, shapeless);
            if (cached != null) handler.arecipes.add(cached);
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
                if (recipe == null || !recipe.contains(recipe.ingredients, ItemHelpers.getItem(ingredient))) continue;

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
            List<Ingredient> ingredients = recipe.getIngredients();
            if (ingredients == null || ingredients.isEmpty()) return null;

            List<Object> rawInputs = new ArrayList<>();

            for (Ingredient ingredient : ingredients) {
                if (ingredient == null || ingredient.isEmpty()) continue;

                ItemStack[] items = ingredient.getItems();
                if (items == null || items.length == 0) return null;

                List<ItemStack> stacks = new ArrayList<>();
                for (ItemStack stack : items) {
                    if (!ItemHelpers.isEmpty(stack)) {
                        stacks.add(stack);
                    }
                }

                if (stacks.isEmpty()) return null;

                rawInputs.add(stacks.size() == 1 ? stacks.getFirst() : stacks);
            }

            if (rawInputs.isEmpty()) return null;

            return handler.new CachedShapelessRecipe(rawInputs, recipe.getRecipeOutput());

        } catch (Exception e) {
            NEIClientConfig.logger.error("Error injecting custom OKCore shapeless recipe into NEI: ", e);
            return null;
        }
    }

    @Unique
    private ShapelessRecipeHandler okcore$getThis() {
        return (ShapelessRecipeHandler) (Object) this;
    }
}
