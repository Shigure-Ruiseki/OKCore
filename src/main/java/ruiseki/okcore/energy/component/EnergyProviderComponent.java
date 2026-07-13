package ruiseki.okcore.energy.component;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;

public class EnergyProviderComponent implements IEnergyProvider {

    @NotNull
    private final TileEntity tile;

    public EnergyProviderComponent(@NotNull TileEntity tile) {
        this.tile = tile;
    }

    private LazyOptional<IEnergyStorage> getCapability(ForgeDirection side) {
        return CapabilityHelpers.getCapability(this.tile, CapabilityEnergy.ENERGY, side);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
        return getCapability(from).map(handler -> handler.extractEnergy(maxExtract, simulate))
            .orElse(0);
    }

    @Override
    public int getEnergyStored(ForgeDirection from) {
        return getCapability(from).map(IEnergyStorage::getEnergyStored)
            .orElse(0);
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return getCapability(from).map(IEnergyStorage::getMaxEnergyStored)
            .orElse(0);
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return getCapability(from).isPresent();
    }
}
