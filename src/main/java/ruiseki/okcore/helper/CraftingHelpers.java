package ruiseki.okcore.helper;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import cpw.mods.fml.common.Loader;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;

/**
 * Several convenience functions for crafting.
 *
 * @author rubensworks
 */
public class CraftingHelpers {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final RecipeManager.CachedCheck<InventoryCrafting, IRecipeOK<InventoryCrafting>> CRAFTING_CHECK = RecipeManager
        .createCheck((IRecipeType) RecipeRegistry.CRAFTING);

    private static final LoadingCache<Pair<CacheableInventoryCrafting, Integer>, Optional<IRecipeOK<?>>> CACHE_RECIPES = CacheBuilder
        .newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<Pair<CacheableInventoryCrafting, Integer>, Optional<IRecipeOK<?>>>() {

            @Override
            public Optional<IRecipeOK<?>> load(Pair<CacheableInventoryCrafting, Integer> key) throws Exception {
                World world = DimensionManager.getWorld(key.getRight());
                if (world == null) return Optional.empty();

                return CRAFTING_CHECK.getRecipeFor(
                    key.getLeft()
                        .getInventoryCrafting(),
                    world)
                    .map(recipe -> (IRecipeOK<?>) recipe);
            }
        });

    public static IRecipeOK<?> findCraftingRecipe(ItemStack itemStack, int index) throws IllegalArgumentException {
        int indexAttempt = index;
        for (IRecipeOK<?> recipe : RecipeManager.getManager()
            .getRecipes()) {
            if (itemStacksEqual(recipe.getRecipeOutput(), itemStack) && indexAttempt-- == 0) {
                return recipe;
            }
        }
        throw new IllegalArgumentException("Could not find crafting recipe for " + itemStack + " with index " + index);
    }

    public static IRecipeOK<?> findMatchingRecipeCached(InventoryCrafting inventoryCrafting, World world,
        boolean uniqueInventory) {
        if (world == null || world.provider == null) return null;
        return CACHE_RECIPES.getUnchecked(
            Pair.of(new CacheableInventoryCrafting(inventoryCrafting, !uniqueInventory), world.provider.dimensionId))
            .orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static Map.Entry<ItemStack, ItemStack> findFurnaceRecipe(ItemStack itemStack, int index)
        throws IllegalArgumentException {
        int indexAttempt = index;
        Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.smelting()
            .getSmeltingList();

        for (Map.Entry<ItemStack, ItemStack> recipe : smeltingList.entrySet()) {
            if (itemStacksEqual(recipe.getValue(), itemStack) && indexAttempt-- == 0) {
                return recipe;
            }
        }
        throw new IllegalArgumentException("Could not find furnace recipe for " + itemStack + " with index " + index);
    }

    public static ResourceLocation newRecipeIdentifier(ItemStack output) {
        String modId = Loader.instance()
            .activeModContainer()
            .getModId()
            .toLowerCase();
        String itemName = output.getItem()
            .getUnlocalizedName();
        if (itemName.startsWith("item.")) itemName = itemName.substring(5);
        if (itemName.startsWith("tile.")) itemName = itemName.substring(5);

        return new ResourceLocation(modId, itemName + "_" + output.getItemDamage());
    }

    @SuppressWarnings("unchecked")
    public static IRecipe registerRecipe(IRecipe recipe) {
        CraftingManager.getInstance()
            .getRecipeList()
            .add(recipe);
        return recipe;
    }

    public static boolean itemStacksEqual(ItemStack itemStack1, ItemStack itemStack2) {
        if (itemStack1 == null || itemStack2 == null) return itemStack1 == itemStack2;

        return itemStack1.getItem() == itemStack2.getItem() && ItemStack.areItemStackTagsEqual(itemStack1, itemStack2)
            && (itemStack1.getItemDamage() == itemStack2.getItemDamage()
                || itemStack1.getItemDamage() == OreDictionary.WILDCARD_VALUE
                || itemStack2.getItemDamage() == OreDictionary.WILDCARD_VALUE
                || itemStack1.getItem()
                    .isDamageable());
    }

    public static class CacheableInventoryCrafting {

        private final InventoryCrafting inventoryCrafting;

        public CacheableInventoryCrafting(InventoryCrafting inventoryCrafting, boolean copyInventory) {
            if (copyInventory) {
                this.inventoryCrafting = new InventoryCrafting(new Container() {

                    @Override
                    public boolean canInteractWith(EntityPlayer playerIn) {
                        return false;
                    }
                },
                    inventoryCrafting.getSizeInventory() == 4 ? 2 : 3,
                    inventoryCrafting.getSizeInventory() == 4 ? 2 : 3);

                for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
                    ItemStack stack = inventoryCrafting.getStackInSlot(i);
                    this.inventoryCrafting.setInventorySlotContents(i, stack != null ? stack.copy() : null);
                }
            } else {
                this.inventoryCrafting = inventoryCrafting;
            }
        }

        public InventoryCrafting getInventoryCrafting() {
            return inventoryCrafting;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CacheableInventoryCrafting)) {
                return false;
            }
            InventoryCrafting otherInv = ((CacheableInventoryCrafting) obj).getInventoryCrafting();
            if (getInventoryCrafting().getSizeInventory() != otherInv.getSizeInventory()) {
                return false;
            }
            for (int i = 0; i < getInventoryCrafting().getSizeInventory(); i++) {
                if (!ItemStack
                    .areItemStacksEqual(getInventoryCrafting().getStackInSlot(i), otherInv.getStackInSlot(i))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 11 + getInventoryCrafting().getSizeInventory();
            for (int i = 0; i < getInventoryCrafting().getSizeInventory(); i++) {
                ItemStack stack = getInventoryCrafting().getStackInSlot(i);
                hash = hash << 1;
                if (stack != null) {
                    hash |= getItemStackHashCode(stack);
                }
            }
            return hash;
        }

        private int getItemStackHashCode(ItemStack stack) {
            if (stack == null) return 0;
            int hash = stack.getItem()
                .hashCode();
            hash = 31 * hash + stack.getItemDamage();
            if (stack.hasTagCompound()) {
                hash = 31 * hash + stack.getTagCompound()
                    .hashCode();
            }
            return hash;
        }
    }
}
