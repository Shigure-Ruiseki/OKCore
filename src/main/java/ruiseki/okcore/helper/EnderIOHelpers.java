package ruiseki.okcore.helper;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import crazypants.enderio.power.IInternalPowerProvider;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IPowerStorage;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.IEnergySink;
import ruiseki.okcore.energy.capability.IEnergySource;
import ruiseki.okcore.energy.capability.enderio.EnderIOPowerProvider;
import ruiseki.okcore.energy.capability.enderio.EnderIOPowerReceiver;
import ruiseki.okcore.energy.capability.enderio.EnderIOPowerStorage;

public class EnderIOHelpers {

    public static LazyOptional<IEnergyStorage> getEnergyCap(TileEntity tile, ForgeDirection facing) {
        if (tile instanceof IPowerStorage storage) {
            return LazyOptional.of(() -> new EnderIOPowerStorage(storage, facing));
        }
        return null;
    }

    public static LazyOptional<IEnergySink> getSinkCap(TileEntity tile, ForgeDirection facing) {
        if (tile instanceof IPowerStorage storage) {
            return LazyOptional.of(() -> new EnderIOPowerStorage(storage, facing));
        }
        if (tile instanceof IInternalPowerReceiver receiver) {
            return LazyOptional.of(() -> new EnderIOPowerReceiver(receiver, facing));
        }
        return null;
    }

    public static LazyOptional<IEnergySource> getSourceCap(TileEntity tile, ForgeDirection facing) {
        if (tile instanceof IPowerStorage storage) {
            return LazyOptional.of(() -> new EnderIOPowerStorage(storage, facing));
        }
        if (tile instanceof IInternalPowerProvider provider) {
            return LazyOptional.of(() -> new EnderIOPowerProvider(provider, facing));
        }
        return null;
    }

}
