package ruiseki.okcore.mixins.early.recipe;

import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;
import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType.SHAPELESS;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.helper.CraftingHelpers;

@Mixin(CraftingManager.class)
public class MixinCraftingManager {

    @Inject(method = "findMatchingRecipe", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings("unchecked")
    private void okcore$onFindMatchingRecipe(InventoryCrafting inv, World world,
        CallbackInfoReturnable<ItemStack> cir) {
        if (cir.getReturnValue() != null) return;

        CraftingHelpers.getRecipeFor(SHAPED, inv, world)
            .ifPresent(recipe -> cir.setReturnValue(recipe.getCraftingResultOK(inv)));

        if (cir.getReturnValue() != null) return;

        CraftingHelpers.getRecipeFor(SHAPELESS, inv, world)
            .ifPresent(recipe -> cir.setReturnValue(recipe.getCraftingResultOK(inv)));
    }

    @Inject(method = "getRecipeList", at = @At("RETURN"), cancellable = true)
    private void okcore$onGetRecipeList(CallbackInfoReturnable<List<IRecipe>> cir) {
        List<IRecipe> vanillaList = cir.getReturnValue();
        if (vanillaList == null) return;

        cir.setReturnValue(new AbstractList<IRecipe>() {

            private List<IRecipe> getCustomRecipes() {
                var shaped = CraftingHelpers.getShapedRecipes();
                var shapeless = CraftingHelpers.getShapelessRecipes();

                if ((shaped == null || shaped.isEmpty()) && (shapeless == null || shapeless.isEmpty())) {
                    return Collections.emptyList();
                }

                return Stream
                    .concat(
                        shaped != null ? shaped.stream() : Stream.empty(),
                        shapeless != null ? shapeless.stream() : Stream.empty())
                    .filter(Objects::nonNull)
                    .filter(
                        recipe -> recipe.getRecipeOutput() != null && recipe.getRecipeOutput()
                            .getItem() != null)
                    .collect(Collectors.toList());
            }

            @Override
            public IRecipe get(int index) {
                int vanillaSize = vanillaList.size();
                if (index < vanillaSize) {
                    return vanillaList.get(index);
                }
                List<IRecipe> custom = getCustomRecipes();
                int customIndex = index - vanillaSize;
                if (customIndex < custom.size()) {
                    return custom.get(customIndex);
                }
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
            }

            @Override
            public int size() {
                return vanillaList.size() + getCustomRecipes().size();
            }

            @Override
            public boolean add(IRecipe recipe) {
                return vanillaList.add(recipe);
            }

            @Override
            public IRecipe remove(int index) {
                int vanillaSize = vanillaList.size();
                if (index < vanillaSize) {
                    return vanillaList.remove(index);
                }
                return null;
            }
        });
    }
}
