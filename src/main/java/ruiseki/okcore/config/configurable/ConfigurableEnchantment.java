package ruiseki.okcore.config.configurable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;

import ruiseki.okcore.config.extendedconfig.EnchantmentConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A simple configurable for Enchantments, will auto-register itself after construction.
 * 
 * @author rubensworks
 *
 */
public class ConfigurableEnchantment extends Enchantment implements IConfigurable {

    protected ExtendedConfig<EnchantmentConfig> eConfig = null;

    /**
     * Make a new Enchantment instance
     * 
     * @param eConfig Config for this enchantment.
     * @param weight  The weight in which this enchantment should occurd
     * @param type    The type of enchantment
     */
    protected ConfigurableEnchantment(ExtendedConfig<EnchantmentConfig> eConfig, int weight, EnumEnchantmentType type) {
        super(eConfig.downCast().ID, weight, type);
        this.setConfig(eConfig);
        this.setName(eConfig.getUnlocalizedName());
        if (isAllowedOnBooks()) {
            addToBookList(this);
        }

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
    }

    @Override
    public String getTranslatedName(int level) {
        String enchantmentName = LangHelpers.localize(
            "enchantment." + eConfig.getMod()
                .getModId()
                + "."
                + eConfig.downCast()
                    .getNamedId());
        return enchantmentName + " " + LangHelpers.localize("enchantment.level." + level);
    }
}
