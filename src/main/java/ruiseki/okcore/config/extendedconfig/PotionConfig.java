package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.potion.Potion;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;

/**
 * Config for potions.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class PotionConfig extends ExtendedConfig<PotionConfig, Potion> {

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
     * @param elementFactory Function factory to create the Potion instance.
     */
    public PotionConfig(ModBase mod, int defaultId, String namedId, String comment,
        Function<PotionConfig, Potion> elementFactory) {
        super(mod, defaultId != 0, namedId, comment, elementFactory);
        this.ID = defaultId;
    }

    @Override
    public String getUnlocalizedName() {
        return "potions." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && this.ID != 0;
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.POTION;
    }

}
