package ruiseki.okcore.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.item.handler.IItemHandlerModifiable;

/**
 * A registerable inventory location.
 */
public interface IInventoryLocation {

    public ResourceLocation getUniqueName();

    @Nullable
    public IItemHandlerModifiable getInventory(EntityPlayer player);

    public ItemStack getItemInSlot(EntityPlayer player, int slot);

    public void setItemInSlot(EntityPlayer player, int slot, ItemStack itemStack);

}
