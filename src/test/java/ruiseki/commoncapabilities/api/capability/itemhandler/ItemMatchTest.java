package ruiseki.commoncapabilities.api.capability.itemhandler;

import static org.junit.Assert.*;

import java.util.Comparator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ItemMatchTest {

    private Comparator<NBTBase> originalComparator;
    private final Item item = new Item();

    @Before
    public void installComparator() {
        originalComparator = ItemMatch.NBT_COMPARATOR;
        ItemMatch.NBT_COMPARATOR = (a, b) -> a.equals(b) ? 0 : 1;
    }

    @After
    public void restoreComparator() {
        ItemMatch.NBT_COMPARATOR = originalComparator;
    }

    @Test
    public void anyMatchesEvenEmptyAgainstNonempty() {
        assertTrue(ItemMatch.areItemStacksEqual(null, new ItemStack(item), ItemMatch.ANY));
        assertTrue(ItemMatch.areItemStacksEqual(new ItemStack(item), null, ItemMatch.ANY));
    }

    @Test
    public void allFlagCombinationsTreatNullAndZeroCountAsEmptySymmetrically() {
        ItemStack empty = new ItemStack(item, 0);
        ItemStack nonempty = new ItemStack(item);
        for (int flags = 1; flags <= ItemMatch.EXACT; flags++) {
            assertTrue("flags=" + flags, ItemMatch.areItemStacksEqual(null, empty, flags));
            assertTrue(ItemMatch.areItemStacksEqual(empty, null, flags));
            assertFalse(ItemMatch.areItemStacksEqual(empty, nonempty, flags));
            assertFalse(ItemMatch.areItemStacksEqual(nonempty, empty, flags));
        }
    }

    @Test
    public void flagsIndependentlyControlItemDamageAndCount() {
        ItemStack a = new ItemStack(item, 2, 3);
        assertFalse(ItemMatch.areItemStacksEqual(a, new ItemStack(new Item(), 2, 3), ItemMatch.ITEM));
        assertTrue(ItemMatch.areItemStacksEqual(a, new ItemStack(new Item(), 2, 3), ItemMatch.DAMAGE));
        ItemStack b = new ItemStack(item, 7, 9);
        assertTrue(ItemMatch.areItemStacksEqual(a, b, ItemMatch.ITEM));
        assertFalse(ItemMatch.areItemStacksEqual(a, b, ItemMatch.DAMAGE));
        assertFalse(ItemMatch.areItemStacksEqual(a, b, ItemMatch.STACKSIZE));
        assertTrue(ItemMatch.areItemStacksEqual(a, a.copy(), ItemMatch.EXACT));
    }

    @Test
    public void wildcardDamageMatchesInBothDirections() {
        ItemStack wildcard = new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack specific = new ItemStack(item, 1, 42);
        assertTrue(ItemMatch.areItemStacksEqual(wildcard, specific, ItemMatch.DAMAGE));
        assertTrue(ItemMatch.areItemStacksEqual(specific, wildcard, ItemMatch.DAMAGE));
    }

    @Test
    public void tagsHandleMissingEqualAndDifferentCompounds() {
        ItemStack a = new ItemStack(item);
        ItemStack b = new ItemStack(item);
        assertTrue(ItemMatch.areItemStackTagsEqual(a, b));
        a.setTagCompound(new NBTTagCompound());
        a.getTagCompound()
            .setString("key", "value");
        assertFalse(ItemMatch.areItemStackTagsEqual(a, b));
        assertFalse(ItemMatch.areItemStackTagsEqual(b, a));
        b.setTagCompound(
            (NBTTagCompound) a.getTagCompound()
                .copy());
        assertTrue(ItemMatch.areItemStacksEqual(a, b, ItemMatch.EXACT));
        b.getTagCompound()
            .setString("key", "other");
        assertFalse(ItemMatch.areItemStacksEqual(a, b, ItemMatch.NBT));
        assertTrue(ItemMatch.areItemStacksEqual(a, b, ItemMatch.ITEM));
    }

    @Test
    public void tagComparisonRecognizesEmptyStacks() {
        assertTrue(ItemMatch.areItemStackTagsEqual(null, new ItemStack(item, -1)));
        assertFalse(ItemMatch.areItemStackTagsEqual(null, new ItemStack(item)));
        assertFalse(ItemMatch.areItemStackTagsEqual(new ItemStack(item), null));
    }
}
