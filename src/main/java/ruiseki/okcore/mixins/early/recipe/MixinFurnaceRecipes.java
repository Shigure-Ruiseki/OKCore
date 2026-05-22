package ruiseki.okcore.mixins.early.recipe;

import static ruiseki.okcore.recipe.type.cooking.furnace.SmeltingType.SMELTING;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.type.cooking.furnace.SmeltingRecipe;

@Mixin(value = FurnaceRecipes.class)
public class MixinFurnaceRecipes {

    @Shadow
    private Map<ItemStack, ItemStack> smeltingList;

    @Shadow
    private Map<ItemStack, Float> experienceList;

    @Inject(method = "getSmeltingList()Ljava/util/Map;", at = @At("HEAD"), cancellable = true)
    private void okcore$getDynamicSmeltingList(CallbackInfoReturnable<Map<ItemStack, ItemStack>> cir) {
        IRecipeType<?> type = RecipeRegistry.getType(SMELTING);
        Collection<IRecipeOK<?>> coreRecipes = RecipeManager.getManager()
            .getRecipesByType(type);
        if (coreRecipes == null || coreRecipes.isEmpty()) {
            return;
        }

        HashMap<ItemStack, ItemStack> combinedMap = new HashMap<ItemStack, ItemStack>(this.smeltingList);

        for (IRecipeOK<?> recipe : coreRecipes) {
            if (recipe instanceof SmeltingRecipe smeltingRecipe) {
                List<ItemStack> inputs = smeltingRecipe.getIngredient()
                    .toStacks();
                ItemStack output = smeltingRecipe.getRecipeOutput();
                if (output == null || inputs.isEmpty()) {
                    continue;
                }

                for (ItemStack inputVariant : inputs) {
                    if (inputVariant != null && inputVariant.getItem() != null) {
                        combinedMap.put(inputVariant, output);
                    }
                }
            }
        }
        cir.setReturnValue(combinedMap);
    }

    @Inject(method = "func_151398_b(Lnet/minecraft/item/ItemStack;)F", at = @At("HEAD"), cancellable = true)
    private void okcore$getDynamicExperience(ItemStack output, CallbackInfoReturnable<Float> cir) {
        if (output == null || output.getItem() == null) {
            return;
        }

        IRecipeType<?> type = RecipeRegistry.getType(SMELTING);
        Collection<IRecipeOK<?>> coreRecipes = RecipeManager.getManager()
            .getRecipesByType(type);
        if (coreRecipes == null || coreRecipes.isEmpty()) {
            return;
        }

        for (IRecipeOK<?> recipe : coreRecipes) {
            if (recipe instanceof SmeltingRecipe smeltingRecipe) {
                ItemStack recipeOutput = smeltingRecipe.getRecipeOutput();
                if (recipeOutput != null && recipeOutput.getItem() == output.getItem()) {
                    if (smeltingRecipe.matchesExperience(output)) {
                        cir.setReturnValue(smeltingRecipe.getExperience());
                        return;
                    }
                }
            }
        }
    }
}
