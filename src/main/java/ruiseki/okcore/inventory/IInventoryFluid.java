package ruiseki.okcore.inventory;

import net.minecraft.inventory.IInventory;

import ruiseki.okcore.fluid.handler.IFluidHandler;

/**
 * An inventory that can also contain fluids.
 * 
 * @author rubensworks
 */
public interface IInventoryFluid extends IInventory {

    public IFluidHandler getFluidHandler();

}
