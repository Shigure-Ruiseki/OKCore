package ruiseki.commoncapabilities.modcompat.vanilla.capability.fluidhandler;

import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.commoncapabilities.modcompat.vanilla.capability.VanillaEntityItemCapabilityDelegator;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

/**
 * A fluid handler for entity items that have a fluid handler.
 * 
 * @author rubensworks
 */
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
    public IFluidTankProperties[] getTankProperties() {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        if (fluidHandler != null) {
            return fluidHandler.getTankProperties();
        }
        return new IFluidTankProperties[0];
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        if (fluidHandler != null) {
            int ret = fluidHandler.fill(resource, doFill);
            if (ret > 0 && doFill) {
                updateItemStack(fluidHandler.getContainer());
            }
            return ret;
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        if (fluidHandler != null) {
            FluidStack ret = fluidHandler.drain(resource, doDrain);
            if (ret != null && doDrain) {
                updateItemStack(fluidHandler.getContainer());
            }
            return ret;
        }
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        IFluidHandlerItem fluidHandler = getCapability().getOrNull();
        if (fluidHandler != null) {
            FluidStack ret = fluidHandler.drain(maxDrain, doDrain);
            if (ret != null && doDrain) {
                updateItemStack(fluidHandler.getContainer());
            }
            return ret;
        }
        return null;
    }
}
