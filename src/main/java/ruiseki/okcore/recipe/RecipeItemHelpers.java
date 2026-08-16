package ruiseki.okcore.recipe;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public class RecipeItemHelpers {

    public final Int2IntMap contents = new Int2IntOpenHashMap();

    public void accountSimpleStack(ItemStack stack) {
        if (stack != null && !stack.isItemDamaged() && !stack.isItemEnchanted() && !stack.hasDisplayName()) {
            this.accountStack(stack);
        }
    }

    public void accountStack(ItemStack stack) {
        this.accountStack(stack, 64);
    }

    public void accountStack(ItemStack stack, int maxCount) {
        if (stack != null && stack.stackSize > 0) {
            int i = getStackingIndex(stack);
            int j = Math.min(maxCount, stack.stackSize);
            this.put(i, j);
        }
    }

    /**
     * Trong 1.7.10, ghép Item ID (16-bit cao) và Metadata (16-bit thấp) thành 1 số Int duy nhất.
     */
    public static int getStackingIndex(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0;
        int id = Item.getIdFromItem(stack.getItem());
        int meta = stack.getItemDamage();
        if (meta < 0 || meta > 65535) meta = 0;
        return (id << 16) | (meta & 0xFFFF);
    }

    public static ItemStack fromStackingIndex(int index) {
        if (index == 0) return null;
        int id = index >> 16;
        int meta = index & 0xFFFF;
        Item item = Item.getItemById(id);
        return item != null ? new ItemStack(item, 1, meta) : null;
    }

    private boolean has(int index) {
        return this.contents.get(index) > 0;
    }

    private int take(int index, int count) {
        int i = this.contents.get(index);
        if (i >= count) {
            this.contents.put(index, i - count);
            return index;
        } else {
            return 0;
        }
    }

    private void put(int index, int count) {
        this.contents.put(index, this.contents.get(index) + count);
    }

    public boolean canCraft(IRecipe recipe, @Nullable IntList matchResult) {
        return this.canCraft(recipe, matchResult, 1);
    }

    public boolean canCraft(IRecipe recipe, @Nullable IntList matchResult, int craftAmount) {
        return (new RecipePicker(recipe)).tryPick(craftAmount, matchResult);
    }

    public int getBiggestCraftableStack(IRecipe recipe, @Nullable IntList matchResult) {
        return this.getBiggestCraftableStack(recipe, Integer.MAX_VALUE, matchResult);
    }

    public int getBiggestCraftableStack(IRecipe recipe, int maxAmount, @Nullable IntList matchResult) {
        return (new RecipePicker(recipe)).tryPickAll(maxAmount, matchResult);
    }

    public void clear() {
        this.contents.clear();
    }

    public static List<Ingredient> getRecipeIngredients(IRecipe recipe) {
        List<Ingredient> ingredients = new ArrayList<>();

        if (recipe instanceof ShapedRecipes) {
            for (ItemStack stack : ((ShapedRecipes) recipe).recipeItems) {
                if (stack != null) ingredients.add(Ingredient.of(stack));
            }
        } else if (recipe instanceof ShapelessRecipes) {
            for (Object obj : ((ShapelessRecipes) recipe).recipeItems) {
                if (obj instanceof ItemStack) {
                    ingredients.add(Ingredient.of(obj));
                }
            }
        } else if (recipe instanceof ShapedOreRecipe) {
            for (Object obj : ((ShapedOreRecipe) recipe).getInput()) {
                addObjectToIngredients(ingredients, obj);
            }
        } else if (recipe instanceof ShapelessOreRecipe) {
            for (Object obj : ((ShapelessOreRecipe) recipe).getInput()) {
                addObjectToIngredients(ingredients, obj);
            }
        } else if (recipe instanceof IRecipeOK<?>recipeOK) {
            for (Ingredient ing : recipeOK.getIngredients()) {
                if (ing != null) ingredients.add(ing);
            }
        }

        return ingredients;
    }

    @SuppressWarnings("unchecked")
    private static void addObjectToIngredients(List<Ingredient> ingredients, Object obj) {
        if (obj == null) return;
        if (obj instanceof ItemStack) {
            ingredients.add(Ingredient.of(obj));
        } else if (obj instanceof String) {
            ingredients.add(Ingredient.fromValues(Stream.of(new Ingredient.OreList((String) obj))));
        } else if (obj instanceof List) {
            List<ItemStack> list = (List<ItemStack>) obj;
            if (!list.isEmpty()) {
                ingredients.add(Ingredient.of(list.stream()));
            }
        } else if (obj instanceof Ingredient) {
            ingredients.add((Ingredient) obj);
        }
    }

    class RecipePicker {

        private final IRecipe recipe;
        private final List<Ingredient> ingredients = Lists.newArrayList();
        private final int ingredientCount;
        private final int[] items;
        private final int itemCount;
        private final BitSet data;
        private final IntList path = new IntArrayList();

        public RecipePicker(IRecipe recipe) {
            this.recipe = recipe;
            this.ingredients.addAll(getRecipeIngredients(recipe));

            // Lọc bỏ ingredient rỗng
            for (int i = this.ingredients.size() - 1; i >= 0; i--) {
                if (this.ingredients.get(i)
                    .isEmpty()) {
                    this.ingredients.remove(i);
                }
            }

            this.ingredientCount = this.ingredients.size();
            this.items = this.getUniqueAvailableIngredientItems();
            this.itemCount = this.items.length;
            this.data = new BitSet(
                this.ingredientCount + this.itemCount + this.ingredientCount + this.ingredientCount * this.itemCount);

            for (int i = 0; i < this.ingredients.size(); ++i) {
                IntList intlist = this.ingredients.get(i)
                    .getStackingIds();

                for (int j = 0; j < this.itemCount; ++j) {
                    if (intlist.contains(this.items[j])) {
                        this.data.set(this.getIndex(true, j, i));
                    }
                }
            }
        }

        public boolean tryPick(int craftAmount, @Nullable IntList matchResult) {
            if (craftAmount <= 0) {
                return true;
            } else {
                int i;
                for (i = 0; this.dfs(craftAmount); ++i) {
                    RecipeItemHelpers.this.take(this.items[this.path.getInt(0)], craftAmount);
                    int j = this.path.size() - 1;
                    this.setSatisfied(this.path.getInt(j));

                    for (int k = 0; k < j; ++k) {
                        this.toggleResidual((k & 1) == 0, this.path.getInt(k), this.path.getInt(k + 1));
                    }

                    this.path.clear();
                    this.data.clear(0, this.ingredientCount + this.itemCount);
                }

                boolean flag = i == this.ingredientCount;
                boolean flag1 = flag && matchResult != null;
                if (flag1) {
                    matchResult.clear();
                }

                this.data.clear(0, this.ingredientCount + this.itemCount + this.ingredientCount);
                int l = 0;

                for (int i1 = 0; i1 < this.ingredients.size(); ++i1) {
                    if (flag1 && this.ingredients.get(i1)
                        .isEmpty()) {
                        matchResult.add(0);
                    } else {
                        for (int j1 = 0; j1 < this.itemCount; ++j1) {
                            if (this.hasResidual(false, l, j1)) {
                                this.toggleResidual(true, j1, l);
                                RecipeItemHelpers.this.put(this.items[j1], craftAmount);
                                if (flag1) {
                                    matchResult.add(this.items[j1]);
                                }
                            }
                        }
                        ++l;
                    }
                }

                return flag;
            }
        }

        private int[] getUniqueAvailableIngredientItems() {
            IntCollection intcollection = new IntAVLTreeSet();

            for (Ingredient ingredient : this.ingredients) {
                intcollection.addAll(ingredient.getStackingIds());
            }

            IntIterator intiterator = intcollection.iterator();

            while (intiterator.hasNext()) {
                if (!RecipeItemHelpers.this.has(intiterator.nextInt())) {
                    intiterator.remove();
                }
            }

            return intcollection.toIntArray();
        }

        private boolean dfs(int amount) {
            int i = this.itemCount;

            for (int j = 0; j < i; ++j) {
                if (RecipeItemHelpers.this.contents.get(this.items[j]) >= amount) {
                    this.visit(false, j);

                    while (!this.path.isEmpty()) {
                        int k = this.path.size();
                        boolean flag = (k & 1) == 1;
                        int l = this.path.getInt(k - 1);
                        if (!flag && !this.isSatisfied(l)) {
                            break;
                        }

                        int i1 = flag ? this.ingredientCount : i;

                        for (int j1 = 0; j1 < i1; ++j1) {
                            if (!this.hasVisited(flag, j1) && this.hasConnection(flag, l, j1)
                                && this.hasResidual(flag, l, j1)) {
                                this.visit(flag, j1);
                                break;
                            }
                        }

                        int k1 = this.path.size();
                        if (k1 == k) {
                            this.path.removeInt(k1 - 1);
                        }
                    }

                    if (!this.path.isEmpty()) {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean isSatisfied(int index) {
            return this.data.get(this.getSatisfiedIndex(index));
        }

        private void setSatisfied(int index) {
            this.data.set(this.getSatisfiedIndex(index));
        }

        private int getSatisfiedIndex(int index) {
            return this.ingredientCount + this.itemCount + index;
        }

        private boolean hasConnection(boolean flag, int a, int b) {
            return this.data.get(this.getIndex(flag, a, b));
        }

        private boolean hasResidual(boolean flag, int a, int b) {
            return flag != this.data.get(1 + this.getIndex(flag, a, b));
        }

        private void toggleResidual(boolean flag, int a, int b) {
            this.data.flip(1 + this.getIndex(flag, a, b));
        }

        private int getIndex(boolean flag, int a, int b) {
            int i = flag ? a * this.ingredientCount + b : b * this.ingredientCount + a;
            return this.ingredientCount + this.itemCount + this.ingredientCount + 2 * i;
        }

        private void visit(boolean flag, int index) {
            this.data.set(this.getVisitedIndex(flag, index));
            this.path.add(index);
        }

        private boolean hasVisited(boolean flag, int index) {
            return this.data.get(this.getVisitedIndex(flag, index));
        }

        private int getVisitedIndex(boolean flag, int index) {
            return (flag ? 0 : this.ingredientCount) + index;
        }

        public int tryPickAll(int amount, @Nullable IntList matchResult) {
            int i = 0;
            int j = Math.min(amount, this.getMinIngredientCount()) + 1;

            while (true) {
                int k = (i + j) / 2;
                if (this.tryPick(k, null)) {
                    if (j - i <= 1) {
                        if (k > 0) {
                            this.tryPick(k, matchResult);
                        }
                        return k;
                    }
                    i = k;
                } else {
                    j = k;
                }
            }
        }

        private int getMinIngredientCount() {
            int i = Integer.MAX_VALUE;

            for (Ingredient ingredient : this.ingredients) {
                int j = 0;

                IntIterator iterator = ingredient.getStackingIds()
                    .iterator();
                while (iterator.hasNext()) {
                    int k = iterator.nextInt();
                    j += RecipeItemHelpers.this.contents.get(k);
                }

                i = Math.min(i, j);
            }

            return i == Integer.MAX_VALUE ? 0 : i;
        }
    }
}
