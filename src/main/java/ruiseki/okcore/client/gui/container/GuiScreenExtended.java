package ruiseki.okcore.client.gui.container;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import ruiseki.okcore.client.IContainerEventHandler;
import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.client.gui.component.IWidgetRenderable;

public class GuiScreenExtended extends GuiScreen implements IContainerEventHandler {

    @Nullable
    private IGuiEventListener focused;
    private boolean isDragging;

    private final List<IGuiEventListener> children = Lists.newArrayList();
    public final List<IWidgetRenderable> renderables = Lists.newArrayList();

    protected <T extends IGuiEventListener & IWidgetRenderable> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        return this.addWidget(widget);
    }

    protected <T extends IWidgetRenderable> T addRenderableOnly(T widget) {
        this.renderables.add(widget);
        return widget;
    }

    protected <T extends IGuiEventListener> T addWidget(T widget) {
        this.children.add(widget);
        return widget;
    }

    protected void removeWidget(IGuiEventListener widget) {
        if (widget instanceof IWidgetRenderable) {
            this.renderables.remove(widget);
        }

        this.children.remove(widget);
    }

    protected void clearWidgets() {
        this.renderables.clear();
        this.children.clear();
    }

    @Override
    public List<IGuiEventListener> getChildren() {
        return children;
    }

    @Override
    public boolean isDragging() {
        return isDragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.isDragging = dragging;
    }

    @Nullable
    public IGuiEventListener getFocused() {
        return this.focused;
    }

    public void setFocused(@Nullable IGuiEventListener focused) {
        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (focused != null) {
            focused.setFocused(true);
        }

        this.focused = focused;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        for (IWidgetRenderable renderable : this.renderables) {
            renderable.drawScreen(mouseX, mouseY, partialTicks);
        }

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
            return true;
        }
        return IContainerEventHandler.super.keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * @deprecated {@link #mouseClicked(double, double, int)}.
     */
    @Override
    @Deprecated
    protected final void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    /**
     * @deprecated {@link #mouseDragged(double, double, int, double, double)}.
     */
    @Override
    @Deprecated
    protected final void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {

    }

    /**
     * @deprecated {@link #mouseMoved(double, double)} and {@link #mouseReleased(double, double, int)}.
     */
    @Override
    @Deprecated
    protected final void mouseMovedOrUp(int mouseX, int mouseY, int state) {

    }

    /**
     * Legacy key typed entry point from Vanilla 1.7.10.
     *
     * @deprecated {@link #charTyped(char, int)}.
     */
    @Override
    @Deprecated
    protected final void keyTyped(char typedChar, int keyCode) {

    }
}
