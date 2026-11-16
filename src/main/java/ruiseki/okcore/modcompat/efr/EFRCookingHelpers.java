package ruiseki.okcore.modcompat.efr;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.recipes.BlastFurnaceRecipes;
import ganymedes01.etfuturum.recipes.SmokerRecipes;
import ruiseki.okcore.event.recipes.RecipesRegisterEvent;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.modcompat.efr.recipe.type.cooking.blast.BlastFurnaceRecipe;
import ruiseki.okcore.modcompat.efr.recipe.type.cooking.smoker.SmokerRecipe;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public class EFRCookingHelpers {

    private static final int DEFAULT_COOKING_TIME = 100;

    private EFRCookingHelpers() {}

    public static void load() {
        MinecraftForge.EVENT_BUS.register(new EFRCookingHelpers());
    }

    @SubscribeEvent
    public void onRecipesRegister(RecipesRegisterEvent event) {
        importBlastFurnaceRecipes(event);
        importSmokerRecipes(event);
    }

    private static void importBlastFurnaceRecipes(RecipesRegisterEvent event) {
        BlastFurnaceRecipes recipes = BlastFurnaceRecipes.smelting();
        Set<ResourceLocation> imported = new HashSet<>();

        // Include vanilla furnace inputs for EFR auto-add recipes.
        for (ItemStack input : FurnaceRecipes.smelting()
            .getSmeltingList()
            .keySet()) {
            importBlastFurnaceRecipe(event, recipes, input, imported);
        }

        // Include EFR explicit recipes, including inputs absent from vanilla.
        for (ItemStack input : recipes.smeltingList.keySet()) {
            importBlastFurnaceRecipe(event, recipes, input, imported);
        }
    }

    private static void importBlastFurnaceRecipe(RecipesRegisterEvent event, BlastFurnaceRecipes recipes,
        ItemStack input, Set<ResourceLocation> imported) {

        if (ItemHelpers.isEmpty(input)) return;

        ItemStack output = recipes.getSmeltingResult(input);
        if (ItemHelpers.isEmpty(output)) return;

        ResourceLocation id = createRecipeId("blast_furnace", input, output);
        if (id == null || !imported.add(id)) return;

        float experience = recipes.getSmeltingExperience(output);

        BlastFurnaceRecipe recipe = new BlastFurnaceRecipe(
            id,
            createIngredient(input),
            output.copy(),
            experience,
            DEFAULT_COOKING_TIME).setVanilla(true);

        event.addRecipe(recipe);
    }

    private static void importSmokerRecipes(RecipesRegisterEvent event) {
        SmokerRecipes recipes = SmokerRecipes.smelting();
        Set<ResourceLocation> imported = new HashSet<>();
        for (ItemStack input : FurnaceRecipes.smelting()
            .getSmeltingList()
            .keySet()) {
            importSmokerRecipe(event, recipes, input, imported);
        }
        for (ItemStack input : recipes.smeltingList.keySet()) {
            importSmokerRecipe(event, recipes, input, imported);
        }
    }

    private static void importSmokerRecipe(RecipesRegisterEvent event, SmokerRecipes recipes, ItemStack input,
        Set<ResourceLocation> imported) {

        if (ItemHelpers.isEmpty(input)) return;

        ItemStack output = recipes.getSmeltingResult(input);
        if (ItemHelpers.isEmpty(output)) return;

        ResourceLocation id = createRecipeId("smoker", input, output);
        if (id == null || !imported.add(id)) return;

        float experience = recipes.getSmeltingExperience(output);

        SmokerRecipe recipe = new SmokerRecipe(
            id,
            createIngredient(input),
            output.copy(),
            experience,
            DEFAULT_COOKING_TIME).setVanilla(true);

        event.addRecipe(recipe);
    }

    private static Ingredient createIngredient(ItemStack input) {
        return Ingredient.of(input.copy());
    }

    private static ResourceLocation createRecipeId(String type, ItemStack input, ItemStack output) {

        if (ItemHelpers.isEmpty(input) || ItemHelpers.isEmpty(output)) return null;

        ResourceLocation inputId = Helpers.getLocation(input);
        ResourceLocation outputId = Helpers.getLocation(output);

        if (inputId == null || outputId == null) return null;

        return new ResourceLocation(
            "okcore",
            "generated/etfuturum/" + type
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
                + ItemHelpers.getStackMeta(output));
    }
}
