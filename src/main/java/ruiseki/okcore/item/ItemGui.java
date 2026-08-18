package ruiseki.okcore.item;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.GuiHandler;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * Configurable item that can show a GUI on right clicking.
 *
 * @author rubensworks
 *
 */
public abstract class ItemGui extends ItemBase implements IGuiContainerProvider {

    /**
     * Make a new item instance.
     */
    protected ItemGui() {
        super();
    }

    @Override
    public abstract Class<? extends Container> getContainer();

    @Override
    @SideOnly(Side.CLIENT)
    public abstract Class<? extends GuiScreen> getGui();

    @Override
    public boolean onDroppedByPlayer(ItemStack itemstack, EntityPlayer player) {
        if (itemstack != null && player instanceof EntityPlayerMP
            && player.openContainer != null
            && player.openContainer.getClass() == getContainer()) {
            player.closeScreen();
        }
        return super.onDroppedByPlayer(itemstack, player);
    }

    /**
     * Open the gui for a certain item index in the player inventory.
     *
     * @param world     The world.
     * @param player    The player.
     * @param itemIndex The item index in the player inventory.
     */
    public void openGuiForItemIndex(World world, EntityPlayer player, int itemIndex) {
        getModGui().getGuiHandler()
            .setTemporaryData(GuiHandler.GuiType.ITEM, itemIndex);
        if (!world.isRemote || isClientSideOnlyGui()) {
            player.openGui(getModGui(), getGuiID(), world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
    }

    protected boolean isClientSideOnlyGui() {
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        openGuiForItemIndex(world, player, player.inventory.currentItem);
        return itemStack;
    }

}
