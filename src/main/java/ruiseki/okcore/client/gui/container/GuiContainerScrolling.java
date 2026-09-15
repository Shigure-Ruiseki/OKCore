package ruiseki.okcore.client.gui.container;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Slot;

import org.lwjgl.input.Keyboard;

import ruiseki.okcore.client.gui.component.GuiScrollBar;
import ruiseki.okcore.client.gui.component.input.GuiTextFieldExtended;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.inventory.container.ScrollingInventoryContainer;

/**
 * Gui for an inventory container that has a scrollbar and search field.
 *
 * @author rubensworks
 */
public abstract class GuiContainerScrolling<T extends ScrollingInventoryContainer<?>> extends GuiContainerExtended<T> {

    private static final int SEARCH_WIDTH = 89;

    private GuiTextFieldExtended searchField = null;
    private GuiScrollBar scrollbar = null;

    /**
     * Make a new instance.
     *
     * @param container The container to make the GUI for.
     */
    public GuiContainerScrolling(T container) {
        super(container);
    }

    @Override
    public void initGui() {
        super.initGui();

        if (isSearchEnabled()) {
            int searchWidth = getSearchWidth();
            int searchX = getSearchX();
            int searchY = getSearchY();
            if (this.searchField == null) {
                this.searchField = new GuiTextFieldExtended(
                    this.fontRendererObj,
                    this.guiLeft + searchX,
                    this.guiTop + searchY,
                    searchWidth,
                    this.fontRendererObj.FONT_HEIGHT);
                this.searchField.setMaxStringLength(15);
                this.searchField.setEnableBackgroundDrawing(false);
                this.searchField.setVisible(true);
                this.searchField.setTextColor(16777215);
                this.searchField.setCanLoseFocus(true);
                this.searchField.setText("");
                this.searchField.setWidth(searchWidth);
                this.searchField.setX(this.guiLeft + (searchX + searchWidth) - this.searchField.getWidth());
            } else {
                this.searchField.setWidth(searchWidth);
                this.searchField.setX(this.guiLeft + (searchX + searchWidth) - this.searchField.getWidth());
                this.searchField.setY(this.guiTop + searchY);
            }
            this.addWidget(this.searchField);
        }

        // Initial element load.
        if (scrollbar == null) {
            getContainer().updateFilter("");
            this.scrollbar = new GuiScrollBar(
                this.guiLeft + getScrollX(),
                this.guiTop + getScrollY(),
                getScrollHeight(),
                getContainer(),
                getContainer().getPageSize(),
                getScrollRegion());
            this.scrollbar.setTotalRows(getContainer().getFilteredItemCount() / getContainer().getColumns());
        } else {
            this.scrollbar.setX(this.guiLeft + getScrollX());
            this.scrollbar.setY(this.guiTop + getScrollY());
            this.scrollbar.setScollRegion(getScrollRegion());
        }

        this.addWidget(this.scrollbar);
        getScrollbar().scrollTo(this.scrollbar.getCurrentScroll());
    }

    /**
     * @return A custom region in which scrolling should also be allowed next to the scrollbar itself.
     */
    protected Rectangle getScrollRegion() {
        return null;
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        if (isSearchEnabled() && this.searchField.isFocused()) {
            if (this.searchField.charTyped(typedChar, keyCode)) {
                this.updateSearch(searchField.getText());
            }
            return true;
        } else {
            return super.charTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean keyPressed(int typedChar, int keyCode, int modifiers) {
        if (isSearchEnabled() && this.searchField.isFocused() && typedChar != Keyboard.KEY_ESCAPE) {
            if (this.searchField.keyPressed(typedChar, keyCode, modifiers)) {
                this.updateSearch(searchField.getText());
            }
            return true;
        } else {
            return super.keyPressed(typedChar, keyCode, modifiers);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (isSubsetRenderSlots()) {
            // Temporarily swap slot list, to avoid rendering all slots (which would include the hidden ones)
            List<Slot> oldSlots = this.container.inventorySlots;
            int startIndex = getContainer().getFirstElement();
            NonNullList<Slot> newSlots = NonNullList.create();
            newSlots.addAll(
                oldSlots.subList(
                    startIndex,
                    Math.min(
                        oldSlots.size(),
                        startIndex + (getContainer().getPageSize() * getContainer().getColumns()))));
            newSlots.addAll(oldSlots.subList(getContainer().getUnfilteredItemCount(), oldSlots.size()));
            this.container.inventorySlots = newSlots;
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.container.inventorySlots = oldSlots;
        } else {
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    /**
     * @return If the optimization should be done for only rendering the visible slots. Default: false
     */
    protected boolean isSubsetRenderSlots() {
        return false;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        if (isSearchEnabled()) this.searchField.drawScreen(mouseX, mouseY, partialTicks);
        this.scrollbar.drawWidget(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double mouseXPrev, double mouseYPrev) {
        if (this.getFocused() != null && this.isDragging()
            && mouseButton == 0
            && this.getFocused()
                .mouseDragged(mouseX, mouseY, mouseButton, mouseXPrev, mouseYPrev)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, mouseXPrev, mouseYPrev);
    }

    protected void updateSearch(String searchString) {
        getContainer().updateFilter(searchString);
        this.scrollbar.setTotalRows(getContainer().getFilteredItemCount() / getContainer().getColumns());
        this.scrollbar.scrollTo(0);
    }

    protected boolean needsScrollBars() {
        return getContainer().getFilteredItemCount() > getContainer().getPageSize();
    }

    public GuiTextField getSearchField() {
        return searchField;
    }

    public GuiScrollBar getScrollbar() {
        return scrollbar;
    }

    protected int getScrollX() {
        return 175;
    }

    protected int getScrollY() {
        return 18;
    }

    protected int getScrollHeight() {
        return 112;
    }

    protected boolean isSearchEnabled() {
        return true;
    }

    protected int getSearchX() {
        return 82;
    }

    protected int getSearchY() {
        return 6;
    }

    protected int getSearchWidth() {
        return SEARCH_WIDTH;
    }

    @Override
    protected final void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    protected final void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }
}
