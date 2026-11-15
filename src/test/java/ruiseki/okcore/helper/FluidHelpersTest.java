package ruiseki.okcore.helper;

import static org.junit.Assert.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.junit.Test;

public class FluidHelpersTest {

    @Test
    public void splitCapsAtAvailableAmountAndCopiesTags() {
        FluidStack source = new FluidStack(FluidRegistry.WATER, 100);
        source.tag = new NBTTagCompound();
        source.tag.setString("key", "original");
        FluidStack split = FluidHelpers.split(source, 40);
        assertEquals(60, source.amount);
        assertEquals(40, split.amount);
        split.tag.setString("key", "changed");
        assertEquals("original", source.tag.getString("key"));
        assertEquals(60, FluidHelpers.split(source, 1000).amount);
        assertEquals(0, source.amount);
        assertNull(FluidHelpers.split(source, 1));
    }

    @Test
    public void zeroSplitLeavesSourceAmountUnchanged() {
        FluidStack source = new FluidStack(FluidRegistry.WATER, 100);
        assertEquals(0, FluidHelpers.split(source, 0).amount);
        assertEquals(100, source.amount);
    }

    @Test
    public void growShrinkAndSetCanRestoreZeroAmountStack() {
        FluidStack stack = new FluidStack(FluidRegistry.WATER, 0);
        FluidHelpers.grow(stack, 10);
        assertEquals(10, stack.amount);
        FluidHelpers.shrink(stack, 4);
        assertEquals(6, stack.amount);
        FluidHelpers.setAmount(stack, 20);
        assertEquals(20, stack.amount);
    }

    @Test
    public void equalitySeparatesFluidTagsAndAmount() {
        FluidStack a = new FluidStack(FluidRegistry.WATER, 100);
        FluidStack b = new FluidStack(FluidRegistry.WATER, 200);
        assertTrue(FluidHelpers.areFluidsEqual(a, b));
        assertTrue(FluidHelpers.areFluidTagsEqual(a, b));
        assertFalse(FluidHelpers.areFluidStacksEqual(a, b));
        assertFalse(FluidHelpers.areFluidsAndAmountsEqual(a, b));
        b.amount = 100;
        b.tag = new NBTTagCompound();
        b.tag.setString("key", "value");
        assertTrue(FluidHelpers.areFluidsAndAmountsEqual(a, b));
        assertFalse(FluidHelpers.areFluidTagsEqual(a, b));
        assertFalse(FluidHelpers.areFluidStacksEqual(a, b));
        assertFalse(FluidHelpers.areFluidsEqual(a, new FluidStack(FluidRegistry.LAVA, 100)));
        assertTrue(FluidHelpers.areFluidStacksEqual(a, a.copy()));
    }

    @Test
    public void emptyEqualityIsSymmetric() {
        FluidStack stack = new FluidStack(FluidRegistry.WATER, 100);
        assertTrue(FluidHelpers.areFluidStacksEqual(null, null));
        assertFalse(FluidHelpers.areFluidStacksEqual(null, stack));
        assertFalse(FluidHelpers.areFluidStacksEqual(stack, null));
        assertFalse(FluidHelpers.areFluidsEqual(stack, new FluidStack(FluidRegistry.WATER, 0)));
    }

    @Test
    public void mergeHandlesEmptyAndAccumulatesIntoFirstStack() {
        FluidStack a = new FluidStack(FluidRegistry.WATER, 100);
        FluidStack b = new FluidStack(FluidRegistry.WATER, 200);
        assertSame(b, FluidHelpers.merge(null, b));
        assertSame(a, FluidHelpers.merge(a, null));
        assertSame(a, FluidHelpers.merge(a, b));
        assertEquals(300, a.amount);
        assertEquals(200, b.amount);
    }
}
