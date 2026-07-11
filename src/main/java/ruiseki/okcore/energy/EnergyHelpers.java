package ruiseki.okcore.energy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;

public class EnergyHelpers {

    public static LazyOptional<IEnergyStorage> getEnergyStorage(Object object, ForgeDirection side) {
        return object instanceof TileEntity tile ? getEnergyStorage(tile, side) : LazyOptional.empty();
    }

    public static LazyOptional<IEnergyStorage> getEnergyStorage(TileEntity tile, ForgeDirection side) {
        return CapabilityHelpers.getCapability(tile, CapabilityEnergy.ENERGY, side);
    }

    public static LazyOptional<IEnergyStorage> getEnergyStorage(World world, BlockPos pos, ForgeDirection side) {
        return getEnergyStorage(pos.getTileEntity(world), side);
    }

    public static LazyOptional<IEnergyStorage> getEnergyStorage(World world, int x, int y, int z, ForgeDirection side) {
        return getEnergyStorage(world, new BlockPos(x, y, z), side);
    }
}
