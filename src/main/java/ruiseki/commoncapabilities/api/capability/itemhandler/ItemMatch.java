package ruiseki.commoncapabilities.api.capability.itemhandler;

import java.util.Comparator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.oredict.OreDictionary;

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

    public static boolean areItemStacksEqual(ItemStack a, ItemStack b, int matchFlags) {
        if (matchFlags == ANY) {
            return true;
        }

        boolean aNull = a == null || a.getItem() == null;
        boolean bNull = b == null || b.getItem() == null;

        if (aNull && bNull) return true;
        if (aNull || bNull) return false;

        boolean item = (matchFlags & ITEM) > 0;
        boolean damage = (matchFlags & DAMAGE) > 0;
        boolean nbt = (matchFlags & NBT) > 0;
        boolean stackSize = (matchFlags & STACKSIZE) > 0;

        if (item && a.getItem() != b.getItem()) {
            return false;
        }

        if (damage) {
            int damageA = a.getItemDamage();
            int damageB = b.getItemDamage();
            boolean hasWildcard = damageA == OreDictionary.WILDCARD_VALUE || damageB == OreDictionary.WILDCARD_VALUE;
            if (!hasWildcard && damageA != damageB) {
                return false;
            }
        }

        if (stackSize && a.stackSize != b.stackSize) {
            return false;
        }

        if (nbt && !areItemStackTagsEqual(a, b)) {
            return false;
        }

        return true;
    }

    public static boolean areItemStackTagsEqual(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return a == b;
        }

        if ((a.getTagCompound() == null && b.getTagCompound() != null)
            || (a.getTagCompound() != null && b.getTagCompound() == null)) {
            return false;
        } else {
            return (a.getTagCompound() == null || NBT_COMPARATOR.compare(a.getTagCompound(), b.getTagCompound()) == 0);
        }
    }

}
