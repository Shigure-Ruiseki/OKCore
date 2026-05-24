package ruiseki.okcore.mixins.early.recipe;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.helper.CraftingHelpers;
import ruiseki.okcore.recipe.type.cooking.furnace.SmeltingRecipe;

@Mixin(value = FurnaceRecipes.class)
public class MixinFurnaceRecipes {

    @Inject(method = "getSmeltingList()Ljava/util/Map;", at = @At("RETURN"), cancellable = true)
    private void okcore$getDynamicSmeltingList(CallbackInfoReturnable<Map<ItemStack, ItemStack>> cir) {
        Map<ItemStack, ItemStack> vanillaMap = cir.getReturnValue();
        if (vanillaMap == null) return;

        cir.setReturnValue(new AbstractMap<ItemStack, ItemStack>() {

            private ItemStack findCustomOutput(ItemStack input) {
                if (input == null || input.getItem() == null) return null;

                Collection<SmeltingRecipe> coreRecipes = CraftingHelpers.getSmeltingRecipes();
                for (SmeltingRecipe smeltingRecipe : coreRecipes) {
                    if (smeltingRecipe.getIngredient() != null && smeltingRecipe.getIngredient()
                        .test(input)) {
                        return smeltingRecipe.getRecipeOutput();
                    }
                }
                return null;
            }

            @Override
            public ItemStack get(Object key) {
                ItemStack result = vanillaMap.get(key);
                if (result != null) return result;

                if (key instanceof ItemStack) {
                    return findCustomOutput((ItemStack) key);
                }
                return null;
            }

            @Override
            public boolean containsKey(Object key) {
                if (vanillaMap.containsKey(key)) return true;
                if (key instanceof ItemStack) {
                    return findCustomOutput((ItemStack) key) != null;
                }
                return false;
            }

            @Override
            public ItemStack put(ItemStack key, ItemStack value) {
                return vanillaMap.put(key, value);
            }

            @Override
            public ItemStack remove(Object key) {
                return vanillaMap.remove(key);
            }

            @Override
            public void clear() {
                vanillaMap.clear();
            }

            @Override
            public @NotNull Set<Entry<ItemStack, ItemStack>> entrySet() {
                HashMap<ItemStack, ItemStack> visualMap = new HashMap<>(vanillaMap);

                Collection<SmeltingRecipe> coreRecipes = CraftingHelpers.getSmeltingRecipes();
                for (SmeltingRecipe smeltingRecipe : coreRecipes) {
                    if (smeltingRecipe.getIngredient() == null) continue;

                    List<ItemStack> inputs = smeltingRecipe.getIngredient()
                        .toStacks();
                    ItemStack output = smeltingRecipe.getRecipeOutput();
                    if (output != null && !inputs.isEmpty()) {
                        for (ItemStack in : inputs) {
                            if (in != null && in.getItem() != null) {
                                visualMap.put(in, output);
                            }
                        }
                    }
                }
                return visualMap.entrySet();
            }

            @Override
            public int size() {
                return entrySet().size();
            }
        });
    }

    @Inject(method = "func_151398_b(Lnet/minecraft/item/ItemStack;)F", at = @At("HEAD"), cancellable = true)
    private void okcore$getDynamicExperience(ItemStack output, CallbackInfoReturnable<Float> cir) {
        if (output == null || output.getItem() == null) {
            return;
        }

        Collection<SmeltingRecipe> coreRecipes = CraftingHelpers.getSmeltingRecipes();
        for (SmeltingRecipe smeltingRecipe : coreRecipes) {
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
