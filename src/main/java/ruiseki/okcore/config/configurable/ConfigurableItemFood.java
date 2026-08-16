package ruiseki.okcore.config.configurable;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
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
public class ConfigurableItemFood extends ItemFood implements IConfigurableItem {

    protected ItemConfig eConfig = null;

    /**
     * Make a new blockState instance.
     *
     * @param eConfig             Config for this blockState.
     * @param healAmount          Amount of health to regen.
     * @param saturationModifier  The modifier for the saturation.
     * @param isWolfsFavoriteMeat If this is wolf food.
     */
    public ConfigurableItemFood(ExtendedConfig<ItemConfig> eConfig, int healAmount, float saturationModifier,
        boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
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
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName());
    }

}
