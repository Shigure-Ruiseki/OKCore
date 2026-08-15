package ruiseki.okcore.config.configurable;

import ruiseki.okcore.block.IBlockTooltipProvider;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Configurable blocks.
 *
 * @author rubensworks
 */
public interface IConfigurableBlock extends IConfigurable<BlockConfig>, IBlockPropertyProvider, IBlockTooltipProvider {

    /**
     * If this block has a corresponding GUI.
     * This required the block to implement {@link ruiseki.okcore.inventory.IGuiContainerProvider}.
     *
     * @return If it has a GUI.
     */
    public boolean hasGui();

}
