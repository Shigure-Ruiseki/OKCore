package ruiseki.okcore.helper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.junit.Test;

import ruiseki.okcore.item.handler.ItemStackHandler;

public class InventoryHelpersTest {

    @Test
    public void transferMovesAllItemsAcrossSlotsAndReportsIndependentCopies() {
        Item item = new Item();
        ItemStackHandler source = new ItemStackHandler(2);
        ItemStackHandler destination = new ItemStackHandler(2);
        source.setStackInSlot(0, new ItemStack(item, 64));
        source.setStackInSlot(1, new ItemStack(item, 10));
        List<Supplier<ItemStack>> notifications = new ArrayList<>();
        InventoryHelpers.transfer(source, destination, notifications::add);
        assertNull(source.getStackInSlot(0));
        assertNull(source.getStackInSlot(1));
        assertEquals(64, destination.getStackInSlot(0).stackSize);
        assertEquals(10, destination.getStackInSlot(1).stackSize);
        assertEquals(2, notifications.size());
        ItemStack reported = notifications.get(0).get();
        assertEquals(64, reported.stackSize);
        assertSame(item, reported.getItem());
        reported.stackSize = 1;
        assertEquals(64, notifications.get(0).get().stackSize);
        assertEquals(64, destination.getStackInSlot(0).stackSize);
    }

    @Test
    public void partialTransferLeavesRemainderAndReportsOnlyInsertedCount() {
        Item item = new Item();
        ItemStackHandler source = new ItemStackHandler(1);
        ItemStackHandler destination = new ItemStackHandler(1);
        source.setStackInSlot(0, new ItemStack(item, 20));
        destination.setStackInSlot(0, new ItemStack(item, 60));
        List<ItemStack> inserted = new ArrayList<>();
        InventoryHelpers.transfer(source, destination, stack -> inserted.add(stack.get()));
        assertEquals(16, source.getStackInSlot(0).stackSize);
        assertEquals(64, destination.getStackInSlot(0).stackSize);
        assertEquals(1, inserted.size());
        assertEquals(4, inserted.get(0).stackSize);
    }

    @Test
    public void incompatibleFullDestinationDoesNotExtractOrNotify() {
        ItemStackHandler source = new ItemStackHandler(1);
        ItemStackHandler destination = new ItemStackHandler(1);
        source.setStackInSlot(0, new ItemStack(new Item(), 20));
        destination.setStackInSlot(0, new ItemStack(new Item(), 64));
        InventoryHelpers.transfer(source, destination, stack -> fail("Nothing should be inserted"));
        assertEquals(20, source.getStackInSlot(0).stackSize);
        assertEquals(64, destination.getStackInSlot(0).stackSize);
    }

    @Test
    public void emptySourceDoesNotNotify() {
        InventoryHelpers.transfer(new ItemStackHandler(2), new ItemStackHandler(1), stack -> fail("Empty source"));
    }

    @Test
    public void refusedExtractionStopsTransferWithoutInsertingOrNotifying() {
        ItemStackHandler source = new ItemStackHandler(1) {
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return null;
            }
        };
        source.setStackInSlot(0, new ItemStack(new Item(), 20));
        ItemStackHandler destination = new ItemStackHandler(1);
        InventoryHelpers.transfer(source, destination, stack -> fail("Extraction refused"));
        assertEquals(20, source.getStackInSlot(0).stackSize);
        assertNull(destination.getStackInSlot(0));
    }
}
