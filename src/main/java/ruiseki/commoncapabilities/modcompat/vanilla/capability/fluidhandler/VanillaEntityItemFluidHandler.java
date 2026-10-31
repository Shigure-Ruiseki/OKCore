
package ruiseki.commoncapabilities.modcompat.vanilla.capability.fluidhandler;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.commoncapabilities.modcompat.vanilla.capability.VanillaEntityItemCapabilityDelegator;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.helper.FluidHelpers;

public class VanillaEntityItemFluidHandler extends VanillaEntityItemCapabilityDelegator<IFluidHandlerItem>
    implements IFluidHandler {

    public VanillaEntityItemFluidHandler(EntityItem entity, ForgeDirection side) {
        super(entity, side);
    }

    @Override
    protected Capability<IFluidHandlerItem> getCapabilityType() {
        return CapabilityFluidHandler.FLUID_HANDLER_ITEM;
    }

    @Override
    public int getTanks() {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        return fluidHandler != null ? fluidHandler.getTanks() : 0;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        return fluidHandler != null ? fluidHandler.getFluidInTank(tank) : FluidHelpers.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        return fluidHandler != null ? fluidHandler.getTankCapacity(tank) : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        return fluidHandler != null && fluidHandler.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        if (fluidHandler == null) return 0;
        int filled = fluidHandler.fill(resource, doFill);
        if (filled > 0 && doFill) {
            updateItemStack(fluidHandler.getContainer());
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        if (fluidHandler == null) return FluidHelpers.EMPTY;
        FluidStack drained = fluidHandler.drain(resource, doDrain);
        if (!FluidHelpers.isEmpty(drained) && doDrain) {
            updateItemStack(fluidHandler.getContainer());
        }
        return drained;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        if (fluidHandler == null) return FluidHelpers.EMPTY;
        FluidStack drained = fluidHandler.drain(maxDrain, doDrain);
        if (!FluidHelpers.isEmpty(drained) && doDrain) {
            updateItemStack(fluidHandler.getContainer());
        }
        return drained;
    }
}
