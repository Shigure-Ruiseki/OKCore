package ruiseki.okcore.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.Reference;
import ruiseki.okcore.item.capability.wrapper.InvWrapper;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;
import ruiseki.okcore.modcompat.backhand.BackhandHelpers;

/**
 * @author rubensworks
 */
public class InventoryLocationPlayer implements IInventoryLocation {

    private static InventoryLocationPlayer INSTANCE = new InventoryLocationPlayer();

    public static InventoryLocationPlayer getInstance() {
        return INSTANCE;
    }

    private InventoryLocationPlayer() {

    }

    @Override
    public ResourceLocation getUniqueName() {
        return new ResourceLocation(Reference.MOD_ID, "player");
    }

    @Override
    public IItemHandlerModifiable getInventory(EntityPlayer player) {
        return new InvWrapper(player.inventory);
    }

    @Override
    public ItemStack getItemInSlot(EntityPlayer player, int slot) {
        return player.inventory.getStackInSlot(slot);
    }

    @Override
    public void setItemInSlot(EntityPlayer player, int slot, ItemStack itemStack) {
        player.inventory.setInventorySlotContents(slot, itemStack);
    }

    public ItemLocation handToLocation(EntityPlayer player, InteractionHand hand, int selectedSlot) {
        int slot;
        if (hand == InteractionHand.MAIN_HAND) {
            slot = selectedSlot;
        } else {
            // Last slot in Inventory compartments
            slot = BackhandHelpers.getOffhandSlot(player);
        }
        return new ItemLocation(this, slot);
    }

}
