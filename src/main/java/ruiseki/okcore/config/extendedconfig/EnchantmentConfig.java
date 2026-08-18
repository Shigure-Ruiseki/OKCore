package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.enchantment.Enchantment;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;

/**
 * Config for enchantments.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class EnchantmentConfig extends ExtendedConfig<EnchantmentConfig, Enchantment> {

    /**
     * The ID for the configurable.
     */
    public int ID;

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param defaultId      The default ID for the configurable.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Enchantment instance.
     */
    public EnchantmentConfig(ModBase mod, int defaultId, String namedId, String comment,
        Function<EnchantmentConfig, Enchantment> elementFactory) {
        super(mod, defaultId != 0, namedId, comment, elementFactory);
        this.ID = defaultId;
    }

    @Override
    public String getUnlocalizedName() {
        return "enchantments." + getNamedId();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && this.ID != 0;
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.ENCHANTMENT;
    }

}
