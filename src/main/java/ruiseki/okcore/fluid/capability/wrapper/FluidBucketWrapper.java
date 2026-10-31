
package ruiseki.okcore.fluid.capability.wrapper;

import java.util.Objects;

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

public class FluidBucketWrapper implements IFluidHandlerItem, ICapabilityProvider {

    @NotNull
    protected ItemStack container;

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    public FluidBucketWrapper(@NotNull ItemStack container) {
        this.container = container;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_ITEM) {
            return holder.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    public boolean canFillFluidType(FluidStack fluidStack) {
        if (FluidHelpers.isEmpty(fluidStack)) {
            return false;
        }

        Fluid fluid = fluidStack.getFluid();

        return fluid == FluidRegistry.WATER || fluid == FluidRegistry.LAVA
            || "milk".equals(fluid.getName())
            || FluidHelpers.getFilledBucket(fluidStack) != null;
    }

    @Nullable
    public FluidStack getFluid() {
        Item item = container.getItem();

        if (item == Items.water_bucket) {
            return new FluidStack(FluidRegistry.WATER, FluidHelpers.BUCKET_VOLUME);
        }

        if (item == Items.lava_bucket) {
            return new FluidStack(FluidRegistry.LAVA, FluidHelpers.BUCKET_VOLUME);
        }

        if (item == Items.milk_bucket) {
            return FluidRegistry.getFluidStack("milk", FluidHelpers.BUCKET_VOLUME);
        }

        return FluidHelpers.getFluidFromContainer(container);
    }

    protected void setFluid(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) {
            container = new ItemStack(Items.bucket);
            return;
        }

        ItemStack filledBucket = FluidHelpers.getFilledBucket(fluidStack);

        container = Objects.requireNonNullElseGet(filledBucket, () -> new ItemStack(Items.bucket));
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0) {
            return FluidHelpers.EMPTY;
        }

        FluidStack fluid = getFluid();

        return FluidHelpers.isEmpty(fluid) ? FluidHelpers.EMPTY : fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? FluidHelpers.BUCKET_VOLUME : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tank == 0 && canFillFluidType(stack)
            && getFluid() == null
            && container.stackSize == 1
            && !(container.getItem() instanceof ItemBucketMilk);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (container.stackSize != 1 || FluidHelpers.isEmpty(resource)
            || resource.amount < FluidHelpers.BUCKET_VOLUME
            || container.getItem() instanceof ItemBucketMilk
            || getFluid() != null
            || !canFillFluidType(resource)) {
            return 0;
        }
        if (doFill) {
            setFluid(resource);
        }
        return FluidHelpers.BUCKET_VOLUME;
    }

    @Override
    public FluidStack drain(FluidStack stack, boolean doDrain) {
        if (container.stackSize != 1 || FluidHelpers.isEmpty(stack) || stack.amount < FluidHelpers.BUCKET_VOLUME) {
            return FluidHelpers.EMPTY;
        }
        FluidStack fluidStack = getFluid();
        if (FluidHelpers.isEmpty(fluidStack) || !fluidStack.isFluidEqual(stack)) {
            return FluidHelpers.EMPTY;
        }
        if (doDrain) {
            setFluid(null);
        }
        return fluidStack;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (container.stackSize != 1 || maxDrain < FluidHelpers.BUCKET_VOLUME) {
            return FluidHelpers.EMPTY;
        }
        FluidStack fluidStack = getFluid();
        if (FluidHelpers.isEmpty(fluidStack)) {
            return FluidHelpers.EMPTY;
        }
        if (doDrain) {
            setFluid(null);
        }
        return fluidStack;
    }
}
