package ruiseki.okcore.config.configurable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

import ruiseki.okcore.config.extendedconfig.EnchantmentConfig;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A simple configurable for Enchantments, will auto-register itself after construction.
 *
 * @author rubensworks
 *
 */
public class ConfigurableEnchantment extends Enchantment implements IConfigurable<EnchantmentConfig> {

    protected EnchantmentConfig eConfig = null;

    /**
     * Make a new Enchantment instance
     *
     * @param eConfig Config for this enchantment.
     * @param weight  The weight in which this enchantment should occurd
     * @param type    The type of enchantment
     */
    protected ConfigurableEnchantment(EnchantmentConfig eConfig, int weight, EnumEnchantmentType type) {
        super(eConfig.ID, weight, type);
        this.setConfig(eConfig);
        this.setName(eConfig.getUnlocalizedName());
        if (isAllowedOnBooks()) {
            addToBookList(this);
        }

    }

    private void setConfig(EnchantmentConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public EnchantmentConfig getConfig() {
        return eConfig;
    }

    @Override
    public String getTranslatedName(int level) {
        String enchantmentName = LangHelpers.localize(
            "enchantment." + eConfig.getMod()
                .getModId() + "." + eConfig.getNamedId());
        return enchantmentName + " " + LangHelpers.localize("enchantment.level." + level);
    }
}
