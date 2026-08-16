package ruiseki.okcore.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.handler.FluidHandlerItemStack;

/**
 * A simple fluid container, to replace the functionality of the old FluidContainerRegistry and IFluidContainerItem.
 * This fluid container may be set so that is can only completely filled or empty. (binary)
 * It may also be set so that it gets consumed when it is drained. (consumable)
 */
public class ItemFluidContainer extends Item implements IItemCapability, IItemSharedTag, IFluidContainerItem {

    protected final int capacity;

    /**
     * @param capacity The maximum capacity of this fluid container.
     */
    public ItemFluidContainer(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidHandlerItemStack(stack, capacity);
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        return FluidHelpers.getFluidContained(container);
    }

    @Override
    public int getCapacity(ItemStack container) {
        return capacity;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        return FluidHelpers.getFluidHandler(container)
            .map(handler -> handler.fill(resource, doFill))
            .orElse(0);
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        return FluidHelpers.getFluidHandler(container)
            .map(handler -> handler.drain(maxDrain, doDrain))
            .orElse(null);
    }
}
