package ruiseki.okcore.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import lombok.Getter;
import lombok.Setter;

/**
 * A slot with some fancy and fun extra features.
 *
 * @author rubensworks
 */
public class SlotExtended extends SlotBackground {

    @Getter
    @Setter
    private boolean enabled = true;

    @Getter
    @Setter
    private boolean phantom = false;

    @Getter
    @Setter
    private boolean adjustable = true;

    public SlotExtended(IInventory inventoryIn, int index, int x, int y) {
        super(inventoryIn, index, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return isEnabled() && inventory.isItemValidForSlot(getSlotIndex(), stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return super.canTakeStack(playerIn) && !isPhantom();
    }

    public int getItemStackLimit(ItemStack stack) {
        return this.getSlotStackLimit();
    }
}
