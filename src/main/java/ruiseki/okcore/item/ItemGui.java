package ruiseki.okcore.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.inventory.IGuiConstructor;

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
     * @param world     The world.
     * @param player    The player opening the GUI.
     * @param itemIndex The item index in the player inventory.
     */
    public void openGuiForItemIndex(World world, EntityPlayer player, int itemIndex) {
        if (!world.isRemote && player instanceof EntityPlayerMP playerMP) {
            IGuiConstructor constructor = getGuiProvider(world, player, itemIndex);
            if (constructor != null) {
                PlayerHelpers.openGui(playerMP, constructor, buf -> writeExtraGuiData(buf, world, player, itemIndex));
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (player instanceof FakePlayer) {
            return itemStack;
        }
        if (player instanceof EntityPlayerMP) {
            openGuiForItemIndex(world, player, player.inventory.currentItem);
        }
        return itemStack;
    }
}
