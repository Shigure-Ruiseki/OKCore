package ruiseki.okcore.helper;

import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.event.recipes.RecipesRegisterEvent;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.cooking.furnace.FurnaceRecipe;

public class SmeltingHelpers {

    private static final int DEFAULT_COOKING_TIME = 200;

    private SmeltingHelpers() {}

    public static void load() {
        MinecraftForge.EVENT_BUS.register(new SmeltingHelpers());
    }

    @SubscribeEvent
    public void onRecipesRegister(RecipesRegisterEvent event) {
        importVanillaFurnaceRecipes(event);
    }

    private void importVanillaFurnaceRecipes(RecipesRegisterEvent event) {
        FurnaceRecipes furnace = FurnaceRecipes.smelting();

        for (Map.Entry<ItemStack, ItemStack> entry : furnace.getSmeltingList()
            .entrySet()) {
            ItemStack input = entry.getKey();
            ItemStack output = entry.getValue();

            if (ItemHelpers.isEmpty(input) || ItemHelpers.isEmpty(output)) continue;
            if (RecipeRegistry.isFurnaceBridgeOutput(output)) continue;

            ResourceLocation id = createRecipeId(input, output);
            if (id == null) continue;

            Ingredient ingredient = Ingredient.of(input);
            float experience = getExperience(furnace, output);

            FurnaceRecipe recipe = new FurnaceRecipe(id, ingredient, output, experience, DEFAULT_COOKING_TIME)
                .setVanilla(true);

            event.addRecipe(recipe);
        }
    }

    private float getExperience(FurnaceRecipes furnace, ItemStack output) {
        return furnace.func_151398_b(output);
    }

    private ResourceLocation createRecipeId(ItemStack input, ItemStack output) {
        if (ItemHelpers.isEmpty(input) || ItemHelpers.isEmpty(output)) return null;

        ResourceLocation inputId = Helpers.getLocation(input);
        ResourceLocation outputId = Helpers.getLocation(output);

        if (inputId == null || outputId == null) return null;

        return new ResourceLocation(
            "okcore",
            "generated/vanilla/smelting/" + inputId.getResourceDomain()
                + "/"
                + inputId.getResourcePath()
                + "_"
                + ItemHelpers.getHashCode(input)
                + "_to_"
                + outputId.getResourceDomain()
                + "/"
                + outputId.getResourcePath()
                + "_"
                + ItemHelpers.getHashCode(output));
    }
}
