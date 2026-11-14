
package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.ItemHelpers;

public class FluidBucketWrapper implements IFluidHandlerItem, ICapabilityProvider {

    private static final int TANK_COUNT = 1;
    private static final int TANK_INDEX = 0;
    private static final int BUCKET_VOLUME = FluidHelpers.BUCKET_VOLUME;

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @NotNull
    protected ItemStack container;

    public FluidBucketWrapper(@NotNull ItemStack container) {
        this.container = container;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    public boolean canFillFluidType(@Nullable FluidStack fluidStack) {
        if (FluidHelpers.isEmpty(fluidStack)) {
            return false;
        }
        Fluid fluid = fluidStack.getFluid();
        if (fluid == FluidRegistry.WATER || fluid == FluidRegistry.LAVA) {
            return true;
        }
        return !ItemHelpers.isEmpty(FluidHelpers.getFilledBucket(fluidStack));
    }

    public @Nullable FluidStack getFluid() {
        if (ItemHelpers.isEmpty(container)) {
            return FluidHelpers.EMPTY;
        }
        Item item = container.getItem();
        if (item == Items.water_bucket) {
            return new FluidStack(FluidRegistry.WATER, BUCKET_VOLUME);
        }
        if (item == Items.lava_bucket) {
            return new FluidStack(FluidRegistry.LAVA, BUCKET_VOLUME);
        }
        if (item instanceof ItemBucketMilk) {
            return FluidRegistry.getFluidStack("milk", BUCKET_VOLUME);
        }
        FluidStack fluidStack = FluidHelpers.getFluidFromContainer(container);
        return FluidHelpers.isEmpty(fluidStack) ? FluidHelpers.EMPTY : FluidHelpers.copy(fluidStack);
    }

    protected void setFluid(@Nullable FluidStack fluidStack) {
        if (FluidHelpers.isEmpty(fluidStack)) {
            container = new ItemStack(Items.bucket);
            return;
        }
        Fluid fluid = fluidStack.getFluid();
        if (fluid == FluidRegistry.WATER) {
            container = new ItemStack(Items.water_bucket);
            return;
        }
        if (fluid == FluidRegistry.LAVA) {
            container = new ItemStack(Items.lava_bucket);
            return;
        }
        if ("milk".equals(fluid.getName())) {
            container = new ItemStack(Items.milk_bucket);
            return;
        }
        ItemStack filledBucket = FluidHelpers.getFilledBucket(fluidStack);
        if (ItemHelpers.isEmpty(filledBucket)) {
            return;
        }
        container = filledBucket.copy();
        container.stackSize = 1;
    }

    @Override
    public int getTanks() {
        return TANK_COUNT;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return tank == TANK_INDEX ? getFluid() : FluidHelpers.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == TANK_INDEX ? BUCKET_VOLUME : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tank == TANK_INDEX && !ItemHelpers.isEmpty(container)
            && container.stackSize == 1
            && !(container.getItem() instanceof ItemBucketMilk)
            && FluidHelpers.isEmpty(getFluid())
            && !FluidHelpers.isEmpty(stack)
            && canFillFluidType(stack);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (ItemHelpers.isEmpty(container) || container.stackSize != 1
            || container.getItem() instanceof ItemBucketMilk
            || !FluidHelpers.isEmpty(getFluid())
            || FluidHelpers.isEmpty(resource)
            || resource.amount < BUCKET_VOLUME
            || !canFillFluidType(resource)) {
            return 0;
        }

        if (doFill) {
            setFluid(resource);

            // Verify that the container actually accepted the fluid.
            FluidStack filledFluid = getFluid();

            if (FluidHelpers.isEmpty(filledFluid) || !filledFluid.isFluidEqual(resource)) {
                return 0;
            }
        }

        return BUCKET_VOLUME;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (ItemHelpers.isEmpty(container) || container.stackSize != 1
            || FluidHelpers.isEmpty(resource)
            || resource.amount < BUCKET_VOLUME) {
            return FluidHelpers.EMPTY;
        }
        FluidStack fluidStack = getFluid();
        if (FluidHelpers.isEmpty(fluidStack) || fluidStack.amount < BUCKET_VOLUME
            || !fluidStack.isFluidEqual(resource)) {
            return FluidHelpers.EMPTY;
        }
        if (doDrain) {
            setFluid(FluidHelpers.EMPTY);
            if (!FluidHelpers.isEmpty(getFluid())) {
                return FluidHelpers.EMPTY;
            }
        }
        return fluidStack;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (ItemHelpers.isEmpty(container) || container.stackSize != 1 || maxDrain < BUCKET_VOLUME) {
            return FluidHelpers.EMPTY;
        }
        FluidStack fluidStack = getFluid();
        if (FluidHelpers.isEmpty(fluidStack) || fluidStack.amount < BUCKET_VOLUME) {
            return FluidHelpers.EMPTY;
        }
        if (doDrain) {
            setFluid(FluidHelpers.EMPTY);
            if (!FluidHelpers.isEmpty(getFluid())) {
                return FluidHelpers.EMPTY;
            }
        }
        return fluidStack;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM) {
            return holder.cast();
        }
        return LazyOptional.empty();
    }
}
