package ruiseki.okcore.fluid.capability;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.helper.ItemHelpers;

public final class EmptyFluidHandlerItem implements IFluidHandlerItem, IFluidTank {

    public static final EmptyFluidHandlerItem INSTANCE = new EmptyFluidHandlerItem();

    private EmptyFluidHandlerItem() {}

    // IFluidHandlerItem

    @Override
    public ItemStack getContainer() {
        return ItemHelpers.EMPTY;
    }

    // IFluidHandler

    @Override
    public int getTanks() {
        return 0;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return null;
    }

    @Override
    public int getTankCapacity(int tank) {
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return false;
    }

    @Override
    public int fill(FluidStack resource, boolean action) {
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean action) {
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean action) {
        return null;
    }

    // IFluidTank

    @Override
    public FluidStack getFluid() {
        return null;
    }

    @Override
    public int getFluidAmount() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public FluidTankInfo getInfo() {
        return EmptyFluidHandler.EMPTY_TANK_INFO;
    }
}
