package ruiseki.okcore.client.gui.container;

import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.container.InventoryContainerConfigurable;

/**
 * A gui for configurables.
 *
 * @author rubensworks
 */
public abstract class GuiContainerConfigurable<C extends InventoryContainerConfigurable> extends GuiContainerExtended {

    /**
     * Make a new instance.
     *
     * @param container The container to make the GUI for.
     */
    public GuiContainerConfigurable(C container) {
        super(container);
    }

    protected C getContainer() {
        return (C) super.getContainer();
    }

    @Override
    public String getGuiTexture() {
        return getContainer().getGuiProvider()
            .getModGui()
            .getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI)
            + getContainer().getGuiProvider()
                .getConfig()
                .getNamedId()
            + ".png";
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(
            LangHelpers.localize(
                getContainer().getGuiProvider()
                    .getConfig()
                    .getFullUnlocalizedName()),
            8,
            6,
            4210752);
    }

}
