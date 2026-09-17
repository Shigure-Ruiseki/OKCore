package ruiseki.okcore.client.gui.component;

import java.awt.Point;
import java.awt.Rectangle;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * A reusable scrollbar for screens.
 *
 * The using screen must add this as a child
 * and call the following method from its respective method:
 * * {@link #drawScreen(int, int, float)}
 * * {@link #mouseDragged(double, double, int, double, double)} (@see
 * {@link ruiseki.okcore.client.gui.container.GuiContainerScrolling} for an example)
 *
 * @author rubensworks
 */
public class GuiScrollBar extends Gui implements IGuiEventListener, IWidgetEventListener, IWidgetRenderable {

    private static final ResourceLocation SCROLLBUTTON = new ResourceLocation(
        "textures/gui/container/creative_inventory/tabs.png");
    private static final int SCROLL_BUTTON_HEIGHT = 15;
    private static final int SCROLL_BUTTON_WIDTH = 12;

    private int x;
    private int y;

    private final int width;
    private final int height;
    private final String narrationMessage;
    @Nullable
    private final IScrollCallback scrollCallback;
    @Nullable
    private Rectangle scollRegion;

    private int totalRows;
    private int visibleRows;
    private float currentScroll; // (0 = top, 1 = bottom)
    private boolean isScrolling; // if the scrollbar is being dragged
    private boolean wasClicking; // if the left mouse button was held down last time drawScreen was called

    public GuiScrollBar(int x, int y, int height, String narrationMessage, @Nullable IScrollCallback scrollCallback,
        int visibleRows) {
        this(x, y, height, narrationMessage, scrollCallback, visibleRows, null);
    }

    public GuiScrollBar(int x, int y, int height, String narrationMessage, @Nullable IScrollCallback scrollCallback,
        int visibleRows, Rectangle scollRegion) {
        this.x = x;
        this.y = y;
        this.width = SCROLL_BUTTON_WIDTH;
        this.height = height;
        this.narrationMessage = narrationMessage;
        this.scrollCallback = scrollCallback;
        this.scollRegion = scollRegion;

        this.currentScroll = 0;
        this.isScrolling = false;
        this.wasClicking = false;
        setVisibleRows(visibleRows);
    }

    public void setScollRegion(@Nullable Rectangle scollRegion) {
        this.scollRegion = scollRegion;
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        if (scollRegion != null) {
            if (RenderHelpers.isPointInRegion(scollRegion, new Point((int) x, (int) y))) {
                return true;
            }
        }
        return IGuiEventListener.super.isMouseOver(x, y);
    }

    /**
     * @return The current scroll position.
     */
    public float getCurrentScroll() {
        return currentScroll;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (scroll != 0 && this.needsScrollBars()) {
            scrollRelative(scroll);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double offsetX, double offsetY) {
        boolean flag = mouseButton == 0 || mouseButton == 1;
        int xMax = getX() + 14;
        int yMax = getY() + height;

        // Reset scroll if too big for current view
        if (!needsScrollBars()) {
            scrollTo(0);
            return true;
        }

        if (!this.wasClicking && flag && mouseX >= getX() && mouseY >= getY() && mouseX < xMax && mouseY < yMax) {
            this.isScrolling = this.needsScrollBars();
        }

        if (!flag) {
            this.isScrolling = false;
        }

        this.wasClicking = flag;

        if (this.isScrolling) {
            this.currentScroll = ((float) (mouseY - getY()) - 7.5F) / ((float) (yMax - getY()) - 15.0F);
            this.currentScroll = MathHelper.clamp_float(this.currentScroll, 0.0F, 1.0F);
            scrollTo(this.currentScroll);
            return true;
        }

        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawWidget(mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        int scrollX = x;
        int scrollMinY = y;
        int scrollMaxY = scrollMinY + height;
        RenderHelpers.bindTexture(SCROLLBUTTON);
        this.drawTexturedModalRect(
            scrollX,
            scrollMinY + (int) ((float) (scrollMaxY - scrollMinY - SCROLL_BUTTON_HEIGHT - 2) * this.currentScroll),
            232 + (this.needsScrollBars() ? 0 : SCROLL_BUTTON_WIDTH),
            0,
            SCROLL_BUTTON_WIDTH,
            SCROLL_BUTTON_HEIGHT);
    }

    protected boolean needsScrollBars() {
        return getTotalRows() > getVisibleRows();
    }

    protected int getScrollStep() {
        return getTotalRows() - getVisibleRows() + 1;
    }

    public void scrollRelative(double step) {
        float scroll = (float) (this.currentScroll - step / getScrollStep());
        scroll = MathHelper.clamp_float(scroll, 0.0F, 1.0F);
        scrollTo(scroll);
    }

    public void scrollTo(float scroll) {
        scrollTo(scroll, true);
    }

    public void scrollTo(float scroll, boolean invokeCallback) {
        this.currentScroll = Math.max(0, scroll);
        if (invokeCallback && scrollCallback != null) {
            int firstRow = (int) ((double) (scroll * getScrollStep()) + 0.5D);
            scrollCallback.onScroll(firstRow);
        }
    }

    public void setFirstRow(int firstRow, boolean invokeCallback) {
        float scroll = ((float) firstRow) / getScrollStep();
        scroll = MathHelper.clamp_float(scroll, 0.0F, 1.0F);
        scrollTo(scroll, invokeCallback);
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getVisibleRows() {
        return visibleRows;
    }

    public void setVisibleRows(int visibleRows) {
        this.visibleRows = visibleRows;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    public static interface IScrollCallback {

        public void onScroll(int firstRow);

    }
}
