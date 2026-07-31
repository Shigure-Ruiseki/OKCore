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
import ruiseki.okcore.helper.LangHelpers;

/**
 * Item food that can hold ExtendedConfigs
 * 
 * @author rubensworks
 *
 */
public class ConfigurableItemBucket extends ItemBucket implements IConfigurable {

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;

    protected boolean canPickUp = true;

    /**
     * Make a new bucket instance.
     * 
     * @param eConfig Config for this blockState.
     * @param block   The fluid blockState it can pick up.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableItemBucket(ExtendedConfig eConfig, Block block) {
        super(block);
        this.setConfig(eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
        setContainerItem(Items.bucket);
    }

    @SuppressWarnings("rawtypes")
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName());
    }

}
