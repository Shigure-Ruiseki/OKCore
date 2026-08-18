package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;

/**
 * Config for items.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class ItemConfig extends ExtendedConfig<ItemConfig, Item> {

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param enabled        If this should is enabled.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Item instance.
     */
    public ItemConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<ItemConfig, Item> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
    }

    @Override
    public String getUnlocalizedName() {
        return "items." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public String getFullUnlocalizedName() {
        return "item." + getUnlocalizedName() + ".name";
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.ITEM;
    }

    /**
     * If the IConfigurable is registered in the OreDictionary, use this name to identify it.
     *
     * @return the name this IConfigurable is registered with in the OreDictionary.
     */
    public String getOreDictionaryId() {
        return null;
    }

    @Override
    public void onRegistered() {
        super.onRegistered();
        if (isEnabled()) {
            if (getOreDictionaryId() != null) {
                OreDictionary.registerOre(
                    getOreDictionaryId(),
                    new ItemStack(this.getInstance(), 1, OreDictionary.WILDCARD_VALUE));
            }
        }
    }

    /**
     * Get the creative tab for this item.
     *
     * @return The creative tab, by default the value in {@link ModBase#getDefaultCreativeTab()}.
     */
    public CreativeTabs getTargetTab() {
        return getMod().getDefaultCreativeTab();
    }

}
