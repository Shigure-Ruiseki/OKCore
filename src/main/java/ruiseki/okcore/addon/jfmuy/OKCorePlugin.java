package ruiseki.okcore.addon.jfmuy;

import ruiseki.jfmuy.api.IModPlugin;
import ruiseki.jfmuy.api.IModRegistry;
import ruiseki.jfmuy.api.JFMUYPlugin;
import ruiseki.jfmuy.api.recipe.VanillaRecipeCategoryUid;
import ruiseki.okcore.addon.jfmuy.crafting.ShapedRecipeWrapper;
import ruiseki.okcore.addon.jfmuy.crafting.ShapelessRecipeWrapper;
import ruiseki.okcore.addon.jfmuy.crafting.SpecialRecipeWrapper;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipe;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;

@JFMUYPlugin
public class OKCorePlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        registry.handleRecipes(ShapelessRecipe.class, ShapelessRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(ShapedRecipe.class, ShapedRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
        registry.handleRecipes(SpecialRecipe.class, SpecialRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);
    }
}
