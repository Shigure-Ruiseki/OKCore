
package ruiseki.commoncapabilities.api.capability.fluidhandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.fluid.handler.IFluidHandler;

/**
 * An iterator over all tanks in a fluid handler.
 *
 * @author rubensworks
 */
public class FluidHandlerFluidStackIterator implements Iterator<FluidStack> {

    private final IFluidHandler fluidHandler;
    private final int tanks;
    private int slot;

    public FluidHandlerFluidStackIterator(IFluidHandler fluidHandler, int offset) {
        this.fluidHandler = fluidHandler;
        this.tanks = fluidHandler.getTanks();
        this.slot = offset;
    }

    public FluidHandlerFluidStackIterator(IFluidHandler fluidHandler) {
        this(fluidHandler, 0);
    }

    @Override
    public boolean hasNext() {
        return slot < tanks;
    }

    @Override
    public FluidStack next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Slot out of bounds");
        }

        return fluidHandler.getFluidInTank(slot++);
    }
}
