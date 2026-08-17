package ruiseki.okcore.config.configurable;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Item food that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ConfigurableItemBucket extends ItemBucket implements IConfigurableItem {

    protected ItemConfig eConfig = null;

    protected boolean canPickUp = true;

    /**
     * Make a new bucket instance.
     *
     * @param eConfig Config for this blockState.
     * @param block   The fluid blockState it can pick up.
     */
    public ConfigurableItemBucket(ExtendedConfig<ItemConfig> eConfig, Block block) {
        super(block);
        this.setConfig((ItemConfig) eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
        this.setTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
        setContainerItem(Items.bucket);
    }

    private void setConfig(ItemConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ItemConfig getConfig() {
        return eConfig;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName());
    }

}
