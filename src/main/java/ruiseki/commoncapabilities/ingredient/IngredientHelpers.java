package ruiseki.commoncapabilities.ingredient;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;

/**
 * Helper methods for ingredients.
 *
 * @author rubensworks
 */
public final class IngredientHelpers {

    private IngredientHelpers() {
        // Prevent instantiation
    }

    /**
     * Compare the given NBT tags with each other for order.
     *
     * @param tag1 An NBT tag.
     * @param tag2 An NBT tag.
     * @return a negative integer, zero, or a positive integer
     */
    public static int compareTags(@Nullable NBTBase tag1, @Nullable NBTBase tag2) {
        if (tag1 == null) {
            return tag2 == null ? 0 : -1;
        } else if (tag2 == null) {
            return 1;
        } else {
            return NBTBaseComparator.INSTANCE.compare(tag1, tag2);
        }
    }

}
