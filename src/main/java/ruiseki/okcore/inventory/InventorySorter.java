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
            ItemStack firstStack = first.getKey()
                .getStack();
            Item firstItem = firstStack.getItem();
            ItemStack secondStack = second.getKey()
                .getStack();
            Item secondItem = secondStack.getItem();
            if (firstItem == secondItem) {
                return 0;
            }
            int ret = compareTags(TagHelpers.getTags(firstItem), TagHelpers.getTags(secondItem));
            return ret != 0 ? ret : getRegistryName(first.getKey()).compareTo(getRegistryName(second.getKey()));
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

        System.out.println("[InventorySorter] ===== SORT START =====");
        System.out.println(
            "[InventorySorter] Handler: " + handler.getClass()
                .getName());
        System.out.println("[InventorySorter] Slots: " + slots);
        System.out.println("[InventorySorter] No-sort slots: " + noSortSlots);

        Map<ItemStackKey, Integer> compactedStacks = InventoryHelpers.getCompactedStacks(handler, noSortSlots);

        int totalItems = 0;
        for (Map.Entry<ItemStackKey, Integer> entry : compactedStacks.entrySet()) {
            totalItems += entry.getValue();
            System.out.println(
                "[InventorySorter] INPUT: item=" + entry.getKey()
                    .getDisplayName()
                    + ", count="
                    + entry.getValue()
                    + ", stack="
                    + entry.getKey()
                        .getStack());
        }

        System.out.println("[InventorySorter] Unique stacks: " + compactedStacks.size());
        System.out.println("[InventorySorter] Total items: " + totalItems);

        List<Map.Entry<ItemStackKey, Integer>> sortedList = new ArrayList<>(compactedStacks.entrySet());
        sortedList.sort(comparator);

        Iterator<Map.Entry<ItemStackKey, Integer>> it = sortedList.iterator();

        ItemStackKey current = null;
        int count = 0;

        for (int slot = 0; slot < slots; slot++) {
            if (noSortSlots.contains(slot)) {
                System.out.println("[InventorySorter] SKIP slot=" + slot);
                continue;
            }

            if ((current == null || count <= 0) && it.hasNext()) {
                Map.Entry<ItemStackKey, Integer> entry = it.next();
                current = entry.getKey();
                count = entry.getValue();

                System.out
                    .println("[InventorySorter] NEXT STACK: item=" + current.getDisplayName() + ", count=" + count);
            }

            if (current != null && count > 0) {
                int before = count;
                int placed = placeStack(handler, current, count, slot);
                count -= placed;

                System.out.println(
                    "[InventorySorter] SLOT RESULT: slot=" + slot
                        + ", item="
                        + current.getDisplayName()
                        + ", before="
                        + before
                        + ", placed="
                        + placed
                        + ", remaining="
                        + count);
            } else {
                emptySlot(handler, slot);
            }
        }

        if (current != null && count > 0) {
            System.out.println(
                "[InventorySorter] WARNING: Remaining stack not placed: item=" + current.getDisplayName()
                    + ", remaining="
                    + count);
        }

        while (it.hasNext()) {
            Map.Entry<ItemStackKey, Integer> entry = it.next();
            System.out.println(
                "[InventorySorter] WARNING: Unprocessed stack: item=" + entry.getKey()
                    .getDisplayName() + ", count=" + entry.getValue());
        }

        System.out.println("[InventorySorter] ===== SORT END =====");
    }

    private static void emptySlot(IItemHandlerModifiable handler, int slot) {
        ItemStack existing = handler.getStackInSlot(slot);

        if (!ItemHelpers.isEmpty(existing)) {
            System.out.println(
                "[InventorySorter] CLEAR slot=" + slot
                    + ", item="
                    + existing
                    + ", count="
                    + ItemHelpers.getCount(existing));

            handler.setStackInSlot(slot, ItemHelpers.EMPTY);

            System.out
                .println("[InventorySorter] CLEAR RESULT slot=" + slot + ", after=" + handler.getStackInSlot(slot));
        }
    }

    private static int placeStack(IItemHandlerModifiable handler, ItemStackKey current, int count, int slot) {
        if (count <= 0) {
            System.out.println("[InventorySorter] INVALID COUNT slot=" + slot + ", count=" + count);
            return 0;
        }

        ItemStack copy = current.getStack()
            .copy();
        int slotLimit = handler instanceof InventoryHandler inventoryHandler
            ? inventoryHandler.getStackLimit(slot, copy)
            : handler.getSlotLimit(slot);

        int countPlaced = Math.min(count, slotLimit);

        if (countPlaced <= 0) {
            System.out.println(
                "[InventorySorter] INVALID LIMIT slot=" + slot
                    + ", slotLimit="
                    + slotLimit
                    + ", count="
                    + count
                    + ", item="
                    + current.getDisplayName());
            return 0;
        }

        ItemHelpers.setCount(copy, countPlaced);

        ItemStack existing = handler.getStackInSlot(slot);

        System.out.println(
            "[InventorySorter] PLACE ATTEMPT: slot=" + slot
                + ", item="
                + current.getDisplayName()
                + ", requested="
                + count
                + ", slotLimit="
                + slotLimit
                + ", countPlaced="
                + countPlaced
                + ", existing="
                + existing
                + ", existingCount="
                + ItemHelpers.getCount(existing)
                + ", target="
                + copy);

        if (!ItemHelpers.areItemsEqual(existing, copy) || ItemHelpers.getCount(existing) != countPlaced) {
            handler.setStackInSlot(slot, copy);
        }

        ItemStack result = handler.getStackInSlot(slot);
        int actualCount = ItemHelpers.getCount(result);

        boolean correctItem = ItemHelpers.areItemsEqual(result, copy);
        boolean correctCount = actualCount == countPlaced;

        System.out.println(
            "[InventorySorter] PLACE RESULT: slot=" + slot
                + ", result="
                + result
                + ", actualCount="
                + actualCount
                + ", expectedCount="
                + countPlaced
                + ", correctItem="
                + correctItem
                + ", correctCount="
                + correctCount);

        if (!correctItem || !correctCount) {
            System.out.println(
                "[InventorySorter] WARNING: Write mismatch at slot=" + slot
                    + ", item="
                    + current.getDisplayName()
                    + ", requested="
                    + countPlaced
                    + ", actual="
                    + actualCount);
        }

        return correctItem ? Math.min(countPlaced, actualCount) : 0;
    }
}
