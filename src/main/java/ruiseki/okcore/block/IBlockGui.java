package ruiseki.okcore.block;

public interface IBlockGui {

    /**
     * If this block has a corresponding GUI.
     * This required the block to implement {@link ruiseki.okcore.inventory.IGuiContainerProvider}.
     *
     * @return If it has a GUI.
     */
    public boolean hasGui();
}
