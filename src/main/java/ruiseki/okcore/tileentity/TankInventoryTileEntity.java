package ruiseki.okcore.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import lombok.experimental.Delegate;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.component.FluidHandlerComponent;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.fluid.handler.SmartTank;
import ruiseki.okcore.persist.nbt.NBTPersist;

/**
 * A TileEntity that has an inventory and a tank that can accept fluids or only one type of fluid.
 *
 * @author rubensworks
 *
 */
public abstract class TankInventoryTileEntity extends InventoryTileEntity
    implements SmartTank.IUpdateListener, IFluidHandler {

    @NBTPersist
    private SmartTank tank = null;
    protected int tankSize;

    @Delegate
    private final FluidHandlerComponent fluidhandler = new FluidHandlerComponent(this);

    /**
     * Make new tile with a tank that can accept anything and an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     * @param inventoryName Internal name of the inventory.
     * @param tankSize      Size (mB) of the tank.
     * @param stackSize     The maximum stacksize each slot can have.
     */
    public TankInventoryTileEntity(int inventorySize, String inventoryName, int tankSize, int stackSize) {
        super(inventorySize, inventoryName, stackSize);
        this.tankSize = tankSize;
        tank = newTank(tankSize);
        this.capabilityCache
            .addCapabilityResolver(BasicCapabilityResolver.create(CapabilityFluidHandler.FLUID_HANDLER, () -> tank));
    }

    protected SmartTank newTank(int tankSize) {
        SmartTank tank = new SmartTank(tankSize);
        tank.setTileEntity(this);
        return tank;
    }

    /**
     * Make new tile with a tank that can accept only one fluid and an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     * @param inventoryName Internal name of the inventory.
     * @param stackSize     The maximum stacksize each slot can have
     * @param tankSize      Size (mB) of the tank.
     * @param acceptedFluid Type of Fluid to accept.
     */
    public TankInventoryTileEntity(int inventorySize, String inventoryName, int stackSize, int tankSize,
        Fluid acceptedFluid) {
        this(inventorySize, inventoryName, stackSize, tankSize);
        this.tankSize = tankSize;
        tank.setRestriction(acceptedFluid);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        tank.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tank.writeToNBT(tag);
    }

    /**
     * Get the internal tank
     *
     * @return The internal SingleUseTank
     */
    public SmartTank getTank() {
        return tank;
    }

    protected boolean isUpdateInventoryHashOnTankContentsChanged() {
        return true;
    }

    /**
     * Fills fluid into internal tanks.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param doFill   If false, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    public int fill(FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    /**
     * Drains fluid out of internal tanks.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return tank.drain(resource, doDrain);
    }

    /**
     * Drains fluid out of internal tanks.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return tank.getTankProperties();
    }

    @Override
    public void onTankChanged() {
        sendUpdate();
    }

    @Override
    protected void onSendUpdate() {
        super.onSendUpdate();
        if (getPos().getBlock(worldObj)
            .hasComparatorInputOverride()) {
            worldObj.notifyBlocksOfNeighborChange(getPos().getX(), getPos().getY(), getPos().getZ(), this.getBlock());
        }
    }
}
