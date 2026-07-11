package ruiseki.okcore.energy.capability.wrapper;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.energy.capability.wrapper.cofh.CoFHEnergyWrapper;
import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.helper.EnderIOHelpers;

public class EnergyStorageWrapper implements ICapabilityProvider {

    private final TileEntity tile;

    @SuppressWarnings("unchecked")
    private final LazyOptional<IEnergyStorage>[] energyCaps = new LazyOptional[7];

    public EnergyStorageWrapper(TileEntity tile) {
        this.tile = tile;
        for (int i = 0; i < energyCaps.length; i++) {
            energyCaps[i] = LazyOptional.empty();
        }
    }

    private int getIndexForSide(@Nullable ForgeDirection side) {
        if (side == null || side == ForgeDirection.UNKNOWN) {
            return 6;
        }
        return side.ordinal();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        if (cap == CapabilityEnergy.ENERGY) {
            int index = getIndexForSide(side);
            LazyOptional<IEnergyStorage> cachedCap = energyCaps[index];

            if (!cachedCap.isPresent()) {

                if (Mods.EnderIO.isModLoaded()) {
                    cachedCap = EnderIOHelpers.getPowerCapability(tile, side);
                }

                if (!cachedCap.isPresent()) {
                    cachedCap = LazyOptional.of(() -> new CoFHEnergyWrapper(tile, side));
                }

                energyCaps[index] = cachedCap;
            }

            return cachedCap.cast();
        }
        return LazyOptional.empty();
    }
}
