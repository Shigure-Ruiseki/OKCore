package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Item that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ItemBase extends Item implements IItemCapability, IItemSharedTag {

    /**
     * Make a new item instance.
     *
     */
    public ItemBase() {}

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean flag) {
        super.addInformation(itemStack, entityPlayer, list, flag);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName(itemStack));
    }

}
