package ruiseki.okcore.config.configurable;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
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
public class ConfigurableItemFood extends ItemFood implements IConfigurable {

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;

    /**
     * Make a new blockState instance.
     *
     * @param eConfig             Config for this blockState.
     * @param healAmount          Amount of health to regen.
     * @param saturationModifier  The modifier for the saturation.
     * @param isWolfsFavoriteMeat If this is wolf food.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableItemFood(ExtendedConfig eConfig, int healAmount, float saturationModifier,
        boolean isWolfsFavoriteMeat) {
        super(healAmount, saturationModifier, isWolfsFavoriteMeat);
        this.setConfig(eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
        this.setTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
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
