package ruiseki.commoncapabilities.modcompat.vanilla.capability.temperature;

import net.minecraft.tileentity.TileEntityFurnace;

import ruiseki.commoncapabilities.api.capability.temperature.ITemperature;

/**
 * Temperature capability for the vanilla furnace tile entity.
 * 
 * @author rubensworks
 */
public class VanillaFurnaceTemperature implements ITemperature {

    private final TileEntityFurnace furnace;

    public VanillaFurnaceTemperature(TileEntityFurnace furnace) {
        this.furnace = furnace;
    }

    @Override
    public double getTemperature() {
        return ITemperature.ZERO_CELCIUS + furnace.furnaceBurnTime;
    }

    @Override
    public double getMaximumTemperature() {
        return Double.MAX_VALUE;
    }

    @Override
    public double getMinimumTemperature() {
        return ITemperature.ZERO_CELCIUS;
    }

    @Override
    public double getDefaultTemperature() {
        return ITemperature.ZERO_CELCIUS;
    }
}
