package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Interface that can be applied to {@link net.minecraft.block.Block} or {@link net.minecraft.item.Item} so that they
 * can provide information
 * when the player hovers over the item in their inventory.
 * 
 * @see InformationProviderComponent
 * @author rubensworks
 *
 */
public interface IInformationProvider {

    /**
     * A prefix for blockState information.
     */
    public static String BLOCK_PREFIX = EnumChatFormatting.RED.toString();
    /**
     * A prefix for item information.
     */
    public static String ITEM_PREFIX = BLOCK_PREFIX;
    /**
     * A prefix for additional info.
     */
    public static String INFO_PREFIX = EnumChatFormatting.DARK_PURPLE.toString() + EnumChatFormatting.ITALIC.toString();

    /**
     * Get info for a given itemStack.
     * 
     * @param itemStack The itemStack that must be given information.
     * @return Information for that itemStack.
     */
    public String getInfo(ItemStack itemStack);

    /**
     * An extended way to provide additional information.
     * 
     * @param itemStack The itemStack that must be given information.
     * @param player    The player that asks for information.
     * @param list      The list of information.
     * @param flag      The tooltip flag type.
     */
    @SideOnly(Side.CLIENT)
    public void provideInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean flag);
}
