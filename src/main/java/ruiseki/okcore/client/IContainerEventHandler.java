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

    default boolean handleMouseInput(MouseInputEvent.Process event) {
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreen gui = event.gui;
        int button = Mouse.getEventButton();
        final int x = Mouse.getEventX() * gui.width / mc.displayWidth;
        final int y = gui.height - Mouse.getEventY() * gui.height / mc.displayHeight - 1;
        boolean buttonState = Mouse.getEventButtonState();
        boolean handled = false;

        // 1. Mouse Scroll
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0) {
            double delta = Math.signum(dWheel);
            if (mouseScrolled(x, y, delta)) {
                handled = true;
            }
        }

        if (button != -1) {
            if (buttonState) {
                // Click
                if (mouseClicked(x, y, button)) {
                    handled = true;
                }
            } else {
                // Released
                if (mouseReleased(x, y, button)) {
                    handled = true;
                }
            }
        } else {
            // 3. Drag & Move
            if (isDragging() && getFocused() != null) {
                double dragX = (double) Mouse.getEventDX() * gui.width / mc.displayWidth;
                double dragY = (double) (-Mouse.getEventDY()) * gui.height / mc.displayHeight;

                if (mouseDragged(x, y, 0, dragX, dragY)) {
                    handled = true;
                }
            } else {
                mouseMoved(x, y);
            }
        }

        return handled;
    }

    default boolean handleKeyboardInput(KeyboardInputEvent.Process event) {
        boolean keyState = Keyboard.getEventKeyState();
        int keyCode = Keyboard.getEventKey();
        char eventChar = Keyboard.getEventCharacter();
        boolean isRepeat = Keyboard.isRepeatEvent();

        int modifiers = KeyBoardHelpers.getModifiers();

        boolean handled = false;

        if (keyState || isRepeat) {
            if (keyCode != Keyboard.KEY_NONE) {
                if (keyPressed(keyCode, 0, modifiers)) {
                    handled = true;
                }
            }

            if (KeyBoardHelpers.isValidChar(eventChar)) {
                if (charTyped(eventChar, modifiers)) {
                    handled = true;
                }
            }
        } else {
            if (keyCode != Keyboard.KEY_NONE) {
                if (keyReleased(keyCode, 0, modifiers)) {
                    handled = true;
                }
            }
        }

        return handled;
    }
}
