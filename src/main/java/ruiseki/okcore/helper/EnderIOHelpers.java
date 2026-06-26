package ruiseki.okcore.helper;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.enderio.EnderIOPowerWrapper;

public class EnderIOHelpers {

    public static LazyOptional<IEnergyStorage> getPowerCapability(TileEntity tile, ForgeDirection facing) {
        try {
            return LazyOptional.of(() -> new EnderIOPowerWrapper(tile, facing));
        } catch (Throwable e) {
            OKCore.okLog("Failed to create EnderIOPowerWrapper: " + e.getMessage());
            return LazyOptional.empty();
        }
    }
}
