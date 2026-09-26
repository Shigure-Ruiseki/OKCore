package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.item.ItemBlockMetadata;

/**
 * Config for blocks.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class BlockConfig extends ExtendedConfig<BlockConfig, Block> {

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param enabled        If this is enabled.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Block instance.
     */
    public BlockConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<BlockConfig, Block> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
    }

    @Override
    public String getUnlocalizedName() {
        return "blocks." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public String getFullUnlocalizedName() {
        return "tile." + getUnlocalizedName() + ".name";
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.BLOCK;
    }

    /**
     * If hasSubTypes() returns true this method can be overwritten to define another ItemBlock class
     *
     * @return the ItemBlock class to use for the target blockState.
     */
    public Class<? extends Item> getItemBlockClass() {
        return ItemBlockMetadata.class;
    }

    /**
     * If the IConfigurable is registered in the OreDictionary, use this name to identify it.
     *
     * @return the name this IConfigurable is registered with in the OreDictionary.
     */
    public String getOreDictionaryId() {
        return null;
    }

    /**
     * Get the item corresponding to the block.
     * Will return Items.AIR rather than null if there isn't one.
     *
     * @return The item.
     */
    @Nonnull
    public Item getItemInstance() {
        return Item.getItemFromBlock(getInstance());
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
