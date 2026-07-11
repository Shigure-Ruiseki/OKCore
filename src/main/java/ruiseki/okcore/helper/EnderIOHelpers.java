package ruiseki.okcore.helper;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerStorage;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.wrapper.enderio.EnderIOPowerWrapper;

public class EnderIOHelpers {

    public static LazyOptional<IEnergyStorage> getPowerCapability(TileEntity tile, ForgeDirection facing) {
        try {
            if (tile instanceof IPowerStorage || tile instanceof IInternalPoweredTile) {
                return LazyOptional.of(() -> new EnderIOPowerWrapper(tile, facing));
            }
        } catch (Throwable e) {
            OKCore.okLog("Failed to create EnderIOPowerWrapper: " + e.getMessage());
        }
        return LazyOptional.empty();
    }
}
