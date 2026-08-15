package ruiseki.okcore.config.configurable;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.IItemSharedTag;

/**
 * Item that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ConfigurableItem extends Item implements IConfigurableItem, IItemCapability, IItemSharedTag {

    protected ItemConfig eConfig = null;

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ConfigurableItem(ExtendedConfig<ItemConfig> eConfig) {
        this.setConfig((ItemConfig) eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
        this.setTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
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
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean flag) {
        super.addInformation(itemStack, entityPlayer, list, flag);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName(itemStack));
    }

}
