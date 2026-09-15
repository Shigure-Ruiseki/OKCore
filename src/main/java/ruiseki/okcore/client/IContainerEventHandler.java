package ruiseki.okcore.client;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.IGuiEventListener;

@SideOnly(Side.CLIENT)
public interface IContainerEventHandler extends IGuiEventListener {

    public List<IGuiEventListener> getChildren();

    default Optional<IGuiEventListener> getChildAt(double mouseX, double mouseY) {
        for (IGuiEventListener guieventlistener : this.getChildren()) {
            if (guieventlistener.isMouseOver(mouseX, mouseY)) {
                return Optional.of(guieventlistener);
            }
        }

        return Optional.empty();
    }

    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IGuiEventListener guieventlistener : this.getChildren()) {
            if (guieventlistener.mouseClicked(mouseX, mouseY, button)) {
                this.setFocused(guieventlistener);
                if (button == 0) {
                    this.setDragging(true);
                }

                return true;
            }
        }

        return false;
    }

    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        return this.getChildAt(mouseX, mouseY)
            .filter((child) -> { return child.mouseReleased(mouseX, mouseY, button); })
            .isPresent();
    }

    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.getFocused() != null && this.isDragging() && button == 0 ? this.getFocused()
            .mouseDragged(mouseX, mouseY, button, dragX, dragY) : false;
    }

    boolean isDragging();

    void setDragging(boolean dragging);

    default boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.getChildAt(mouseX, mouseY)
            .filter((child) -> { return child.mouseScrolled(mouseX, mouseY, delta); })
            .isPresent();
    }

    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.getFocused() != null && this.getFocused()
            .keyPressed(keyCode, scanCode, modifiers);
    }

    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.getFocused() != null && this.getFocused()
            .keyReleased(keyCode, scanCode, modifiers);
    }

    default boolean charTyped(char codePoint, int modifiers) {
        return this.getFocused() != null && this.getFocused()
            .charTyped(codePoint, modifiers);
    }

    @Nullable
    IGuiEventListener getFocused();

    void setFocused(@Nullable IGuiEventListener focused);

    default void setFocused(boolean focused) {}

    default boolean isFocused() {
        return this.getFocused() != null;
    }
}
