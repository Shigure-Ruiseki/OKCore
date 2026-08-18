package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.block.Block;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;

/**
 * Config for blocks with tile entities.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public class BlockContainerConfig extends BlockConfig {

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param enabled        If this should is enabled.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Block instance.
     */
    public BlockContainerConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<BlockConfig, Block> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.BLOCKCONTAINER;
    }

}
