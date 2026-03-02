package ruiseki.okcore.capabilities.fluid;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.fluid.IFluidHandlerItem;

public class FluidContainerAdapter implements IFluidHandlerItem, ICapabilityProvider {

    private final ItemStack stack;
    private final IFluidContainerItem legacy;

    public FluidContainerAdapter(ItemStack stack, IFluidContainerItem legacy) {
        this.stack = stack;
        this.legacy = legacy;
    }

    // ===== Capability bridge =====

    @Override
    public boolean hasCapability(@NotNull Capability<?> cap, ForgeDirection side) {
        return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> cap, ForgeDirection side) {
        return cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? (T) this : null;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return stack;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null) return 0;
        return legacy.fill(stack, resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null) return null;

        FluidStack current = legacy.getFluid(stack);
        if (current == null || !current.isFluidEqual(resource)) {
            return null;
        }

        return legacy.drain(stack, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) return null;
        return legacy.drain(stack, maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (fluid == null) return false;
        FluidStack current = legacy.getFluid(stack);
        if (current == null) return true;
        return current.getFluid() == fluid;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        FluidStack current = legacy.getFluid(stack);
        return current != null && (fluid == null || current.getFluid() == fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { new FluidTankInfo(legacy.getFluid(stack), legacy.getCapacity(stack)) };
    }
}
