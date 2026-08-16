package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.block.IBlockRarityProvider;
import ruiseki.okcore.config.configurable.ConfigurableBlockDoor;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A hybrid of {@link ItemBlockMetadata} and {@link net.minecraft.item.ItemDoor}.
 *
 * @author josephcsible
 *
 */
public class ItemDoorMetadata extends ItemDoor {

    protected InformationProviderComponent informationProvider;
    protected IBlockRarityProvider rarityProvider = null;

    protected final ConfigurableBlockDoor block;

    public ItemDoorMetadata(Block block) {
        super(block.getMaterial());
        this.block = (ConfigurableBlockDoor) block;
        this.block.item = this;
        informationProvider = new InformationProviderComponent(block);
        if (block instanceof IBlockRarityProvider) {
            rarityProvider = (IBlockRarityProvider) block;
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return block.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName() {
        return block.getUnlocalizedName();
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return block.getCreativeTabToDisplayOn();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean flag) {
        super.addInformation(itemStack, player, list, flag);
        block.addInformation(itemStack, player, list, flag);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName());
        informationProvider.addInformation(itemStack, player, list, flag);
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        if (rarityProvider != null) {
            return rarityProvider.getRarity(itemStack);
        }
        return super.getRarity(itemStack);
    }
}
