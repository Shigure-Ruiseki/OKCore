
package ruiseki.okcore.client;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.event.input.KeyboardInputEvent;
import ruiseki.okcore.event.input.MouseInputEvent;
import ruiseki.okcore.helper.KeyBoardHelpers;

/**
 * Defines a GUI event container that manages child listeners, focus,
 * dragging state, and input event dispatching.
 *
 * <p>
 * Implementations provide a collection of {@link IGuiEventListener}
 * children and manage the currently focused listener. Mouse and
 * keyboard events are delegated to the appropriate child according
 * to the event type.
 * </p>
 *
 * <p>
 * Mouse clicks are dispatched to children in iteration order until
 * one consumes the event. The consuming child becomes focused, and
 * a handled left-click also enables dragging.
 * </p>
 *
 * <p>
 * Mouse scrolling and release events are dispatched to the first
 * child under the cursor. Keyboard events and active drag events
 * are forwarded to the focused child.
 * </p>
 *
 * <p>
 * This interface also provides adapters for translating LWJGL mouse
 * and keyboard input into the higher-level methods defined by
 * {@link IGuiEventListener}.
 * </p>
 *
 * <p>
 * This interface is intended for client-side use only.
 * </p>
 *
 * @see IGuiEventListener
 * @see MouseInputEvent
 * @see KeyboardInputEvent
 */
@SideOnly(Side.CLIENT)
public interface IContainerEventHandler extends IGuiEventListener {

    /**
     * Returns the child GUI event listeners managed by this container.
     *
     * <p>
     * Child iteration order determines which listener receives a mouse
     * click first. The first listener that consumes a click prevents
     * subsequent listeners from receiving it.
     * </p>
     *
     * @return the child GUI event listeners
     */
    List<IGuiEventListener> getChildren();

    /**
     * Finds the first child listener under the specified mouse position.
     *
     * <p>
     * Children are checked in iteration order using
     * {@link IGuiEventListener#isMouseOver(double, double)}.
     * </p>
     *
     * @param mouseX the mouse X coordinate in GUI space
     * @param mouseY the mouse Y coordinate in GUI space
     * @return an {@link Optional} containing the first matching child,
     *         or an empty optional if no child is under the cursor
     */
    default Optional<IGuiEventListener> getChildAt(double mouseX, double mouseY) {
        for (IGuiEventListener guieventlistener : this.getChildren()) {
            if (guieventlistener.isMouseOver(mouseX, mouseY)) {
                return Optional.of(guieventlistener);
            }
        }

        return Optional.empty();
    }

    /**
     * Dispatches a mouse click to child listeners.
     *
     * <p>
     * Children receive the click in iteration order until one returns
     * {@code true}. The first child that handles the click becomes
     * the focused listener.
     * </p>
     *
     * <p>
     * When a left mouse click is handled, dragging is enabled through
     * {@link #setDragging(boolean)}.
     * </p>
     *
     * @param mouseX the mouse X coordinate in GUI space
     * @param mouseY the mouse Y coordinate in GUI space
     * @param button the mouse button identifier
     * @return {@code true} if a child handled the click,
     *         otherwise {@code false}
     */
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

    /**
     * Dispatches a mouse release to the first child under the cursor.
     *
     * <p>
     * Dragging is disabled before the release event is dispatched.
     * The release is not automatically forwarded to the currently
     * focused child.
     * </p>
     *
     * @param mouseX the mouse X coordinate in GUI space
     * @param mouseY the mouse Y coordinate in GUI space
     * @param button the released mouse button
     * @return {@code true} if the child under the cursor handled
     *         the release, otherwise {@code false}
     */
    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        return this.getChildAt(mouseX, mouseY)
            .filter((child) -> child.mouseReleased(mouseX, mouseY, button))
            .isPresent();
    }

    /**
     * Dispatches a mouse drag event to the focused child.
     *
     * <p>
     * Drag events are forwarded only when a child is focused,
     * dragging is enabled, and the supplied button is the
     * left mouse button ({@code 0}).
     * </p>
     *
     * @param mouseX the current mouse X coordinate
     * @param mouseY the current mouse Y coordinate
     * @param button the mouse button identifier
     * @param dragX  the horizontal mouse movement since the previous event
     * @param dragY  the vertical mouse movement since the previous event
     * @return {@code true} if the focused child handles the drag,
     *         otherwise {@code false}
     */
    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.getFocused() != null && this.isDragging() && button == 0 ? this.getFocused()
            .mouseDragged(mouseX, mouseY, button, dragX, dragY) : false;
    }

    /**
     * Returns whether this container is currently tracking a drag.
     *
     * @return {@code true} if dragging is enabled
     */
    boolean isDragging();

    /**
     * Updates the dragging state of this container.
     *
     * @param dragging {@code true} to enable dragging,
     *                 or {@code false} to disable it
     */
    void setDragging(boolean dragging);

    /**
     * Dispatches a mouse scroll event to the first child under the cursor.
     *
     * @param mouseX the mouse X coordinate in GUI space
     * @param mouseY the mouse Y coordinate in GUI space
     * @param delta  the scroll amount
     * @return {@code true} if the child handles the scroll,
     *         otherwise {@code false}
     */
    @Override
    default boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return this.getChildAt(mouseX, mouseY)
            .filter((child) -> child.mouseScrolled(mouseX, mouseY, delta))
            .isPresent();
    }

    /**
     * Forwards a key press to the currently focused child.
     *
     * @param keyCode   the key code
     * @param scanCode  the scan code
     * @param modifiers the active keyboard modifier flags
     * @return {@code true} if the focused child handles the key press
     */
    @Override
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.getFocused() != null && this.getFocused()
            .keyPressed(keyCode, scanCode, modifiers);
    }

    /**
     * Forwards a key release to the currently focused child.
     *
     * @param keyCode   the released key code
     * @param scanCode  the scan code
     * @param modifiers the active keyboard modifier flags
     * @return {@code true} if the focused child handles the key release
     */
    @Override
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.getFocused() != null && this.getFocused()
            .keyReleased(keyCode, scanCode, modifiers);
    }

    /**
     * Forwards a typed character to the currently focused child.
     *
     * @param codePoint the typed character
     * @param modifiers the active keyboard modifier flags
     * @return {@code true} if the focused child handles the character
     */
    @Override
    default boolean charTyped(char codePoint, int modifiers) {
        return this.getFocused() != null && this.getFocused()
            .charTyped(codePoint, modifiers);
    }

    /**
     * Returns the currently focused child listener.
     *
     * @return the focused listener, or {@code null} if none is focused
     */
    @Nullable
    IGuiEventListener getFocused();

    /**
     * Sets the currently focused child listener.
     *
     * @param focused the listener to focus, or {@code null}
     *                to clear focus
     */
    void setFocused(@Nullable IGuiEventListener focused);

    /**
     * Updates the boolean focus state.
     *
     * <p>
     * The default implementation does nothing. Implementations may
     * override this method if they maintain a separate focus state.
     * </p>
     *
     * @param focused the requested focus state
     */
    default void setFocused(boolean focused) {}

    /**
     * Determines whether this container currently has a focused child.
     *
     * @return {@code true} if {@link #getFocused()} is non-null
     */
    default boolean isFocused() {
        return this.getFocused() != null;
    }

    /**
     * Processes a raw LWJGL mouse event and dispatches it to GUI listeners.
     *
     * <p>
     * Mouse coordinates are converted from display coordinates into
     * scaled GUI coordinates using the current {@link GuiScreen}
     * dimensions.
     * </p>
     *
     * <p>
     * The method handles mouse wheel movement, button presses,
     * button releases, dragging, and cursor movement.
     * Scroll wheel values are normalized to {@code -1} or {@code 1}.
     * </p>
     *
     * <p>
     * Cursor movement without a pressed mouse button invokes
     * {@link #mouseMoved(double, double)} and reports whether a child
     * is under the cursor.
     * </p>
     *
     * @param event the mouse input event containing the active GUI
     * @return {@code true} if the input is handled or mouse movement
     *         occurs over a child listener, otherwise {@code false}
     */
    default boolean handleMouseInput(MouseInputEvent.Process event) {
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = event.gui;

        final int x = Mouse.getEventX() * gui.width / mc.displayWidth;
        final int y = gui.height - Mouse.getEventY() * gui.height / mc.displayHeight - 1;

        int button = Mouse.getEventButton();
        boolean buttonState = Mouse.getEventButtonState();

        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            double delta = Math.signum(dWheel);
            if (this.mouseScrolled(x, y, delta)) {
                return true;
            }
        }

        if (button != -1) {
            if (buttonState) {
                return this.mouseClicked(x, y, button);
            } else {
                return this.mouseReleased(x, y, button);
            }
        }

        if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
            double dragX = (double) Mouse.getEventDX() * gui.width / mc.displayWidth;
            double dragY = (double) (-Mouse.getEventDY()) * gui.height / mc.displayHeight;

            if (this.isDragging() && this.getFocused() != null) {
                return this.mouseDragged(x, y, 0, dragX, dragY);
            } else if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1) || Mouse.isButtonDown(2)) {
                int activeBtn = Mouse.isButtonDown(0) ? 0 : (Mouse.isButtonDown(1) ? 1 : 2);
                return this.mouseDragged(x, y, activeBtn, dragX, dragY);
            } else {
                this.mouseMoved(x, y);
                return this.getChildAt(x, y)
                    .isPresent();
            }
        }

        return false;
    }

    /**
     * Processes a raw LWJGL keyboard event and dispatches it to
     * the currently focused GUI listener.
     *
     * <p>
     * Key press and repeat events first attempt character input
     * through {@link #charTyped(char, int)}. If the character is not
     * handled, the event is forwarded through
     * {@link #keyPressed(int, int, int)} when a valid key code exists.
     * </p>
     *
     * <p>
     * Key release events are forwarded through
     * {@link #keyReleased(int, int, int)}.
     * </p>
     *
     * <p>
     * Keyboard modifiers are retrieved from
     * {@link KeyBoardHelpers#getModifiers()}.
     * </p>
     *
     * @param event the keyboard input event being processed
     * @return {@code true} if a focused listener handles the input,
     *         otherwise {@code false}
     */
    default boolean handleKeyboardInput(KeyboardInputEvent.Process event) {
        boolean keyState = Keyboard.getEventKeyState();
        int keyCode = Keyboard.getEventKey();
        char eventChar = Keyboard.getEventCharacter();
        boolean isRepeat = Keyboard.isRepeatEvent();
        int modifiers = KeyBoardHelpers.getModifiers();

        boolean handled = false;

        if (keyState || isRepeat) {
            if (this.charTyped(eventChar, modifiers)) {
                handled = true;
            }

            if (!handled && keyCode != Keyboard.KEY_NONE) {
                if (this.keyPressed(keyCode, keyCode, modifiers)) {
                    handled = true;
                }
            }
        } else {
            if (keyCode != Keyboard.KEY_NONE) {
                if (this.keyReleased(keyCode, keyCode, modifiers)) {
                    handled = true;
                }
            }
        }

        return handled;
    }
}
