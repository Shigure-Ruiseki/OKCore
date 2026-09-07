package ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler;

import java.util.function.Function;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.gas.IGasTankInfoProvider;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.base.ISideConfiguration;

public class MekanismGasHandler implements IGasHandler, IGasTankInfoProvider {

    private final TileEntity tile;
    private final BiFunction3Param<ForgeDirection, GasStack, Boolean, Integer> receiveGasFunc;
    private final BiFunction3Param<ForgeDirection, Integer, Boolean, GasStack> drawGasFunc;
    private final Function<ForgeDirection, Boolean> canReceiveFunc;
    private final Function<ForgeDirection, Boolean> canDrawFunc;
    private final Function<TileEntity, GasTankInfo[]> tankInfoSupplier;

    @FunctionalInterface
    public interface BiFunction3Param<T, U, V, R> {

        R apply(T t, U u, V v);
    }

    public MekanismGasHandler(TileEntity tile, Function<TileEntity, GasTank> tankSupplier) {
        this.tile = tile;

        this.receiveGasFunc = (side, stack, doTransfer) -> {
            if (stack == null || !canReceiveGas(side, stack.getGas())) return 0;
            GasTank tank = tankSupplier.apply(tile);
            if (tank == null) return 0;
            return tank.receive(stack, doTransfer);
        };

        this.drawGasFunc = (side, amount, doTransfer) -> {
            GasTank tank = tankSupplier.apply(tile);
            if (tank == null || !canDrawGas(side, tank.getGasType())) return null;
            return tank.draw(amount, doTransfer);
        };

        this.canReceiveFunc = (side) -> {
            GasTank tank = tankSupplier.apply(tile);
            return tank != null && tank.getNeeded() > 0;
        };

        this.canDrawFunc = (side) -> {
            GasTank tank = tankSupplier.apply(tile);
            return tank != null && tank.getStored() > 0;
        };

        this.tankInfoSupplier = (t) -> {
            GasTank tank = tankSupplier.apply(t);
            if (tank == null) return IGasTankInfoProvider.NONE;
            return new GasTankInfo[] { tank };
        };
    }

    public MekanismGasHandler(TileEntity tile, IGasHandler handler) {
        this.tile = tile;
        this.receiveGasFunc = handler::receiveGas;
        this.drawGasFunc = handler::drawGas;
        this.canReceiveFunc = (side) -> handler.canReceiveGas(side, null);
        this.canDrawFunc = (side) -> handler.canDrawGas(side, null);

        this.tankInfoSupplier = (t) -> {
            if (t instanceof IGasTankInfoProvider infoProvider) {
                return infoProvider.getTankInfo();
            }
            return IGasTankInfoProvider.NONE;
        };
    }

    @Override
    public @NotNull GasTankInfo @NotNull [] getTankInfo() {
        if (tankInfoSupplier != null) {
            return tankInfoSupplier.apply(tile);
        }
        return IGasTankInfoProvider.NONE;
    }

    @Override
    public int receiveGas(ForgeDirection side, GasStack stack, boolean doTransfer) {
        if (stack == null) return 0;
        if (receiveGasFunc != null) {
            return receiveGasFunc.apply(side, stack, doTransfer);
        }
        return 0;
    }

    @Override
    public int receiveGas(ForgeDirection side, GasStack stack) {
        return receiveGas(side, stack, true);
    }

    @Override
    public GasStack drawGas(ForgeDirection side, int amount, boolean doTransfer) {
        if (amount <= 0) return null;
        if (drawGasFunc != null) {
            return drawGasFunc.apply(side, amount, doTransfer);
        }
        return null;
    }

    @Override
    public GasStack drawGas(ForgeDirection side, int amount) {
        return drawGas(side, amount, true);
    }

    @Override
    public boolean canReceiveGas(ForgeDirection side, Gas type) {
        if (tile instanceof ISideConfiguration configTile) {
            boolean sideAllowed = configTile.getConfig()
                .getSidesForData(TransmissionType.GAS, configTile.getOrientation(), 1)
                .contains(side);
            if (!sideAllowed) return false;
        }
        return canReceiveFunc != null && canReceiveFunc.apply(side);
    }

    @Override
    public boolean canDrawGas(ForgeDirection side, Gas type) {
        if (tile instanceof ISideConfiguration configTile) {
            boolean sideAllowed = configTile.getConfig()
                .getSidesForData(TransmissionType.GAS, configTile.getOrientation(), 2)
                .contains(side);
            if (!sideAllowed) return false;
        }
        return canDrawFunc != null && canDrawFunc.apply(side);
    }
}
