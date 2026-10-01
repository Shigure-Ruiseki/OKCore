package ruiseki.okcore.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IContainerConstructor;
import ruiseki.okcore.inventory.InteractionHand;
import ruiseki.okcore.inventory.InventoryLocationPlayer;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.modcompat.backhand.BackhandHelpers;

/**
 * Configurable item that can show a GUI on right clicking.
 *
 * @author rubensworks
 */
public abstract class ItemGui extends ItemBase implements IItemGui {

    protected ItemGui() {
        super();
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
        if (!ItemHelpers.isEmpty(itemstack) && player instanceof EntityPlayerMP
            && player.openContainer != null
            && player.openContainer.getClass() == getContainerClass(player.worldObj, player, itemstack)) {
            player.closeScreen();
        }
        return super.onDroppedByPlayer(itemstack, player);
    }

    /**
     * Open the GUI for a certain item slot index in the player inventory.
     *
     * @param world        The world.
     * @param player       The player opening the GUI.
     * @param itemLocation The item with its location.
     */
    public void openGuiForItemIndex(World world, EntityPlayerMP player, ItemLocation itemLocation) {
        if (!world.isRemote) {
            IContainerConstructor constructor = getContainer(world, player, itemLocation);
            if (constructor != null) {
                PlayerHelpers.openGui(player, constructor, buf -> writeExtraGuiData(buf, world, player, itemLocation));
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (player instanceof FakePlayer) {
            return itemStack;
        }
        if (player instanceof EntityPlayerMP playerMP) {
            InteractionHand hand = InteractionHand.getHand(player, itemStack);
            int slot = (hand == InteractionHand.OFF_HAND) ? BackhandHelpers.getOffhandSlot(player)
                : player.inventory.currentItem;

            openGuiForItemIndex(
                world,
                playerMP,
                InventoryLocationPlayer.getInstance()
                    .handToLocation(player, hand, slot));
        }
        return itemStack;
    }
}
