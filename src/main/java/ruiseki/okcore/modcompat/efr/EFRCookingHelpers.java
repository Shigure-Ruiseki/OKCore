
package ruiseki.okcore.modcompat.efr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        smoker.setReloadingCT(true);
        try {
            importBlastFurnaceRecipes(event);
            importSmokerRecipes(event);
        } finally {
            blast.setReloadingCT(false);
            smoker.setReloadingCT(false);
        }
    }

    private static void importBlastFurnaceRecipes(RecipesRegisterEvent event) {
        BlastFurnaceRecipes recipes = BlastFurnaceRecipes.smelting();
        Set<ResourceLocation> imported = new HashSet<>();
        Set<ItemStack> explicitInputs = recipes.smeltingList.keySet();

        for (ItemStack input : FurnaceRecipes.smelting()
            .getSmeltingList()
            .keySet()) {
            for (ItemStack candidate : resolveCandidates(input, explicitInputs)) {
                importBlastFurnaceRecipe(event, recipes, candidate, imported);
            }
        }

        for (ItemStack input : explicitInputs) {
            for (ItemStack candidate : resolveCandidates(input, explicitInputs)) {
                importBlastFurnaceRecipe(event, recipes, candidate, imported);
            }
        }
    }

    private static void importBlastFurnaceRecipe(RecipesRegisterEvent event, BlastFurnaceRecipes recipes,
        ItemStack input, Set<ResourceLocation> imported) {
        if (ItemHelpers.isEmpty(input)) return;
        if (isBlacklisted(recipes, input)) return;

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
        Set<ItemStack> explicitInputs = recipes.smeltingList.keySet();

        for (ItemStack input : FurnaceRecipes.smelting()
            .getSmeltingList()
            .keySet()) {
            for (ItemStack candidate : resolveCandidates(input, explicitInputs)) {
                importSmokerRecipe(event, recipes, candidate, imported);
            }
        }

        for (ItemStack input : explicitInputs) {
            for (ItemStack candidate : resolveCandidates(input, explicitInputs)) {
                importSmokerRecipe(event, recipes, candidate, imported);
            }
        }
    }

    private static void importSmokerRecipe(RecipesRegisterEvent event, SmokerRecipes recipes, ItemStack input,
        Set<ResourceLocation> imported) {
        if (ItemHelpers.isEmpty(input)) return;
        if (isBlacklisted(recipes, input)) return;

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

    private static List<ItemStack> resolveCandidates(ItemStack input, Set<ItemStack> explicitInputs) {
        List<ItemStack> candidates = new ArrayList<>();

        if (ItemHelpers.isEmpty(input)) return candidates;

        if (input.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
            candidates.add(input.copy());
            return candidates;
        }

        // Expand registered concrete variants.
        List<ItemStack> variants = new ArrayList<>();
        ItemHelpers.getItem(input)
            .getSubItems(
                ItemHelpers.getItem(input),
                ItemHelpers.getItem(input)
                    .getCreativeTab(),
                variants);

        for (ItemStack variant : variants) {
            if (!ItemHelpers.isEmpty(variant)) {
                addCandidate(candidates, variant);
            }
        }

        // Include explicit EFR variants not exposed through creative tabs.
        for (ItemStack explicit : explicitInputs) {
            if (ItemHelpers.isEmpty(explicit)) continue;

            if (explicit.getItem() == input.getItem() && explicit.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
                addCandidate(candidates, explicit);
            }
        }

        return candidates;
    }

    private static void addCandidate(List<ItemStack> candidates, ItemStack candidate) {
        for (ItemStack existing : candidates) {
            if (ItemStack.areItemStacksEqual(existing, candidate)) {
                return;
            }
        }

        candidates.add(candidate.copy());
    }

    private static <T> boolean isBlacklisted(T recipes, ItemStack input) {
        if (recipes instanceof BlastFurnaceRecipes) {
            return ((BlastFurnaceRecipes) recipes).smeltingBlacklist.contains(input);
        }

        if (recipes instanceof SmokerRecipes) {
            return ((SmokerRecipes) recipes).smeltingBlacklist.contains(input);
        }

        return false;
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
