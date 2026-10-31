
package ruiseki.okcore.fluid.component;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.FluidHelpers;

public class FluidContainerItemComponent implements IFluidContainerItem {

    protected LazyOptional<IFluidHandlerItem> getHandler(ItemStack container) {
        return CapabilityHelpers.getCapability(container, CapabilityFluidHandler.FLUID_HANDLER_ITEM);
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        return getHandler(container).filter(handler -> handler.getTanks() > 0)
            .map(handler -> handler.getFluidInTank(0))
            .filter(fluid -> !FluidHelpers.isEmpty(fluid))
            .orElse(FluidHelpers.EMPTY);
    }

    @Override
    public int getCapacity(ItemStack container) {
        return getHandler(container).filter(handler -> handler.getTanks() > 0)
            .map(handler -> handler.getTankCapacity(0))
            .orElse(0);
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        return getHandler(container).filter(handler -> !FluidHelpers.isEmpty(resource))
            .map(handler -> handler.fill(resource, doFill))
            .orElse(0);
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        return getHandler(container).filter(handler -> maxDrain > 0)
            .map(handler -> handler.drain(maxDrain, doDrain))
            .filter(fluid -> !FluidHelpers.isEmpty(fluid))
            .orElse(FluidHelpers.EMPTY);
    }
}
