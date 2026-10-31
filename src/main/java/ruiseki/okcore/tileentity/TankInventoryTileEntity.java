
package ruiseki.okcore.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import lombok.experimental.Delegate;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.component.FluidHandlerComponent;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.SmartTank;

public abstract class TankInventoryTileEntity extends InventoryTileEntity
    implements SmartTank.IUpdateListener, IFluidHandler {

    private SmartTank tank = null;
    protected int tankSize;

    @Delegate
    private final FluidHandlerComponent fluidhandler = new FluidHandlerComponent(this);

    public TankInventoryTileEntity(int inventorySize, int stackSize, int tankSize) {
        super(inventorySize, stackSize);

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

    public TankInventoryTileEntity(int inventorySize, int stackSize, int tankSize, Fluid acceptedFluid) {
        this(inventorySize, stackSize, tankSize);
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

    public SmartTank getTank() {
        return tank;
    }

    protected boolean isUpdateInventoryHashOnTankContentsChanged() {
        return true;
    }

    public int fill(FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return tank.drain(resource, doDrain);
    }

    public FluidStack drain(int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public int getTanks() {
        return tank.getTanks();
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.tank.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tank.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return this.tank.isFluidValid(tank, stack);
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
