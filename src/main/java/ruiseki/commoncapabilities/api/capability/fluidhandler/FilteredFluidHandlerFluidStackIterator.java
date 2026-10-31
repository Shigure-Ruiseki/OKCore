
package ruiseki.commoncapabilities.api.capability.fluidhandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.fluid.handler.IFluidHandler;

/**
 * A filtered iterator over all tanks in a fluid handler.
 *
 * @author rubensworks
 */
public class FilteredFluidHandlerFluidStackIterator implements Iterator<FluidStack> {

    private final IFluidHandler fluidHandler;
    private final int tanks;
    private final FluidStack prototype;
    private final int matchFlags;

    private int slot = 0;
    private FluidStack next;

    public FilteredFluidHandlerFluidStackIterator(IFluidHandler fluidHandler, FluidStack prototype, int matchFlags) {
        this.fluidHandler = fluidHandler;
        this.tanks = fluidHandler.getTanks();
        this.prototype = prototype;
        this.matchFlags = matchFlags;
        this.next = findNext();
    }

    protected FluidStack findNext() {
        while (slot < tanks) {
            FluidStack fluidStack = fluidHandler.getFluidInTank(slot++);

            if (FluidMatch.areFluidStacksEqual(fluidStack, prototype, matchFlags)) {
                return fluidStack;
            }
        }

        return null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public FluidStack next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Slot out of bounds");
        }

        FluidStack result = this.next;
        this.next = findNext();

        return result;
    }
}
