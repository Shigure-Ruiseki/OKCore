package ruiseki.commoncapabilities.modcompat.mekansim.api.gas;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.common.Optional;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTankInfo;

/**
 * Modern wrapper interface for Mekanism's gas handling capability.
 * <p>
 * Abstracts away direction-based ({@link ForgeDirection}) interactions and exposes
 * tank-index-based accessors similar to modern fluid handlers.
 */
@Optional.InterfaceList({ @Optional.Interface(iface = "mekanism.api.gas.IGasHandler", modid = "Mekanism"),
    @Optional.Interface(iface = "mekanism.api.gas.IGasTankInfoProvider", modid = "Mekanism") })
public interface IGasHandler extends mekanism.api.gas.IGasHandler, mekanism.api.gas.IGasTankInfoProvider {

    /**
     * Gets the number of internal gas tanks available in this handler.
     *
     * @return The total number of tanks.
     */
    int getTanks();

    /**
     * Gets the {@link GasStack} currently stored in the specified tank slot.
     *
     * @param tank Slot index of the tank to query.
     * @return The {@link GasStack} in the tank, or {@code null} if empty.
     */
    GasStack getChemicalInTank(int tank);

    /**
     * Gets the maximum capacity of the specified tank slot.
     *
     * @param tank Slot index of the tank to query.
     * @return Maximum amount of gas the tank can hold.
     */
    int getTankCapacity(int tank);

    /**
     * Checks if the given {@link GasStack} can be validly inserted into the specified tank.
     *
     * @param tank  Slot index of the tank.
     * @param stack The gas stack to validate.
     * @return {@code true} if the stack can be accepted by this tank, {@code false} otherwise.
     */
    boolean isGasValid(int tank, GasStack stack);

    /**
     * Inserts gas into internal tanks. Distribution across tanks is determined by implementation.
     *
     * @param stack      GasStack representing the gas type and maximum amount to insert.
     * @param doTransfer If {@code false}, insertion is only simulated.
     * @return Amount of gas that was (or would have been) successfully accepted.
     */
    int insertGas(GasStack stack, boolean doTransfer);

    /**
     * Extracts gas from internal tanks up to a maximum amount.
     *
     * @param amount     Maximum amount of gas to extract.
     * @param doTransfer If {@code false}, extraction is only simulated.
     * @return GasStack representing the gas type and amount extracted, or {@code null} if empty.
     */
    GasStack extractGas(int amount, boolean doTransfer);

    /**
     * Extracts a specific gas type from internal tanks.
     *
     * @param stack      GasStack representing the desired gas type and maximum amount to extract.
     * @param doTransfer If {@code false}, extraction is only simulated.
     * @return GasStack representing the gas type and amount extracted, or {@code null} if empty/mismatched.
     */
    GasStack extractGas(GasStack stack, boolean doTransfer);

    /**
     * Returns an empty stack representation for this chemical/gas handler.
     *
     * @return {@code null} by default in 1.7.10.
     */
    default GasStack getEmptyStack() {
        return null;
    }

    // Overrides / Bridge Methods for Mekanism IGasHandler

    @Override
    default int receiveGas(ForgeDirection side, GasStack stack, boolean doTransfer) {
        return insertGas(stack, doTransfer);
    }

    @Override
    default int receiveGas(ForgeDirection side, GasStack stack) {
        return receiveGas(side, stack, true);
    }

    @Override
    default GasStack drawGas(ForgeDirection side, int amount, boolean doTransfer) {
        return extractGas(amount, doTransfer);
    }

    @Override
    default GasStack drawGas(ForgeDirection side, int amount) {
        return drawGas(side, amount, true);
    }

    @Override
    default boolean canReceiveGas(ForgeDirection side, Gas type) {
        if (type == null) return false;
        GasStack mockStack = new GasStack(type, 1);
        int tanks = getTanks();
        for (int i = 0; i < tanks; i++) {
            if (isGasValid(i, mockStack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean canDrawGas(ForgeDirection side, Gas type) {
        if (type == null) return false;
        int tanks = getTanks();
        for (int i = 0; i < tanks; i++) {
            GasStack inTank = getChemicalInTank(i);
            if (inTank != null && inTank.getGas() == type && inTank.amount > 0) {
                return true;
            }
        }
        return false;
    }

    // Overrides for Mekanism IGasTankInfoProvider

    @Override
    default GasTankInfo @NotNull [] getTankInfo() {
        int tanks = getTanks();
        if (tanks <= 0) {
            return NONE;
        }

        GasTankInfo[] infos = new GasTankInfo[tanks];
        for (int i = 0; i < tanks; i++) {
            GasStack stack = getChemicalInTank(i);
            int capacity = getTankCapacity(i);
            infos[i] = new GasTankInfoWrapper(stack, capacity);
        }
        return infos;
    }

    /**
     * Immutable wrapper class implementing {@link GasTankInfo} for tank queries.
     */
    static class GasTankInfoWrapper implements GasTankInfo {

        private final GasStack stack;
        private final int capacity;

        public GasTankInfoWrapper(GasStack stack, int capacity) {
            this.stack = stack;
            this.capacity = capacity;
        }

        @Override
        public GasStack getGas() {
            return stack;
        }

        @Override
        public int getStored() {
            return stack != null ? stack.amount : 0;
        }

        @Override
        public int getMaxGas() {
            return capacity;
        }
    }
}
