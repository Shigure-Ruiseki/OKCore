
package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.helper.FluidHelpers;

public class FluidContainerWrapper implements IFluidHandlerItem, ICapabilityProvider {

    private final ItemStack stack;
    private final IFluidContainerItem legacy;
    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    public FluidContainerWrapper(ItemStack stack, IFluidContainerItem legacy) {
        this.stack = stack;
        this.legacy = legacy;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return stack;
    }

    @Override
    public int getTanks() {
        return legacy.getCapacity(stack) > 0 ? 1 : 0;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0 || getTanks() == 0) {
            return FluidHelpers.EMPTY;
        }
        FluidStack fluid = legacy.getFluid(stack);
        return FluidHelpers.isEmpty(fluid) ? FluidHelpers.EMPTY : fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? Math.max(0, legacy.getCapacity(stack)) : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack fluid) {
        if (tank != 0 || FluidHelpers.isEmpty(fluid) || getTanks() == 0) {
            return false;
        }
        return legacy.fill(stack, fluid, false) > 0;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (FluidHelpers.isEmpty(resource) || getTanks() == 0) {
            return 0;
        }
        return legacy.fill(stack, resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (FluidHelpers.isEmpty(resource) || getTanks() == 0) {
            return FluidHelpers.EMPTY;
        }
        FluidStack current = legacy.getFluid(stack);
        if (FluidHelpers.isEmpty(current) || !current.isFluidEqual(resource)) {
            return FluidHelpers.EMPTY;
        }
        return legacy.drain(stack, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0 || getTanks() == 0) {
            return FluidHelpers.EMPTY;
        }
        FluidStack result = legacy.drain(stack, maxDrain, doDrain);
        return FluidHelpers.isEmpty(result) ? FluidHelpers.EMPTY : result;
    }
}
