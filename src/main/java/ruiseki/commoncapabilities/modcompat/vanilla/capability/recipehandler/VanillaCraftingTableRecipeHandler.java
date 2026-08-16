package ruiseki.commoncapabilities.modcompat.vanilla.capability.recipehandler;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.api.capability.recipehandler.RecipeDefinition;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.MixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;

public class VanillaCraftingTableRecipeHandler implements IRecipeHandler {

    private static final Set<IngredientComponent<?, ?>> COMPONENTS_INPUT = Sets
        .newHashSet(IngredientComponent.ITEMSTACK);
    private static final Set<IngredientComponent<?, ?>> COMPONENTS_OUTPUT = Sets
        .newHashSet(IngredientComponent.ITEMSTACK);

    public static final Container DUMMY_CONTAINTER = new Container() {

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    };

    private final World world;

    public VanillaCraftingTableRecipeHandler(World world) {
        this.world = world;
    }

    @Override
    public Set<IngredientComponent<?, ?>> getRecipeInputComponents() {
        return COMPONENTS_INPUT;
    }

    @Override
    public Set<IngredientComponent<?, ?>> getRecipeOutputComponents() {
        return COMPONENTS_OUTPUT;
    }

    @Override
    public boolean isValidSizeInput(IngredientComponent component, int size) {
        return component == IngredientComponent.ITEMSTACK && size > 0;
    }

    @SuppressWarnings("unchecked")
    public static List<IPrototypedIngredient<ItemStack, Integer>> getPrototypesFromObject(@Nullable Object ingredient) {
        List<IPrototypedIngredient<ItemStack, Integer>> prototypes = Lists.newArrayList();

        if (ingredient instanceof ItemStack) {
            ItemStack stack = (ItemStack) ingredient;
            if (stack != null) {
                int matchFlags = stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? ItemMatch.ITEM
                    : ItemMatch.ITEM | ItemMatch.DAMAGE;
                prototypes.add(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, stack, matchFlags));
            }
        } else if (ingredient instanceof List) {
            List<ItemStack> list = (List<ItemStack>) ingredient;
            for (ItemStack stack : list) {
                if (stack != null) {
                    int matchFlags = stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? ItemMatch.ITEM
                        : ItemMatch.ITEM | ItemMatch.DAMAGE;
                    prototypes.add(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, stack, matchFlags));
                }
            }
        } else if (ingredient instanceof Ingredient okIngredient) {
            ItemStack[] matchingStacks = okIngredient.getItems();
            if (matchingStacks != null) {
                for (ItemStack stack : matchingStacks) {
                    if (stack != null) {
                        int matchFlags = stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? ItemMatch.ITEM
                            : ItemMatch.ITEM | ItemMatch.DAMAGE;
                        prototypes.add(new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, stack, matchFlags));
                    }
                }
            }
        }

        if (prototypes.isEmpty()) {
            prototypes.add(
                new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, null, ItemMatch.ITEM | ItemMatch.DAMAGE));
        }

        return prototypes;
    }

    @Nullable
    public static IRecipeDefinition recipeToRecipeDefinition(IRecipe recipe) {
        ItemStack output = recipe.getRecipeOutput();
        if (output == null) {
            return null;
        }

        List<Object> rawInputs = null;

        if (recipe instanceof ShapedRecipes shaped) {
            rawInputs = Lists.newArrayList(shaped.recipeItems);
        } else if (recipe instanceof ShapelessRecipes shapeless) {
            rawInputs = Lists.newArrayList(shapeless.recipeItems);
        } else if (recipe instanceof ShapedOreRecipe shapedOre) {
            rawInputs = Lists.newArrayList(shapedOre.getInput());
        } else if (recipe instanceof ShapelessOreRecipe shapelessOre) {
            rawInputs = shapelessOre.getInput();
        } else if (recipe instanceof ShapedRecipe customShaped) {
            rawInputs = Lists.newArrayList(customShaped.getIngredients());
        } else if (recipe instanceof ShapelessRecipe customShapeless) {
            rawInputs = Lists.newArrayList(customShapeless.getIngredients());
        }

        if (rawInputs == null || rawInputs.isEmpty()) {
            return null;
        }

        List<List<IPrototypedIngredient<ItemStack, Integer>>> inputIngredients = Lists
            .newArrayListWithCapacity(rawInputs.size());

        for (Object rawInput : rawInputs) {
            inputIngredients.add(getPrototypesFromObject(rawInput));
        }

        return RecipeDefinition.ofIngredients(
            IngredientComponent.ITEMSTACK,
            inputIngredients,
            MixedIngredients.ofInstance(IngredientComponent.ITEMSTACK, output));
    }

    @Override
    public Collection<IRecipeDefinition> getRecipes() {
        @SuppressWarnings("unchecked")
        List<IRecipe> recipes = CraftingManager.getInstance()
            .getRecipeList();

        Collection<IRecipeDefinition> definitions = Collections2
            .transform(recipes, new Function<IRecipe, IRecipeDefinition>() {

                @Nullable
                @Override
                public IRecipeDefinition apply(@Nullable IRecipe input) {
                    return input != null ? recipeToRecipeDefinition(input) : null;
                }
            });

        return Collections2.filter(definitions, Objects::nonNull);
    }

    @Override
    public IMixedIngredients simulate(IMixedIngredients input) {
        List<ItemStack> recipeIngredients = input.getInstances(IngredientComponent.ITEMSTACK);
        if (input.getComponents()
            .size() != 1 || recipeIngredients.isEmpty()) {
            return null;
        }

        InventoryCrafting inventoryCrafting = new InventoryCrafting(DUMMY_CONTAINTER, 3, 3);
        for (int i = 0; i < Math.min(recipeIngredients.size(), 9); i++) {
            inventoryCrafting.setInventorySlotContents(i, recipeIngredients.get(i));
        }

        ItemStack result = CraftingManager.getInstance()
            .findMatchingRecipe(inventoryCrafting, world);
        return result != null ? MixedIngredients.ofInstance(IngredientComponent.ITEMSTACK, result) : null;
    }
}
