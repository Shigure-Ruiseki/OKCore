package ruiseki.commoncapabilities.modcompat.vanilla.capability.work;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

import ruiseki.commoncapabilities.api.capability.work.IWorker;

/**
 * Worker capability for the vanilla furnace tile entity.
 * 
 * @author rubensworks
 */
public class VanillaFurnaceWorker implements IWorker {

    private final TileEntityFurnace furnace;

    public VanillaFurnaceWorker(TileEntityFurnace furnace) {
        this.furnace = furnace;
    }

    @Override
    public boolean hasWork() {
        ItemStack toMelt = furnace.getStackInSlot(0);
        return toMelt != null && FurnaceRecipes.smelting()
            .getSmeltingResult(toMelt) != null;
    }

    @Override
    public boolean canWork() {
        return furnace.isBurning() || TileEntityFurnace.getItemBurnTime(furnace.getStackInSlot(1)) > 0;
    }
}
