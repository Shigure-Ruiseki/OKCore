package ruiseki.okcore.client.mui.gui.sync;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.junit.Test;

public class FluidStackSyncValueTest {

    @Test
    public void setValueCopiesInputAndOnlyWritesSourceWhenRequested() {
        FluidStack input = new FluidStack(FluidRegistry.WATER, 4);
        AtomicInteger writes = new AtomicInteger();
        AtomicReference<FluidStack> received = new AtomicReference<>();
        FluidStackSyncValue value = new FluidStackSyncValue(() -> null, (FluidStack stack) -> {
            writes.incrementAndGet();
            received.set(stack);
        });
        value.setValue(input, false, false);
        assertEquals(0, writes.get());
        assertNotSame(input, value.getValue());
        input.amount = 7;
        assertEquals(4, value.getValue().amount);
        value.setValue(input, true, false);
        assertEquals(1, writes.get());
        assertSame(input, received.get());
        assertNotSame(input, value.getValue());
        assertEquals(7, value.getValue().amount);
        assertSame(FluidStack.class, value.getValueType());
    }

    @Test
    public void cacheDetectsAmountChangesAndEmptyTransitions() {
        AtomicReference<FluidStack> source = new AtomicReference<>(new FluidStack(FluidRegistry.WATER, 4));
        FluidStackSyncValue value = new FluidStackSyncValue(source::get);
        assertTrue(value.updateCacheFromSource(true));
        assertFalse(value.updateCacheFromSource(false));
        source.get().amount = 9;
        assertTrue(value.updateCacheFromSource(false));
        assertEquals(9, value.getValue().amount);
        assertFalse(value.updateCacheFromSource(false));
        source.set(null);
        assertTrue(value.updateCacheFromSource(false));
        assertNull(value.getValue());
        assertFalse(value.updateCacheFromSource(false));
    }

    @Test
    public void readOnlyValueAcceptsEmptyStackWithoutSetter() {
        FluidStackSyncValue value = new FluidStackSyncValue(() -> null);
        FluidStack empty = new FluidStack(FluidRegistry.WATER, 4);
        empty.amount = 0;
        value.setValue(empty, true, false);
        assertNull(value.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void rejectsMissingGetter() {
        new FluidStackSyncValue((java.util.function.Supplier<FluidStack>) null);
    }
}
