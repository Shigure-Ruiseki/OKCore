
package ruiseki.okcore.modcompat.efr;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.recipes.BlastFurnaceRecipes;
import ganymedes01.etfuturum.recipes.SmokerRecipes;
import ruiseki.okcore.event.recipes.RecipesRegisterEvent;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.modcompat.efr.recipe.type.cooking.blast.BlastFurnaceRecipe;
import ruiseki.okcore.modcompat.efr.recipe.type.cooking.smoker.SmokerRecipe;
import ruiseki.okcore.recipe.ingredient.ExcludedIngredient;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public class EFRCookingHelpers {

    private static final int DEFAULT_COOKING_TIME = 100;

    private EFRCookingHelpers() {}

    public static void load() {
        MinecraftForge.EVENT_BUS.register(new EFRCookingHelpers());
    }

    @SubscribeEvent
    public void onRecipesRegister(RecipesRegisterEvent event) {
        BlastFurnaceRecipes blast = BlastFurnaceRecipes.smelting();
        SmokerRecipes smoker = SmokerRecipes.smelting();

        blast.setReloadingCT(true);
        try {
            importBlastFurnaceRecipes(event, blast);
        } finally {
            blast.setReloadingCT(false);
        }

        smoker.setReloadingCT(true);
        try {
            importSmokerRecipes(event, smoker);
        } finally {
            smoker.setReloadingCT(false);
        }
    }

    private static void importBlastFurnaceRecipes(RecipesRegisterEvent event, BlastFurnaceRecipes recipes) {
        Collection<ItemStack> explicitInputs = recipes.smeltingList.keySet();

        Set<ResourceLocation> imported = new HashSet<>();

        // Explicit EFR recipes
        for (ItemStack input : explicitInputs) {
            importBlastFurnaceRecipe(event, recipes, input, createIngredient(input), imported);
        }

        // Vanilla furnace fallback recipes
        for (ItemStack input : FurnaceRecipes.smelting()
            .getSmeltingList()
            .keySet()) {
            importBlastFurnaceRecipe(event, recipes, input, createFallbackIngredient(input, explicitInputs), imported);
        }
    }

    private static void importSmokerRecipes(RecipesRegisterEvent event, SmokerRecipes recipes) {
        Collection<ItemStack> explicitInputs = recipes.smeltingList.keySet();

        Set<ResourceLocation> imported = new HashSet<>();

        // Explicit EFR recipes
        for (ItemStack input : explicitInputs) {
            importSmokerRecipe(event, recipes, input, createIngredient(input), imported);
        }

        // Vanilla furnace fallback recipes
        for (ItemStack input : FurnaceRecipes.smelting()
            .getSmeltingList()
            .keySet()) {
            importSmokerRecipe(event, recipes, input, createFallbackIngredient(input, explicitInputs), imported);
        }
    }

    private static void importBlastFurnaceRecipe(RecipesRegisterEvent event, BlastFurnaceRecipes recipes,
        ItemStack input, Ingredient ingredient, Set<ResourceLocation> imported) {
        if (ItemHelpers.isEmpty(input)) return;

        ItemStack output = recipes.getSmeltingResult(input);
        if (ItemHelpers.isEmpty(output)) return;

        ResourceLocation id = createRecipeId("blast_furnace", input, output);
        if (id == null || !imported.add(id)) return;

        float experience = recipes.getSmeltingExperience(output);

        BlastFurnaceRecipe recipe = new BlastFurnaceRecipe(
            id,
            ingredient,
            output.copy(),
            experience,
            DEFAULT_COOKING_TIME);
        recipe.setVanilla(true);
        event.addRecipe(recipe);
    }

    private static void importSmokerRecipe(RecipesRegisterEvent event, SmokerRecipes recipes, ItemStack input,
        Ingredient ingredient, Set<ResourceLocation> imported) {
        if (ItemHelpers.isEmpty(input)) return;

        ItemStack output = recipes.getSmeltingResult(input);
        if (ItemHelpers.isEmpty(output)) return;

        ResourceLocation id = createRecipeId("smoker", input, output);
        if (id == null || !imported.add(id)) return;

        float experience = recipes.getSmeltingExperience(output);

        SmokerRecipe recipe = new SmokerRecipe(id, ingredient, output.copy(), experience, DEFAULT_COOKING_TIME);
        recipe.setVanilla(true);
        event.addRecipe(recipe);
    }

    private static Ingredient createIngredient(ItemStack input) {
        return Ingredient.of(input.copy());
    }

    private static Ingredient createFallbackIngredient(ItemStack input, Collection<ItemStack> explicitInputs) {
        List<ItemStack> excluded = explicitInputs.stream()
            .filter(explicit -> explicit.getItem() == input.getItem())
            .filter(explicit -> ItemHelpers.getStackMeta(explicit) != OreDictionary.WILDCARD_VALUE)
            .collect(Collectors.toList());
        if (excluded.isEmpty()) return createIngredient(input);
        return new ExcludedIngredient(input, excluded);
    }

    private static ResourceLocation createRecipeId(String type, ItemStack input, ItemStack output) {
        if (ItemHelpers.isEmpty(input) || ItemHelpers.isEmpty(output)) return null;
        ResourceLocation inputId = Helpers.getLocation(input);
        ResourceLocation outputId = Helpers.getLocation(output);
        if (inputId == null || outputId == null) return null;
        String path = "generated/etfuturum/" + type
            + "/"
            + inputId.getResourceDomain()
            + "/"
            + inputId.getResourcePath()
            + "_"
            + ItemHelpers.getStackMeta(input)
            + "_to/"
            + outputId.getResourceDomain()
            + "/"
            + outputId.getResourcePath()
            + "_"
            + ItemHelpers.getStackMeta(output);
        return new ResourceLocation("okcore", path);
    }
}
