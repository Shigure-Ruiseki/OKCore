package ruiseki.okcore.client.mui.gui.sync;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.Test;

public class ItemStackSyncValueTest {

    @Test
    public void setValueCopiesInputAndOnlyWritesSourceWhenRequested() {
        ItemStack input = new ItemStack(new Item(), 4);
        AtomicInteger writes = new AtomicInteger();
        AtomicReference<ItemStack> received = new AtomicReference<>();
        ItemStackSyncValue value = new ItemStackSyncValue(() -> null, (ItemStack stack) -> {
            writes.incrementAndGet();
            received.set(stack);
        });
        value.setValue(input, false, false);
        assertEquals(0, writes.get());
        assertNotSame(input, value.getValue());
        input.stackSize = 7;
        assertEquals(4, value.getValue().stackSize);
        value.setValue(input, true, false);
        assertEquals(1, writes.get());
        assertSame(input, received.get());
        assertNotSame(input, value.getValue());
        assertEquals(7, value.getValue().stackSize);
        assertSame(ItemStack.class, value.getValueType());
    }

    @Test
    public void cacheDetectsAmountChangesAndEmptyTransitions() {
        AtomicReference<ItemStack> source = new AtomicReference<>(new ItemStack(new Item(), 4));
        ItemStackSyncValue value = new ItemStackSyncValue(source::get);
        assertTrue(value.updateCacheFromSource(true));
        assertFalse(value.updateCacheFromSource(false));
        source.get().stackSize = 9;
        assertTrue(value.updateCacheFromSource(false));
        assertEquals(9, value.getValue().stackSize);
        assertFalse(value.updateCacheFromSource(false));
        source.set(null);
        assertTrue(value.updateCacheFromSource(false));
        assertNull(value.getValue());
        assertFalse(value.updateCacheFromSource(false));
    }

    @Test
    public void readOnlyValueAcceptsEmptyStackWithoutSetter() {
        ItemStackSyncValue value = new ItemStackSyncValue(() -> null);
        ItemStack empty = new ItemStack(new Item(), 4);
        empty.stackSize = 0;
        value.setValue(empty, true, false);
        assertNull(value.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void rejectsMissingGetter() {
        new ItemStackSyncValue((java.util.function.Supplier<ItemStack>) null);
    }
}
