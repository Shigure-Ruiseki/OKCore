package ruiseki.okcore.fluid.handler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.base.Strings;

public class SmartTank extends FluidTank {

    protected Fluid restriction;

    public SmartTank(FluidStack liquid, int capacity) {
        super(liquid, capacity);
        if (liquid != null) {
            restriction = liquid.getFluid();
        } else {
            restriction = null;
        }
    }

    public SmartTank(int capacity) {
        super(capacity);
    }

    public SmartTank(Fluid liquid, int capacity) {
        super(capacity);
        restriction = liquid;
    }

    public void setRestriction(Fluid restriction) {
        this.restriction = restriction;
    }

    public float getFilledRatio() {
        return (float) getFluidAmount() / getCapacity();
    }

    public boolean isFull() {
        return getFluidAmount() >= getCapacity();
    }

    public int getAvailableSpace() {
        return getCapacity() - getFluidAmount();
    }

    public void addFluidAmount(int amount) {
        setFluidAmount(getFluidAmount() + amount);
    }

    public boolean canDrainFluidType(Fluid fl) {
        if (fl == null || fluid == null) {
            return false;
        }
        return fl.getID() == fluid.getFluidID();
    }

    @Override
    public boolean canDrainFluidType(FluidStack resource) {
        if (resource == null || resource.getFluid() == null || fluid == null) {
            return false;
        }
        return fluid.isFluidEqual(resource) && canDrain();
    }

    public boolean canFill(FluidStack resource) {
        if (resource == null || resource.getFluid() == null || !canFill()) {
            return false;
        }

        if (fluid != null) {
            return fluid.isFluidEqual(resource);
        } else if (restriction != null) {
            return restriction.getID() == resource.getFluid()
                .getID();
        } else {
            return true;
        }
    }

    public boolean canFill(Fluid fl) {
        if (!canFill() || fl == null) {
            return false;
        }

        if (fluid != null) {
            return fluid.getFluid()
                .getID() == fl.getID();
        } else if (restriction != null) {
            return restriction.getID() == fl.getID();
        } else {
            return true;
        }
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFill(resource)) {
            return 0;
        }
        return super.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (!canDrainFluidType(resource)) {
            return null;
        }
        return super.drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack getFluid() {
        if (fluid != null) {
            return fluid;
        } else if (restriction != null) {
            return new FluidStack(restriction, 0);
        } else {
            return null;
        }
    }

    public void setFluidAmount(int amount) {
        if (amount > 0) {
            if (fluid != null) {
                fluid.amount = Math.min(capacity, amount);
            } else if (restriction != null) {
                setFluid(new FluidStack(restriction, Math.min(capacity, amount)));
            } else {
                throw new RuntimeException("Cannot set fluid amount of an empty tank");
            }
        } else {
            setFluid(null);
        }
        onContentsChanged();
    }

    @Override
    public void setCapacity(int capacity) {
        super.setCapacity(capacity);
        if (getFluidAmount() > capacity) {
            setFluidAmount(capacity);
        }
    }

    @Override
    public FluidTank readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.restriction = null;
        if (nbt.hasKey("FluidRestriction")) {
            String fluidName = nbt.getString("FluidRestriction");
            if (!Strings.isNullOrEmpty(fluidName)) {
                this.restriction = FluidRegistry.getFluid(fluidName);
            }
        }

        onContentsChanged();
        return this;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if (restriction != null) {
            nbt.setString("FluidRestriction", FluidRegistry.getFluidName(restriction));
        }

        return nbt;
    }
}
