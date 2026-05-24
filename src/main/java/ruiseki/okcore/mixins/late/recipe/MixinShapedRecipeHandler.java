package ruiseki.okcore.mixins.late.recipe;

import java.util.List;
import java.util.Map;

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
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;

@Mixin(value = ShapedRecipeHandler.class, remap = false)
public abstract class MixinShapedRecipeHandler extends TemplateRecipeHandler {

    @Inject(
        method = "loadCraftingRecipes(Ljava/lang/String;[Ljava/lang/Object;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/crafting/CraftingManager;getRecipeList()Ljava/util/List;",
            shift = At.Shift.AFTER))
    private void injectLoadCraftingRecipes(String outputId, Object[] results, CallbackInfo ci) {
        ShapedRecipeHandler handler = (ShapedRecipeHandler) (Object) this;

        if (outputId.equals("crafting") && handler.getClass() == ShapedRecipeHandler.class) {
            for (IRecipe irecipe : CraftingManager.getInstance()
                .getRecipeList()) {
                if (irecipe instanceof ShapedRecipe shaped) {
                    ShapedRecipeHandler.CachedShapedRecipe recipe = okcore$createNeiCachedRecipe(handler, shaped);
                    if (recipe != null) {
                        recipe.computeVisuals();
                        handler.arecipes.add(recipe);
                    }
                }
            }
        } else if (outputId.equals("crafting2x2") && handler.getClass() == ShapedRecipeHandler.class) {
            for (IRecipe irecipe : CraftingManager.getInstance()
                .getRecipeList()) {
                if (irecipe instanceof ShapedRecipe shaped && shaped.getRecipeWidth() <= 2
                    && shaped.getRecipeHeight() <= 2) {
                    ShapedRecipeHandler.CachedShapedRecipe recipe = okcore$createNeiCachedRecipe(handler, shaped);
                    if (recipe != null) {
                        recipe.computeVisuals();
                        handler.arecipes.add(recipe);
                    }
                }
            }
        }
    }

    @Inject(method = "loadCraftingRecipes(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private void injectLoadCraftingRecipesByResult(ItemStack result, CallbackInfo ci) {
        ShapedRecipeHandler handler = (ShapedRecipeHandler) (Object) this;

        for (IRecipe irecipe : CraftingManager.getInstance()
            .getRecipeList()) {
            if (irecipe instanceof ShapedRecipe shaped) {
                if (NEIServerUtils.areStacksSameTypeCrafting(shaped.getRecipeOutput(), result)) {
                    ShapedRecipeHandler.CachedShapedRecipe recipe = okcore$createNeiCachedRecipe(handler, shaped);
                    if (recipe == null) continue;

                    recipe.computeVisuals();
                    handler.arecipes.add(recipe);
                }
            }
        }
    }

    @Inject(method = "loadUsageRecipes(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    private void injectLoadUsageRecipes(ItemStack ingredient, CallbackInfo ci) {
        ShapedRecipeHandler handler = (ShapedRecipeHandler) (Object) this;

        for (IRecipe irecipe : CraftingManager.getInstance()
            .getRecipeList()) {
            if (irecipe instanceof ShapedRecipe shaped) {
                ShapedRecipeHandler.CachedShapedRecipe recipe = okcore$createNeiCachedRecipe(handler, shaped);
                if (recipe == null || !recipe.contains(recipe.ingredients, ingredient.getItem())) continue;

                recipe.computeVisuals();
                if (recipe.contains(recipe.ingredients, ingredient)) {
                    recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                    handler.arecipes.add(recipe);
                }
            }
        }
    }

    @Unique
    private ShapedRecipeHandler.CachedShapedRecipe okcore$createNeiCachedRecipe(ShapedRecipeHandler handler,
        ShapedRecipe recipe) {
        try {
            int width = recipe.getRecipeWidth();
            int height = recipe.getRecipeHeight();
            String[] pattern = recipe.getPattern();
            Map<Character, CompoundItemMaterial> keyMap = recipe.getKeyMap();

            Object[] rawItems = new Object[width * height];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < pattern[i].length(); j++) {
                    char chr = pattern[i].charAt(j);
                    CompoundItemMaterial materials = keyMap.get(chr);
                    if (materials == null || materials.isEmpty()) {
                        rawItems[j + i * width] = null;
                        continue;
                    }

                    List<ItemStack> displayStacks = materials.toStacks();
                    if (displayStacks.isEmpty()) {
                        return null;
                    }

                    rawItems[j + i * width] = (displayStacks.size() == 1) ? displayStacks.getFirst() : displayStacks;
                }
            }

            return handler.new CachedShapedRecipe(width, height, rawItems, recipe.getRecipeOutput());

        } catch (Exception e) {
            NEIClientConfig.logger.error("Error injecting custom OKCore recipe into NEI: ", e);
            return null;
        }
    }
}
