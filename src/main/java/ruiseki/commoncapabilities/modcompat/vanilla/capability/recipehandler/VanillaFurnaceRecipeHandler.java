package ruiseki.commoncapabilities.modcompat.vanilla.capability.recipehandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

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
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.MixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;

/**
 * Recipe handler capability for the vanilla furnace.
 *
 * @author rubensworks
 */
public class VanillaFurnaceRecipeHandler implements IRecipeHandler {

    private static final VanillaFurnaceRecipeHandler INSTANCE = new VanillaFurnaceRecipeHandler();
    private static final Set<IngredientComponent<?, ?>> COMPONENTS_INPUT = Sets
        .newHashSet(IngredientComponent.ITEMSTACK);
    private static final Set<IngredientComponent<?, ?>> COMPONENTS_OUTPUT = Sets
        .newHashSet(IngredientComponent.ITEMSTACK);

    private List<IRecipeDefinition> recipes = null;

    private VanillaFurnaceRecipeHandler() {

    }

    public static VanillaFurnaceRecipeHandler getInstance() {
        return INSTANCE;
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
        return component == IngredientComponent.ITEMSTACK && size == 1;
    }

    @Override
    public List<IRecipeDefinition> getRecipes() {
        if (recipes != null) {
            return recipes;
        }

        return recipes = Lists.newArrayList(
            Collections2.transform(
                FurnaceRecipes.smelting()
                    .getSmeltingList()
                    .entrySet(),
                new Function<Map.Entry<ItemStack, ItemStack>, IRecipeDefinition>() {

                    @Nullable
                    @Override
                    public IRecipeDefinition apply(Map.Entry<ItemStack, ItemStack> input) {
                        if (input == null || input.getKey() == null || input.getValue() == null) {
                            return null;
                        }
                        return RecipeDefinition.ofIngredient(
                            IngredientComponent.ITEMSTACK,
                            Lists.newArrayList(
                                new PrototypedIngredient<>(
                                    IngredientComponent.ITEMSTACK,
                                    input.getKey(),
                                    ItemMatch.ITEM | ItemMatch.DAMAGE | ItemMatch.NBT)),
                            MixedIngredients.ofInstance(IngredientComponent.ITEMSTACK, input.getValue()));
                    }
                }));
    }

    @Nullable
    @Override
    public IMixedIngredients simulate(IMixedIngredients input) {
        ItemStack recipeIngredient = input.getFirstNonEmpty(IngredientComponent.ITEMSTACK);
        if (input.getComponents()
            .size() != 1 && recipeIngredient != null) {
            return null;
        }
        ItemStack result = FurnaceRecipes.smelting()
            .getSmeltingResult(recipeIngredient);
        return MixedIngredients.ofInstance(IngredientComponent.ITEMSTACK, result);
    }
}
