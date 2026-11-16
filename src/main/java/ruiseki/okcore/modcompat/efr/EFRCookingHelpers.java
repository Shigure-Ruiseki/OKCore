package ruiseki.okcore.modcompat.efr;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ganymedes01.etfuturum.recipes.BlastFurnaceRecipes;
import ganymedes01.etfuturum.recipes.SmokerRecipes;
import ruiseki.okcore.event.recipes.RecipesRegisterEvent;
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

        for (Map.Entry<ItemStack, ItemStack> entry : recipes.smeltingList.entrySet()) {
            ItemStack input = entry.getKey();
            ItemStack output = entry.getValue();

            if (ItemHelpers.isEmpty(input) || ItemHelpers.isEmpty(output)) continue;

            ResourceLocation id = createRecipeId("blast_furnace", input, output);
            if (id == null) continue;

            Float experience = recipes.experienceList.get(output);

            BlastFurnaceRecipe recipe = new BlastFurnaceRecipe(
                id,
                Ingredient.of(input.copy()),
                output.copy(),
                experience != null ? experience : 0.0F,
                DEFAULT_COOKING_TIME).setVanilla(true);

            event.addRecipe(recipe);
        }
    }

    private static void importSmokerRecipes(RecipesRegisterEvent event) {
        SmokerRecipes recipes = SmokerRecipes.smelting();

        for (Map.Entry<ItemStack, ItemStack> entry : recipes.smeltingList.entrySet()) {
            ItemStack input = entry.getKey();
            ItemStack output = entry.getValue();

            if (ItemHelpers.isEmpty(input) || ItemHelpers.isEmpty(output)) continue;

            ResourceLocation id = createRecipeId("smoker", input, output);
            if (id == null) continue;

            Float experience = recipes.experienceList.get(output);

            SmokerRecipe recipe = new SmokerRecipe(
                id,
                Ingredient.of(input.copy()),
                output.copy(),
                experience != null ? experience : 0.0F,
                DEFAULT_COOKING_TIME).setVanilla(true);

            event.addRecipe(recipe);
        }
    }

    private static ResourceLocation createRecipeId(String type, ItemStack input, ItemStack output) {
        if (ItemHelpers.isEmpty(input) || ItemHelpers.isEmpty(output)) return null;

        return new ResourceLocation(
            "okcore",
            "generated/etfuturum/" + type
                + "/"
                + ItemHelpers.getHashCode(input)
                + "_to_"
                + ItemHelpers.getHashCode(output));
    }
}
