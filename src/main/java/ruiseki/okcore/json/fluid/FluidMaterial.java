package ruiseki.okcore.json.fluid;

import java.util.Objects;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

public class FluidMaterial extends AbstractJsonMaterial {

    private Fluid fluid;
    private int amount = 1000;

    @Override
    public void read(JsonObject json) {
        String fluidName = getString(json, "fluid", null);
        this.fluid = fluidName != null ? FluidRegistry.getFluid(fluidName) : null;
        this.amount = getInt(json, "amount", 1000);
        captureUnknownProperties(json, "fluid", "amount");
    }

    @Override
    public void write(JsonObject json) {
        if (this.fluid != null) json.addProperty("fluid", FluidRegistry.getFluidName(this.fluid));
        json.addProperty("amount", this.amount);
        writeUnknownProperties(json);
    }

    @Override
    public boolean validate() {
        if (this.fluid == null) {
            logValidationError("FluidMaterial: fluid can not be empty!");
            return false;
        }
        return true;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public int getAmount() {
        return amount;
    }

    public FluidStack toStack() {
        if (this.fluid == null) return null;
        return new FluidStack(this.fluid, this.amount);
    }

    public void fromStack(FluidStack stack) {
        if (stack == null || stack.getFluid() == null) {
            this.fluid = null;
            this.amount = 0;
            return;
        }
        this.fluid = stack.getFluid();
        this.amount = stack.amount;
        this.unknownProperties.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FluidMaterial that = (FluidMaterial) o;

        if (amount != that.amount) return false;
        return Objects.equals(fluid, that.fluid);
    }

    @Override
    public int hashCode() {
        int result = fluid != null ? fluid.hashCode() : 0;
        result = 31 * result + amount;
        return result;
    }

    @Override
    public String toString() {
        return "FluidMaterial[Fluid=" + (fluid != null ? FluidRegistry.getFluidName(fluid) : "null")
            + " x"
            + amount
            + "mB]";
    }
}
