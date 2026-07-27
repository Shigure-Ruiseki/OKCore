package ruiseki.okcore.config.configurable;

/**
 * Configurable blocks.
 * 
 * @author rubensworks
 */
public interface IConfigurableBlock extends IConfigurable {

    /**
     * If this block has a corresponding GUI.
     * This required the block to implement {@link ruiseki.okcore.inventory.IGuiContainerProvider}.
     * 
     * @return If it has a GUI.
     */
    public boolean hasGui();

}
