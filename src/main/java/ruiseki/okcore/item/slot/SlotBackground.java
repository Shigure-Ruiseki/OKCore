package ruiseki.okcore.item.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.ISlotBackground;

public class SlotBackground extends Slot implements ISlotBackground {

    @SideOnly(Side.CLIENT)
    protected ResourceLocation background;

    public SlotBackground(IInventory inventory, int slotIndex, int xDisplayPosition, int yDisplayPosition) {
        super(inventory, slotIndex, xDisplayPosition, yDisplayPosition);
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return background;
    }

    @Override
    public void setBackgroundTexture(ResourceLocation backgroundTexture) {
        this.background = backgroundTexture;
    }
}
