package ruiseki.okcore.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Listener interface for handling GUI input events such as mouse movements,
 * clicks, scrolling, and keyboard actions.
 */
@SideOnly(Side.CLIENT)
public interface IGuiEventListener {

    /**
     * Time threshold in milliseconds to distinguish a double-click event from two single clicks.
     */
    long DOUBLE_CLICK_THRESHOLD_MS = 250L;

    /**
     * Called when the mouse cursor is moved within the GUI boundaries.
     *
     * @param mouseX the current X coordinate of the mouse pointer
     * @param mouseY the current Y coordinate of the mouse pointer
     */
    default void mouseMoved(double mouseX, double mouseY) {}

    /**
     * Called when a mouse button is pressed down.
     *
     * @param mouseX the X coordinate where the mouse was clicked
     * @param mouseY the Y coordinate where the mouse was clicked
     * @param button the mouse button index (0: Left, 1: Right, 2: Middle)
     * @return {@code true} if the event was handled and consumed, {@code false} otherwise
     */
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Called when a mouse button is released.
     *
     * @param mouseX the X coordinate where the mouse was released
     * @param mouseY the Y coordinate where the mouse was released
     * @param button the mouse button index (0: Left, 1: Right, 2: Middle)
     * @return {@code true} if the event was handled and consumed, {@code false} otherwise
     */
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    /**
     * Called while a mouse button is held down and the mouse is moved.
     *
     * @param mouseX the current X coordinate of the mouse pointer
     * @param mouseY the current Y coordinate of the mouse pointer
     * @param button the mouse button index being held down
     * @param dragX  the relative movement distance along the X-axis since the last update
     * @param dragY  the relative movement distance along the Y-axis since the last update
     * @return {@code true} if the event was handled and consumed, {@code false} otherwise
     */
    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return false;
    }

    /**
     * Called when the mouse scroll wheel is moved.
     *
     * @param mouseX the X coordinate of the mouse pointer during scroll
     * @param mouseY the Y coordinate of the mouse pointer during scroll
     * @param delta  the scroll direction and magnitude (positive for scrolling up, negative for scrolling down)
     * @return {@code true} if the event was handled and consumed, {@code false} otherwise
     */
    default boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }

    /**
     * Called when a keyboard key is pressed.
     *
     * @param keyCode   the LWJGL/GLFW key code of the pressed key
     * @param scanCode  the physical scancode of the key
     * @param modifiers bitmask indicating active modifier keys (e.g., Shift, Ctrl, Alt)
     * @return {@code true} if the event was handled and consumed, {@code false} otherwise
     */
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * Called when a keyboard key is released.
     *
     * @param keyCode   the LWJGL/GLFW key code of the released key
     * @param scanCode  the physical scancode of the key
     * @param modifiers bitmask indicating active modifier keys (e.g., Shift, Ctrl, Alt)
     * @return {@code true} if the event was handled and consumed, {@code false} otherwise
     */
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    /**
     * Called when a character input event is emitted (useful for text inputs).
     *
     * @param codePoint the character typed
     * @param modifiers bitmask indicating active modifier keys
     * @return {@code true} if the event was handled and consumed, {@code false} otherwise
     */
    default boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    /**
     * Checks whether the mouse cursor is currently positioned over this element.
     *
     * @param mouseX the X coordinate of the mouse pointer
     * @param mouseY the Y coordinate of the mouse pointer
     * @return {@code true} if the mouse is hovering over this element, {@code false} otherwise
     */
    default boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    /**
     * Sets the focus state of this component.
     *
     * @param focused {@code true} to grant focus to this component, {@code false} to remove focus
     */
    void setFocused(boolean focused);

    /**
     * Checks if this component currently holds input focus.
     *
     * @return {@code true} if focused, {@code false} otherwise
     */
    boolean isFocused();
}
