package ruiseki.okcore.modcompat.baubles;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import baubles.api.BaublesApi;
import ruiseki.okcore.Reference;
import ruiseki.okcore.inventory.IInventoryLocation;
import ruiseki.okcore.item.capability.wrapper.InvWrapper;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

/**
 * Extends the iteratable inventory with Baubles slots.
 */
public class InventoryLocationBaubles implements IInventoryLocation {

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(Reference.MOD_ID, "baubles");
    }

    @Override
    @Nullable
    public IItemHandlerModifiable getInventory(EntityPlayer player) {
        return new InvWrapper(BaublesApi.getBaubles(player));
    }

    @Override
    public ItemStack getItemInSlot(EntityPlayer player, int slot) {
        return BaublesApi.getBaubles(player)
            .getStackInSlot(slot);
    }

    @Override
    public void setItemInSlot(EntityPlayer player, int slot, ItemStack itemStack) {
        BaublesApi.getBaubles(player)
            .setInventorySlotContents(slot, itemStack);
    }
}
