
package ruiseki.okcore.fluid.handler;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import ruiseki.okcore.helper.FluidHelpers;

public class FluidTank implements IFluidTank, IFluidHandler {

    @Nullable
    protected FluidStack fluid;

    protected int capacity;
    protected TileEntity tile;

    protected boolean canFill = true;
    protected boolean canDrain = true;

    public FluidTank(int capacity) {
        this(null, capacity);
    }

    public FluidTank(@Nullable FluidStack fluidStack, int capacity) {
        this.fluid = fluidStack;
        this.capacity = capacity;
    }

    public FluidTank(Fluid fluid, int amount, int capacity) {
        this(new FluidStack(fluid, amount), capacity);
    }

    @Override
    @Nullable
    public FluidStack getFluid() {
        return fluid;
    }

    public void setFluid(@Nullable FluidStack fluid) {
        this.fluid = fluid;
        onContentsChanged();
    }

    @Override
    public int getFluidAmount() {
        return fluid == null ? 0 : fluid.amount;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setTileEntity(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    // ---------------------------------------------------------------------
    // New tank API
    // ---------------------------------------------------------------------

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0 || fluid == null || fluid.amount <= 0) {
            return FluidHelpers.EMPTY;
        }

        return fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? capacity : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tank == 0 && stack != null && stack.amount > 0 && canFillFluidType(stack);
    }

    // ---------------------------------------------------------------------
    // Fill
    // ---------------------------------------------------------------------

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFillFluidType(resource)) {
            return 0;
        }

        return fillInternal(resource, doFill);
    }

    /**
     * Bypasses the canFillFluidType restriction.
     * Intended for internal use by the tank owner.
     */
    public int fillInternal(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0 || capacity <= 0) {
            return 0;
        }

        if (fluid == null) {
            int fillAmount = Math.min(capacity, resource.amount);

            if (doFill) {
                fluid = new FluidStack(resource, fillAmount);

                onContentsChanged();

                if (tile != null) {
                    FluidEvent.fireEvent(
                        new FluidEvent.FluidFillingEvent(
                            fluid,
                            tile.getWorldObj(),
                            tile.xCoord,
                            tile.yCoord,
                            tile.zCoord,
                            this,
                            fillAmount));
                }
            }

            return fillAmount;
        }

        if (!fluid.isFluidEqual(resource)) {
            return 0;
        }

        int filled = Math.min(Math.max(0, capacity - fluid.amount), resource.amount);

        if (doFill && filled > 0) {
            fluid.amount += filled;

            onContentsChanged();

            if (tile != null) {
                FluidEvent.fireEvent(
                    new FluidEvent.FluidFillingEvent(
                        fluid,
                        tile.getWorldObj(),
                        tile.xCoord,
                        tile.yCoord,
                        tile.zCoord,
                        this,
                        filled));
            }
        }

        return filled;
    }

    // ---------------------------------------------------------------------
    // Drain
    // ---------------------------------------------------------------------

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (!canDrainFluidType(fluid)) {
            return FluidHelpers.EMPTY;
        }

        return drainInternal(resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!canDrainFluidType(fluid)) {
            return FluidHelpers.EMPTY;
        }

        return drainInternal(maxDrain, doDrain);
    }

    /**
     * Bypasses the canDrainFluidType restriction.
     * Intended for internal use by the tank owner.
     */
    @Nullable
    public FluidStack drainInternal(FluidStack resource, boolean doDrain) {
        if (resource == null || fluid == null || !resource.isFluidEqual(fluid)) {
            return FluidHelpers.EMPTY;
        }

        return drainInternal(resource.amount, doDrain);
    }

    /**
     * Bypasses the canDrainFluidType restriction.
     * Intended for internal use by the tank owner.
     */
    @Nullable
    public FluidStack drainInternal(int maxDrain, boolean doDrain) {
        if (fluid == null || fluid.amount <= 0 || maxDrain <= 0) {
            return FluidHelpers.EMPTY;
        }

        int drained = Math.min(maxDrain, fluid.amount);

        FluidStack result = new FluidStack(fluid, drained);

        if (doDrain) {
            fluid.amount -= drained;

            if (fluid.amount <= 0) {
                fluid = null;
            }

            onContentsChanged();

            if (tile != null) {
                FluidEvent.fireEvent(
                    new FluidEvent.FluidDrainingEvent(
                        result,
                        tile.getWorldObj(),
                        tile.xCoord,
                        tile.yCoord,
                        tile.zCoord,
                        this,
                        drained));
            }
        }

        return result;
    }

    // ---------------------------------------------------------------------
    // Fill / Drain permissions
    // ---------------------------------------------------------------------

    public boolean canFill() {
        return canFill;
    }

    public boolean canDrain() {
        return canDrain;
    }

    public void setCanFill(boolean canFill) {
        this.canFill = canFill;
    }

    public void setCanDrain(boolean canDrain) {
        this.canDrain = canDrain;
    }

    public boolean canFillFluidType(FluidStack fluid) {
        return canFill();
    }

    public boolean canDrainFluidType(@Nullable FluidStack fluid) {
        return fluid != null && canDrain();
    }

    protected void onContentsChanged() {}

    // ---------------------------------------------------------------------
    // NBT
    // ---------------------------------------------------------------------

    public FluidTank readFromNBT(NBTTagCompound nbt) {
        if (!nbt.hasKey("Empty")) {
            FluidStack loaded = FluidStack.loadFluidStackFromNBT(nbt);
            setFluid(loaded);
        } else {
            setFluid(null);
        }

        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (fluid != null) {
            fluid.writeToNBT(nbt);
        } else {
            nbt.setString("Empty", "");
        }

        return nbt;
    }
}
