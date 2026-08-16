package ruiseki.commoncapabilities.api.capability.fluidhandler;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

/**
 * An iterator over all slots in a fluid handler.
 * 
 * @author rubensworks
 */
public class FluidHandlerFluidStackIterator implements Iterator<FluidStack> {

    private final IFluidTankProperties[] fluidTankProperties;
    private int slot;

    public FluidHandlerFluidStackIterator(IFluidHandler fluidHandler, int offset) {
        this.fluidTankProperties = fluidHandler.getTankProperties();
        this.slot = offset;
    }

    public FluidHandlerFluidStackIterator(IFluidHandler fluidHandler) {
        this(fluidHandler, 0);
    }

    @Override
    public boolean hasNext() {
        return slot < fluidTankProperties.length;
    }

    @Override
    public FluidStack next() {
        if (!hasNext()) {
            throw new NoSuchElementException("Slot out of bounds");
        }
        return fluidTankProperties[slot++].getContents();
    }
}
