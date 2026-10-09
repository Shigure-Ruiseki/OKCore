package ruiseki.okcore.helper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.event.recipes.RecipesUpdatedEvent;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;

/**
 * Several convenience functions for crafting.
 */
public class CraftingHelpers {

    private static RecipeManager CLIENT_RECIPE_MANAGER;

    private CraftingHelpers() {}

    public static void load() {
        MinecraftForge.EVENT_BUS.register(new CraftingHelpers());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRecipesLoaded(RecipesUpdatedEvent event) {
        CLIENT_RECIPE_MANAGER = event.getRecipeManager();
    }

    /**
     * Get the current OKCore recipe manager.
     *
     * On the client, use the manager received from the server after recipes
     * have been synced. Before that event fires, fall back to the singleton.
     *
     * On the server, the singleton is the authoritative manager.
     */
    public static RecipeManager getRecipeManager() {
        if (MinecraftHelpers.isClientSide() && CLIENT_RECIPE_MANAGER != null) {
            return CLIENT_RECIPE_MANAGER;
        }
        return RecipeManager.getManager();
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> Collection<T> findRecipes(World world,
        IRecipeType<? extends T> recipeType) {
        return getRecipeManager().getAllRecipesFor(recipeType);
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> Optional<T> getServerRecipe(
        IRecipeType<? extends T> recipeType, ResourceLocation recipeName) {
        return Optional.ofNullable(
            getRecipeManager().byKey(recipeName)
                .map(recipe -> {
                    @SuppressWarnings("unchecked")
                    T result = (T) recipe;
                    return result;
                })
                .orElse(null));
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> Optional<T> findServerRecipe(IRecipeType<T> recipeType,
        C inventory, World world) {
        return RecipeManager.getManager()
            .getRecipeFor(recipeType, inventory, world);
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> Collection<T> findServerRecipes(
        IRecipeType<? extends T> recipeType) {
        return getRecipeManager().getAllRecipesFor(recipeType);
    }

    @SideOnly(Side.CLIENT)
    public static <C extends IInventory, T extends IRecipeOK<C>> Optional<T> getClientRecipe(
        IRecipeType<? extends T> recipeType, ResourceLocation recipeName) {

        return getServerRecipe(recipeType, recipeName);
    }

    @SideOnly(Side.CLIENT)
    public static <C extends IInventory, T extends IRecipeOK<C>> Collection<T> getClientRecipes(
        IRecipeType<? extends T> recipeType) {
        return getRecipeManager().getAllRecipesFor(recipeType);
    }

    /**
     * Find a vanilla crafting recipe by output.
     */
    public static IRecipe findCraftingRecipe(ItemStack itemStack, int index) throws IllegalArgumentException {
        int indexAttempt = index;
        List<IRecipe> recipeList = CraftingManager.getInstance()
            .getRecipeList();

        for (IRecipe recipe : recipeList) {
            if (recipe != null && recipe.getRecipeOutput() != null
                && ItemHelpers.areItemsEqual(recipe.getRecipeOutput(), itemStack)
                && indexAttempt-- == 0) {
                return recipe;
            }
        }
        throw new IllegalArgumentException("Could not find crafting recipe for " + itemStack + " with index " + index);
    }

    /**
     * Find the first vanilla crafting recipe matching the inventory.
     */
    public static IRecipe findRecipeFromCraftingManager(InventoryCrafting inventory, World world) {
        List<IRecipe> recipeList = CraftingManager.getInstance()
            .getRecipeList();
        for (IRecipe recipe : recipeList) {
            if (recipe != null && recipe.matches(inventory, world)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Find a matching OKCore recipe.
     */
    public static <C extends IInventory, T extends IRecipeOK<C>> Optional<T> findMatchingRecipe(
        IRecipeType<T> recipeType, C inventory, World world) {
        return getRecipeManager().getRecipeFor(recipeType, inventory, world);
    }

    /**
     * Find a vanilla furnace recipe by output.
     */
    public static Map.Entry<ItemStack, ItemStack> findFurnaceRecipe(ItemStack itemStack, int index)
        throws IllegalArgumentException {
        int indexAttempt = index;
        for (Map.Entry<ItemStack, ItemStack> recipe : FurnaceRecipes.smelting()
            .getSmeltingList()
            .entrySet()) {
            if (ItemHelpers.areItemsEqual(recipe.getValue(), itemStack) && indexAttempt-- == 0) {
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

        if (itemName.startsWith("item.")) {
            itemName = itemName.substring(5);
        }

        if (itemName.startsWith("tile.")) {
            itemName = itemName.substring(5);
        }

        return new ResourceLocation(modId, itemName + "_" + output.getItemDamage());
    }

    private static final LoadingCache<Triple<IRecipeType<?>, CacheableInventoryCrafting, Integer>, Optional<IRecipe>> CACHE_RECIPES = CacheBuilder
        .newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<Triple<IRecipeType<?>, CacheableInventoryCrafting, Integer>, Optional<IRecipe>>() {

            @Override
            public Optional<IRecipe> load(Triple<IRecipeType<?>, CacheableInventoryCrafting, Integer> key) {

                World world = DimensionManager.getWorld(key.getRight());

                if (world == null || !(key.getMiddle()
                    .getInventoryCrafting() instanceof InventoryCrafting crafting)) {
                    return Optional.empty();
                }

                return Optional.ofNullable(findRecipeFromCraftingManager(crafting, world));
            }
        });

    /**
     * A cache-based variant of {@link RecipeManager#getRecipeFor(IRecipeType, IInventory, World)}.
     *
     * @param recipeType        The recipe type.
     * @param inventoryCrafting The crafting inventory.
     * @param world             The world.
     * @param uniqueInventory   If inventoryCrafting is a unique instance that can be cached safely.
     *                          Otherwise a deep copy will be taken.
     * @return The optional recipe if one was found.
     * @param <C> The inventory type.
     * @param <T> The recipe type.
     */
    @SuppressWarnings("unchecked")
    public static <C extends IInventory, T extends IRecipeOK<C>> Optional<T> findRecipeCached(IRecipeType<T> recipeType,
        C inventoryCrafting, World world, boolean uniqueInventory) {
        return (Optional<T>) (Optional<?>) CACHE_RECIPES.getUnchecked(
            Triple.of(
                recipeType,
                new CacheableInventoryCrafting(inventoryCrafting, !uniqueInventory),
                world.provider.dimensionId));
    }

    public static class CacheableInventoryCrafting {

        private final IInventory inventoryCrafting;

        public CacheableInventoryCrafting(IInventory inventoryCrafting, boolean copyInventory) {

            if (copyInventory) {
                int width = inventoryCrafting.getSizeInventory();
                int height = 1;
                if (inventoryCrafting instanceof InventoryCrafting) {
                    width = inventoryCrafting.getSizeInventory() == 4 ? 2 : 3;
                    height = inventoryCrafting.getSizeInventory() == 4 ? 2 : 3;
                }
                this.inventoryCrafting = new InventoryCrafting(new Container() {

                    @Override
                    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
                        return ItemHelpers.EMPTY;
                    }

                    @Override
                    public boolean canInteractWith(EntityPlayer player) {
                        return false;
                    }
                }, width, height);
                for (int i = 0; i < inventoryCrafting.getSizeInventory(); i++) {
                    ItemStack stack = inventoryCrafting.getStackInSlot(i);
                    this.inventoryCrafting.setInventorySlotContents(i, stack != null ? stack.copy() : null);
                }
            } else {
                this.inventoryCrafting = inventoryCrafting;
            }
        }

        public IInventory getInventoryCrafting() {
            return inventoryCrafting;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CacheableInventoryCrafting)) {
                return false;
            }
            IInventory otherInv = ((CacheableInventoryCrafting) obj).getInventoryCrafting();
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
