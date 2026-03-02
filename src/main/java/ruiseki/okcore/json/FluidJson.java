package ruiseki.okcore.json;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.gson.JsonObject;

public class FluidJson implements IJsonMaterial {

    public String name;
    public int amount = 1000;

    @Override
    public void read(JsonObject json) {
        if (json == null) return;

        this.name = json.has("name") ? json.get("name")
            .getAsString() : null;
        this.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1000;
    }

    @Override
    public void write(JsonObject json) {
        if (json == null) return;

        if (name != null) {
            json.addProperty("name", name);
        }

        if (amount != 1000) {
            json.addProperty("amount", amount);
        }
    }

    @Override
    public boolean validate() {
        return name != null && !name.isEmpty() && FluidRegistry.isFluidRegistered(name);
    }

    public static FluidStack resolveFluidStack(FluidJson data) {
        if (data == null || data.name == null) return null;

        try {
            Fluid fluid = FluidRegistry.getFluid(data.name);
            if (fluid == null) return null;

            return new FluidStack(fluid, data.amount > 0 ? data.amount : 1000);

        } catch (Throwable ignored) {
            return null;
        }
    }

    public static FluidJson parseFluidStack(FluidStack stack) {
        if (stack == null || stack.getFluid() == null) return null;

        FluidJson json = new FluidJson();
        json.name = stack.getFluid()
            .getName();
        json.amount = stack.amount;

        return json;
    }

    /**
     * Parse from string like:
     * "water,1000"
     */
    public static FluidJson parseFluidString(String string) {
        if (string == null || string.trim()
            .isEmpty()) return null;

        String[] parts = string.split(",");
        if (parts.length == 0) return null;

        FluidJson json = new FluidJson();
        json.name = parts[0].trim();

        if (parts.length > 1) {
            try {
                json.amount = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException ignored) {
                json.amount = 1000;
            }
        }

        return json;
    }
}
