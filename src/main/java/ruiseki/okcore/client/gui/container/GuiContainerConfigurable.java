package ruiseki.okcore.client.gui.container;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.inventory.container.InventoryContainerConfigurable;

/**
 * A gui for configurables.
 *
 * @author rubensworks
 */
public abstract class GuiContainerConfigurable<C extends InventoryContainerConfigurable>
    extends GuiContainerExtended<C> {

    /**
     * Make a new instance.
     *
     * @param container The container to make the GUI for.
     */
    public GuiContainerConfigurable(C container) {
        super(container);
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return new ResourceLocation(
            getContainer().getGuiProvider()
                .getModGui()
                .getModId(),
            getContainer().getGuiProvider()
                .getConfig()
                .getNamedId() + ".png");
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
