package ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.wrapper;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.base.ISideConfiguration;
import ruiseki.commoncapabilities.modcompat.mekansim.api.gas.IGasHandler;

public class GasHandlerWrapper implements IGasHandler {

    private final TileEntity tile;
    protected final mekanism.api.gas.IGasHandler handler;
    protected final IGasTankInfoProvider info;
    protected final ForgeDirection side;

    public GasHandlerWrapper(TileEntity tile, mekanism.api.gas.IGasHandler handler, ForgeDirection side) {
        this.tile = tile;
        this.handler = handler;
        this.info = handler instanceof IGasTankInfoProvider tankInfo ? tankInfo : null;
        this.side = side;
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
        if (info != null) {
            GasTankInfo[] tanks = info.getTankInfo();
            return tanks != null ? tanks.length : 0;
        }
        return 1;
    }

    @Override
    public GasStack getChemicalInTank(int tank) {
        if (info != null) {
            GasTankInfo[] tanks = info.getTankInfo();
            if (tanks != null && tank >= 0 && tank < tanks.length) {
                return tanks[tank].getGas();
            }
        }
        return null;
    }

    @Override
    public int getTankCapacity(int tank) {
        if (info != null) {
            GasTankInfo[] tanks = info.getTankInfo();
            if (tanks != null && tank >= 0 && tank < tanks.length) {
                return tanks[tank].getMaxGas();
            }
        }
        return 0;
    }

    @Override
    public boolean isGasValid(int tank, GasStack stack) {
        if (!isSideAllowedForInput() || stack == null || stack.getGas() == null) {
            return false;
        }
        return handler.canReceiveGas(side, stack.getGas());
    }

    @Override
    public int insertGas(GasStack stack, boolean doTransfer) {
        if (!isSideAllowedForInput() || stack == null || stack.amount <= 0) {
            return 0;
        }
        return handler.receiveGas(side, stack, doTransfer);
    }

    @Override
    public GasStack extractGas(int amount, boolean doTransfer) {
        if (!isSideAllowedForOutput() || amount <= 0) {
            return null;
        }
        return handler.drawGas(side, amount, doTransfer);
    }

    @Override
    public GasStack extractGas(GasStack stack, boolean doTransfer) {
        if (!isSideAllowedForOutput() || stack == null || stack.amount <= 0) {
            return null;
        }
        return handler.drawGas(side, stack.amount, doTransfer);
    }
}
