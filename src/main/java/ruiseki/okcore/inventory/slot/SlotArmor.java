package ruiseki.okcore.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.helper.ItemStackHelpers;

/**
 * Slot that is used to hold armor.
 *
 * @author rubensworks
 *
 */
public class SlotArmor extends SlotExtended {

    private final int armorIndex;
    private final EntityPlayer player;

    /**
     * Make a new instance.
     *
     * @param inventory  The inventory this slot will be in.
     * @param index      The index of this slot.
     * @param x          X coordinate.
     * @param y          Y coordinate.
     * @param player     The player entity.
     * @param armorIndex The index of the armor slot.
     */
    public SlotArmor(IInventory inventory, int index, int x, int y, EntityPlayer player, int armorIndex) {
        super(inventory, index, x, y);
        this.armorIndex = armorIndex;
        this.player = player;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        if (ItemStackHelpers.isEmpty(itemStack)) return false;
        return itemStack.getItem()
            .isValidArmor(itemStack, armorIndex, player);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getBackgroundIconIndex() {
        return ItemArmor.func_94602_b(this.armorIndex);
    }

}
