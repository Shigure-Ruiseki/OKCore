package ruiseki.okcore.json.fluid;

import java.util.Objects;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

public class FluidMaterial extends AbstractJsonMaterial {

    private Fluid fluid;
    private String name;
    private int amount = 1000;

    @Override
    public void read(JsonObject json) {
        this.name = getString(json, "fluid", null);
        this.fluid = null;
        this.amount = getInt(json, "amount", 1000);
        captureUnknownProperties(json, "fluid", "amount");
    }

    @Override
    public void write(JsonObject json) {
        Fluid currentFluid = getFluid();
        if (currentFluid != null) {
            json.addProperty("fluid", FluidRegistry.getFluidName(currentFluid));
        } else if (this.name != null) {
            json.addProperty("fluid", this.name);
        }
        json.addProperty("amount", this.amount);
        writeUnknownProperties(json);
    }

    @Override
    public boolean validate() {
        if (getFluid() == null) {
            logValidationError("FluidMaterial: fluid '" + name + "' can not be found in FluidRegistry!");
            return false;
        }
        return true;
    }

    public Fluid getFluid() {
        if (this.fluid == null && this.name != null) {
            this.fluid = FluidRegistry.getFluid(this.name);
        }
        return fluid;
    }

    public int getAmount() {
        return amount;
    }

    public FluidStack toStack() {
        Fluid f = getFluid();
        if (f == null) return null;
        return new FluidStack(f, this.amount);
    }

    public void fromStack(FluidStack stack) {
        if (stack == null || stack.getFluid() == null) {
            this.fluid = null;
            this.name = null;
            this.amount = 0;
            return;
        }
        this.fluid = stack.getFluid();
        this.name = FluidRegistry.getFluidName(fluid);
        this.amount = stack.amount;
        this.unknownProperties.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FluidMaterial that = (FluidMaterial) o;

        if (amount != that.amount) return false;
        return Objects.equals(getFluid(), that.getFluid());
    }

    @Override
    public int hashCode() {
        int result = getFluid() != null ? getFluid().hashCode() : 0;
        result = 31 * result + amount;
        return result;
    }

    @Override
    public String toString() {
        return "FluidMaterial[Fluid=" + (getFluid() != null ? FluidRegistry.getFluidName(fluid) : name)
            + " x"
            + amount
            + "mB]";
    }
}
