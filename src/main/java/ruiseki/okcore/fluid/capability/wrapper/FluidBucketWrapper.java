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
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.FluidTankProperties;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

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
        Fluid fluid = fluidStack.getFluid();
        return fluid == FluidRegistry.WATER || fluid == FluidRegistry.LAVA
            || fluid.getName()
                .equals("milk");
    }

    @Nullable
    public FluidStack getFluid() {
        Item item = container.getItem();
        if (item == Items.water_bucket) {
            return new FluidStack(FluidRegistry.WATER, FluidHelpers.BUCKET_VOLUME);
        } else if (item == Items.lava_bucket) {
            return new FluidStack(FluidRegistry.LAVA, FluidHelpers.BUCKET_VOLUME);
        } else if (item == Items.milk_bucket) {
            return FluidRegistry.getFluidStack("milk", FluidHelpers.BUCKET_VOLUME);
        }
        return null;
    }

    protected void setFluid(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) {
            this.container = new ItemStack(Items.bucket);
        } else {
            ItemStack filledBucket = FluidHelpers.getFilledBucket(fluidStack);
            this.container = Objects.requireNonNullElseGet(filledBucket, () -> new ItemStack(Items.bucket));
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new FluidTankProperties[] { new FluidTankProperties(getFluid(), FluidHelpers.BUCKET_VOLUME) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (container.stackSize != 1 || resource == null
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
        if (container.stackSize != 1 || stack == null || stack.amount < FluidHelpers.BUCKET_VOLUME) {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null && fluidStack.isFluidEqual(stack)) {
            if (doDrain) {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (container.stackSize != 1 || maxDrain < FluidHelpers.BUCKET_VOLUME) {
            return null;
        }

        FluidStack fluidStack = getFluid();
        if (fluidStack != null) {
            if (doDrain) {
                setFluid(null);
            }
            return fluidStack;
        }

        return null;
    }
}
