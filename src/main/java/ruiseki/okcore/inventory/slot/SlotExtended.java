package ruiseki.okcore.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import lombok.Getter;
import lombok.Setter;
import ruiseki.okcore.client.gui.ISlotBackground;

/**
 * A slot with some fancy and fun extra features.
 *
 * @author rubensworks
 */
public class SlotExtended extends Slot implements ISlotBackground {

    protected ResourceLocation backgroundTexture;

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

    @Override
    public ResourceLocation getBackgroundTexture() {
        return backgroundTexture;
    }

    @Override
    public void setBackgroundTexture(ResourceLocation backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
    }
}
