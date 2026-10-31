
package ruiseki.okcore.fluid.handler;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.helper.FluidHelpers;

public class FluidHandlerItemStack implements IFluidHandlerItem, ICapabilityProvider {

    public static final String FLUID_NBT_KEY = "Fluid";

    protected ItemStack container;

    protected int capacity;

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    public FluidHandlerItemStack(@NotNull ItemStack container, int capacity) {
        this.container = container;
        this.capacity = Math.max(0, capacity);
    }

    @Override
    @NotNull
    public ItemStack getContainer() {
        return container;
    }

    @Nullable
    public FluidStack getFluid() {
        if (container == null || !container.hasTagCompound()) {
            return null;
        }

        NBTTagCompound tagCompound = container.getTagCompound();
        if (tagCompound == null || !tagCompound.hasKey(FLUID_NBT_KEY)) {
            return null;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompoundTag(FLUID_NBT_KEY));
    }

    protected void setFluid(@Nullable FluidStack fluid) {
        if (container == null) {
            return;
        }

        if (fluid == null || fluid.amount <= 0) {
            setContainerToEmpty();
            return;
        }

        if (!container.hasTagCompound()) {
            container.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound fluidTag = new NBTTagCompound();
        fluid.writeToNBT(fluidTag);
        container.getTagCompound()
            .setTag(FLUID_NBT_KEY, fluidTag);
    }

    @Override
    public int getTanks() {
        return capacity > 0 && container != null ? 1 : 0;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0 || getTanks() == 0) {
            return FluidHelpers.EMPTY;
        }

        FluidStack fluid = getFluid();

        return fluid == null || fluid.amount <= 0 ? FluidHelpers.EMPTY : fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 && getTanks() > 0 ? capacity : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack fluid) {
        return tank == 0 && getTanks() > 0 && fluid.amount > 0 && canFill(null, fluid.getFluid());
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (container == null || container.stackSize != 1
            || resource == null
            || resource.amount <= 0
            || capacity <= 0
            || !canFill(null, resource.getFluid())) {
            return 0;
        }

        FluidStack contained = getFluid();

        if (contained == null || contained.amount <= 0) {
            int fillAmount = Math.min(capacity, resource.amount);

            if (doFill && fillAmount > 0) {
                FluidStack filled = resource.copy();
                filled.amount = fillAmount;
                setFluid(filled);
            }

            return fillAmount;
        }

        if (!contained.isFluidEqual(resource)) {
            return 0;
        }

        int fillAmount = Math.min(Math.max(0, capacity - contained.amount), resource.amount);

        if (doFill && fillAmount > 0) {
            contained.amount += fillAmount;
            setFluid(contained);
        }

        return fillAmount;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) {
            return FluidHelpers.EMPTY;
        }

        FluidStack contained = getFluid();

        if (contained == null || !resource.isFluidEqual(contained)) {
            return FluidHelpers.EMPTY;
        }

        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (container == null || maxDrain <= 0 || container.stackSize != 1) {
            return FluidHelpers.EMPTY;
        }

        FluidStack contained = getFluid();

        if (contained == null || contained.amount <= 0) {
            return FluidHelpers.EMPTY;
        }

        int drainAmount = Math.min(maxDrain, contained.amount);

        FluidStack drained = contained.copy();
        drained.amount = drainAmount;

        if (doDrain) {
            contained.amount -= drainAmount;

            if (contained.amount <= 0) {
                setContainerToEmpty();
            } else {
                setFluid(contained);
            }
        }

        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    /**
     * Override this method for special handling.
     * Can be used to swap out or destroy the container.
     */
    protected void setContainerToEmpty() {
        if (container == null || !container.hasTagCompound()) {
            return;
        }

        NBTTagCompound tagCompound = container.getTagCompound();

        if (tagCompound != null) {
            tagCompound.removeTag(FLUID_NBT_KEY);
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM) {
            return holder.cast();
        }

        return LazyOptional.empty();
    }

    /**
     * Destroys the container item when it's emptied.
     */
    public static class Consumable extends FluidHandlerItemStack {

        public Consumable(ItemStack container, int capacity) {
            super(container, capacity);
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            if (container != null) {
                container.stackSize--;
                if (container.stackSize <= 0) {
                    container = null;
                }
            }
        }
    }

    /**
     * Swaps the container item for a different one when it's emptied.
     */
    public static class SwapEmpty extends FluidHandlerItemStack {

        private final ItemStack emptyContainer;

        public SwapEmpty(ItemStack container, ItemStack emptyContainer, int capacity) {
            super(container, capacity);
            this.emptyContainer = emptyContainer;
        }

        @Override
        protected void setContainerToEmpty() {
            super.setContainerToEmpty();
            container = emptyContainer.copy();
        }
    }
}
