package ruiseki.commoncapabilities.ingredient;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.ItemHelpers;

/**
 * Matcher for ItemStacks.
 *
 * @author rubensworks
 */
public class IngredientMatcherItemStack implements IIngredientMatcher<ItemStack, Integer> {

    @Override
    public boolean isInstance(Object object) {
        return object instanceof ItemStack;
    }

    @Override
    public Integer getAnyMatchCondition() {
        return ItemMatch.ANY;
    }

    @Override
    public Integer getExactMatchCondition() {
        return ItemMatch.EXACT;
    }

    @Override
    public Integer getExactMatchNoQuantityCondition() {
        return ItemMatch.ITEM | ItemMatch.DAMAGE | ItemMatch.NBT;
    }

    @Override
    public Integer withCondition(Integer matchCondition, Integer with) {
        return matchCondition | with;
    }

    @Override
    public Integer withoutCondition(Integer matchCondition, Integer without) {
        return matchCondition & ~without;
    }

    @Override
    public boolean hasCondition(Integer matchCondition, Integer searchCondition) {
        return (matchCondition & searchCondition) > 0;
    }

    @Override
    public boolean matches(ItemStack a, ItemStack b, Integer matchCondition) {
        return ItemMatch.areItemStacksEqual(a, b, matchCondition);
    }

    @Override
    public ItemStack getEmptyInstance() {
        return null;
    }

    @Override
    public boolean isEmpty(ItemStack instance) {
        return instance == null || instance.getItem() == null || instance.stackSize <= 0;
    }

    @Override
    public int hash(ItemStack instance) {
        return ItemHelpers.getItemStackHashCode(instance);
    }

    @Override
    public ItemStack copy(ItemStack instance) {
        return instance == null ? null : instance.copy();
    }

    @Override
    public long getQuantity(ItemStack instance) {
        if (instance == null || instance.getItem() == null) {
            return 0;
        }
        return instance.stackSize;
    }

    @Override
    public ItemStack withQuantity(ItemStack instance, long quantity) {
        if (instance == null || instance.getItem() == null || quantity <= 0) {
            return null;
        }
        if (instance.stackSize == quantity) {
            return instance;
        }
        ItemStack copy = instance.copy();
        copy.stackSize = Helpers.castSafe(quantity);
        return copy;
    }

    @Override
    public long getMaximumQuantity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int conditionCompare(Integer a, Integer b) {
        return Integer.compare(a, b);
    }

    @Override
    public String localize(ItemStack instance) {
        if (instance == null || instance.getItem() == null) {
            return "";
        }
        return instance.getDisplayName();
    }

    @Override
    public int compare(ItemStack o1, ItemStack o2) {
        boolean empty1 = isEmpty(o1);
        boolean empty2 = isEmpty(o2);

        if (empty1) {
            return empty2 ? 0 : -1;
        } else if (empty2) {
            return 1;
        } else if (o1.getItem() == o2.getItem()) {
            int m1 = o1.getItemDamage();
            int m2 = o2.getItemDamage();
            if (m1 == m2) {
                int c1 = o1.stackSize;
                int c2 = o2.stackSize;
                if (c1 == c2) {
                    return IngredientHelpers.compareTags(o1.getTagCompound(), o2.getTagCompound());
                }
                return c1 - c2;
            }
            return m1 - m2;
        }
        return Item.getIdFromItem(o1.getItem()) - Item.getIdFromItem(o2.getItem());
    }
}
