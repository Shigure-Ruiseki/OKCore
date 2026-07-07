package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.IFluidHandlerItem;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;

public class FluidContainerWrapper implements IFluidHandlerItem, ICapabilityProvider {

    private final ItemStack stack;
    private final IFluidContainerItem legacy;
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    public FluidContainerWrapper(ItemStack stack, IFluidContainerItem legacy) {
        this.stack = stack;
        this.legacy = legacy;
    }

    // ===== Capability bridge =====

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
            return holder.cast();
        }
        return LazyOptional.empty();
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
