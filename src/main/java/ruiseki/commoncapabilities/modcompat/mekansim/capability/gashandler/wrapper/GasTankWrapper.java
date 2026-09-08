package ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.wrapper;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.base.ISideConfiguration;
import ruiseki.commoncapabilities.modcompat.mekansim.api.gas.IGasHandler;

public class GasTankWrapper implements IGasHandler {

    private final TileEntity tile;
    private final GasTank tank;
    private final ForgeDirection side;

    public GasTankWrapper(TileEntity tile, GasTank tank, ForgeDirection side) {
        this.tile = tile;
        this.tank = tank;
        this.side = side;
    }

    public GasTankWrapper(TileEntity tile, GasTank tank) {
        this(tile, tank, ForgeDirection.UNKNOWN);
    }

    private boolean isSideAllowedForInput() {
        if (tile instanceof ISideConfiguration configTile && side != null && side != ForgeDirection.UNKNOWN) {
            return configTile.getConfig()
                .getSidesForData(TransmissionType.GAS, configTile.getOrientation(), 1)
                .contains(side);
        }
        return true;
    }

    private boolean isSideAllowedForOutput() {
        if (tile instanceof ISideConfiguration configTile && side != null && side != ForgeDirection.UNKNOWN) {
            return configTile.getConfig()
                .getSidesForData(TransmissionType.GAS, configTile.getOrientation(), 2)
                .contains(side);
        }
        return true;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public GasStack getChemicalInTank(int tank) {
        return this.tank != null ? this.tank.getGas() : null;
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tank != null ? this.tank.getMaxGas() : 0;
    }

    @Override
    public boolean isGasValid(int tank, GasStack stack) {
        if (!isSideAllowedForInput()) {
            return false;
        }
        return this.tank != null && stack != null && this.tank.canReceive(stack.getGas());
    }

    @Override
    public int insertGas(GasStack stack, boolean doTransfer) {
        if (!isSideAllowedForInput() || this.tank == null || stack == null || stack.amount <= 0) {
            return 0;
        }
        return this.tank.receive(stack, doTransfer);
    }

    @Override
    public GasStack extractGas(int amount, boolean doTransfer) {
        if (!isSideAllowedForOutput() || this.tank == null || amount <= 0 || this.tank.getStored() <= 0) {
            return null;
        }
        return this.tank.draw(amount, doTransfer);
    }

    @Override
    public GasStack extractGas(GasStack stack, boolean doTransfer) {
        if (!isSideAllowedForOutput() || this.tank == null
            || stack == null
            || stack.amount <= 0
            || this.tank.getStored() <= 0) {
            return null;
        }
        if (this.tank.getGasType() != stack.getGas()) {
            return null;
        }
        return this.tank.draw(stack.amount, doTransfer);
    }
}
