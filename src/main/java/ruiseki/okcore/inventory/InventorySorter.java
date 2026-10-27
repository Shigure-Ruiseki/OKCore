package ruiseki.okcore.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;
import ruiseki.okcore.item.handler.InventoryHandler;
import ruiseki.okcore.tag.TagKey;

public class InventorySorter {

    private InventorySorter() {}

    public static final Comparator<Map.Entry<ItemStackKey, Integer>> BY_NAME = Comparator
        .comparing(o -> getRegistryName(o.getKey()));

    public static final Comparator<Map.Entry<ItemStackKey, Integer>> BY_COUNT = (first, second) -> {
        int ret = second.getValue()
            .compareTo(first.getValue());
        return ret != 0 ? ret : getRegistryName(first.getKey()).compareTo(getRegistryName(second.getKey()));
    };

    public static final Comparator<Map.Entry<ItemStackKey, Integer>> BY_ORE_DICT = (first, second) -> {
        int[] firstOreIds = first.getKey()
            .getOreIds();
        int[] secondOreIds = second.getKey()
            .getOreIds();

        int length = Math.min(firstOreIds.length, secondOreIds.length);

        for (int i = 0; i < length; i++) {
            int ret = OreDictionary.getOreName(firstOreIds[i])
                .compareTo(OreDictionary.getOreName(secondOreIds[i]));

            if (ret != 0) {
                return ret;
            }
        }

        int ret = Integer.compare(firstOreIds.length, secondOreIds.length);
        return ret != 0 ? ret : getRegistryName(first.getKey()).compareTo(getRegistryName(second.getKey()));
    };

    public static final Comparator<Map.Entry<ItemStackKey, Integer>> BY_TAG = new Comparator<>() {

        @Override
        public int compare(Map.Entry<ItemStackKey, Integer> first, Map.Entry<ItemStackKey, Integer> second) {
            ItemStackKey firstKey = first.getKey();
            ItemStackKey secondKey = second.getKey();

            Item firstItem = ItemHelpers.getItem(firstKey.getStack());
            Item secondItem = ItemHelpers.getItem(secondKey.getStack());

            if (firstItem == secondItem) return 0;

            if (firstItem == null) return -1;
            if (secondItem == null) return 1;

            int ret = compareTags(TagHelpers.getTags(firstItem), TagHelpers.getTags(secondItem));
            return ret != 0 ? ret : getRegistryName(firstKey).compareTo(getRegistryName(secondKey));
        }

        private int compareTags(Set<TagKey<Item>> firstTags, Set<TagKey<Item>> secondTags) {
            int ret = Integer.compare(secondTags.size(), firstTags.size());
            if (ret != 0) {
                return ret;
            }

            if (firstTags.size() == 1) {
                return firstTags.iterator()
                    .next()
                    .location()
                    .toString()
                    .compareTo(
                        secondTags.iterator()
                            .next()
                            .location()
                            .toString());
            }

            ArrayList<TagKey<Item>> firstTagsSorted = new ArrayList<>(firstTags);
            ArrayList<TagKey<Item>> secondTagsSorted = new ArrayList<>(secondTags);
            firstTagsSorted.sort(
                Comparator.comparing(
                    itemTagKey -> itemTagKey.location()
                        .toString()));
            secondTagsSorted.sort(
                Comparator.comparing(
                    itemTagKey -> itemTagKey.location()
                        .toString()));

            for (int i = 0; i < firstTagsSorted.size(); i++) {
                ret = firstTagsSorted.get(i)
                    .location()
                    .toString()
                    .compareTo(
                        secondTagsSorted.get(i)
                            .location()
                            .toString());
                if (ret != 0) {
                    return ret;
                }
            }
            return 0;
        }
    };

    public static final Comparator<Map.Entry<ItemStackKey, Integer>> BY_MOD_ID = Comparator
        .comparing(
            (Map.Entry<ItemStackKey, Integer> entry) -> entry.getKey()
                .getModId())
        .thenComparing(entry -> getRegistryName(entry.getKey()));

    private static String getRegistryName(ItemStackKey itemStackKey) {
        // noinspection ConstantConditions - registryName is nonNull by the time it exists in itemstack form
        return itemStackKey.getDisplayName();
    }

    public static void sortHandler(IItemHandlerModifiable handler,
        Comparator<? super Map.Entry<ItemStackKey, Integer>> comparator, Set<Integer> noSortSlots) {
        int slots = handler.getSlots();
        Map<ItemStackKey, Integer> compactedStacks = InventoryHelpers.getCompactedStacks(handler, noSortSlots);

        List<Map.Entry<ItemStackKey, Integer>> sortedList = new ArrayList<>(compactedStacks.entrySet());
        sortedList.sort(comparator);

        Iterator<Map.Entry<ItemStackKey, Integer>> it = sortedList.iterator();

        ItemStackKey current = null;
        int count = 0;

        for (int slot = 0; slot < slots; slot++) {
            if (noSortSlots.contains(slot)) {
                continue;
            }

            if ((current == null || count <= 0) && it.hasNext()) {
                Map.Entry<ItemStackKey, Integer> entry = it.next();
                current = entry.getKey();
                count = entry.getValue();
            }

            if (current != null && count > 0) {
                count -= placeStack(handler, current, count, slot);
            } else {
                emptySlot(handler, slot);
            }
        }
    }

    private static void emptySlot(IItemHandlerModifiable handler, int slot) {
        ItemStack existing = handler.getStackInSlot(slot);
        if (!ItemHelpers.isEmpty(existing)) {
            handler.setStackInSlot(slot, ItemHelpers.EMPTY);
        }
    }

    private static int placeStack(IItemHandlerModifiable handler, ItemStackKey current, int count, int slot) {
        if (count <= 0) return 0;

        ItemStack stack = current.getStack()
            .copy();

        int slotLimit = handler instanceof InventoryHandler inventoryHandler
            ? inventoryHandler.getStackLimit(slot, stack)
            : handler.getSlotLimit(slot);

        int placed = Math.min(count, slotLimit);
        if (placed <= 0) return 0;

        ItemHelpers.setCount(stack, placed);

        ItemStack existing = handler.getStackInSlot(slot);
        if (!ItemHelpers.areItemsEqual(existing, stack) || ItemHelpers.getCount(existing) != placed) {
            handler.setStackInSlot(slot, stack);
        }

        ItemStack result = handler.getStackInSlot(slot);
        return ItemHelpers.areItemsEqual(result, stack) ? Math.min(placed, ItemHelpers.getCount(result)) : 0;
    }
}
