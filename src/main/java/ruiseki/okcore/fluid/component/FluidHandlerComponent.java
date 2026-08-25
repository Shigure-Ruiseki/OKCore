package ruiseki.okcore.fluid.component;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.CapabilityHelpers;

public class FluidHandlerComponent implements net.minecraftforge.fluids.IFluidHandler {

    @NotNull
    private final TileEntity tile;

    public FluidHandlerComponent(@NotNull TileEntity tile) {
        this.tile = tile;
    }

    protected LazyOptional<IFluidHandler> getCapability(@Nullable ForgeDirection side) {
        return CapabilityHelpers.getCapability(this.tile, CapabilityFluidHandler.FLUID_HANDLER, side);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }
        return getCapability(from).map(handler -> handler.fill(from, resource, doFill))
            .orElse(0);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) {
            return null;
        }
        return getCapability(from).map(handler -> handler.drain(from, resource, doDrain))
            .orElse(null);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return null;
        }
        return getCapability(from).map(handler -> handler.drain(from, maxDrain, doDrain))
            .orElse(null);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        if (fluid == null) {
            return false;
        }
        return getCapability(from).map(handler -> handler.canFill(from, fluid))
            .orElse(false);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (fluid == null) {
            return false;
        }
        return getCapability(from).map(handler -> handler.canDrain(from, fluid))
            .orElse(false);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return getCapability(from).map(handler -> handler.getTankInfo(from))
            .orElseGet(() -> new FluidTankInfo[0]);
    }
}
