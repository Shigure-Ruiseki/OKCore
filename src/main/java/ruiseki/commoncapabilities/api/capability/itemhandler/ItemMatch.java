package ruiseki.commoncapabilities.api.capability.itemhandler;

import java.util.Comparator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.helper.ItemHelpers;

/**
 * Item matching flags to be used in {@link ISlotlessItemHandler}.
 *
 * @author rubensworks
 */
public final class ItemMatch {

    /**
     * Convenience value matching any ItemStack.
     */
    public static final int ANY = 0;
    /**
     * Match ItemStack items.
     */
    public static final int ITEM = 1;
    /**
     * Match ItemStack damage values.
     */
    public static final int DAMAGE = 2;
    /**
     * Match ItemStack NBT tags.
     */
    public static final int NBT = 4;
    /**
     * Match ItemStack stacksizes.
     */
    public static final int STACKSIZE = 8;
    /**
     * Convenience value matching ItemStacks exactly by item, damage value, NBT tag and stacksize.
     */
    public static final int EXACT = ITEM | DAMAGE | NBT | STACKSIZE;

    /**
     * A comparator for NBT tags. (This is set in GeneralConfig)
     */
    public static Comparator<NBTBase> NBT_COMPARATOR;

    private ItemMatch() {}

    public static boolean areItemStacksEqual(@Nullable ItemStack a, @Nullable ItemStack b, int matchFlags) {
        if (matchFlags == ANY) return true;

        boolean aEmpty = ItemHelpers.isEmpty(a);
        boolean bEmpty = ItemHelpers.isEmpty(b);

        if (aEmpty) return bEmpty;
        if (bEmpty) return false;

        if ((matchFlags & ITEM) != 0 && !ItemHelpers.isSameItem(a, b)) {
            return false;
        }

        if ((matchFlags & DAMAGE) != 0
            && !ItemHelpers.doMetasMatch(ItemHelpers.getStackMeta(a), ItemHelpers.getStackMeta(b))) {
            return false;
        }

        if ((matchFlags & STACKSIZE) != 0 && ItemHelpers.getCount(a) != ItemHelpers.getCount(b)) {
            return false;
        }

        if ((matchFlags & NBT) != 0 && !areItemStackTagsEqual(a, b)) {
            return false;
        }

        return true;
    }

    public static boolean areItemStackTagsEqual(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (ItemHelpers.isEmpty(a)) return ItemHelpers.isEmpty(b);
        if (ItemHelpers.isEmpty(b)) return false;
        return areTagsEqual(ItemHelpers.getTagCompound(a), ItemHelpers.getTagCompound(b));
    }

    private static boolean areTagsEqual(@Nullable NBTBase tagA, @Nullable NBTBase tagB) {
        if (tagA == tagB) return true;
        if (tagA == null || tagB == null) return false;
        return NBT_COMPARATOR.compare(tagA, tagB) == 0;
    }
}
