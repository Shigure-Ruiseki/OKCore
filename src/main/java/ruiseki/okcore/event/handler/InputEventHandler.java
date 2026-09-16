package ruiseki.okcore.event.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.client.IContainerEventHandler;
import ruiseki.okcore.event.input.IGuiInputHandle;
import ruiseki.okcore.event.input.KeyboardInputEvent;
import ruiseki.okcore.event.input.MouseInputEvent;
import ruiseki.okcore.item.IItemToggle;
import ruiseki.okcore.network.packet.PacketItemToggle;

public class InputEventHandler {

    public static final InputEventHandler INSTANCE = new InputEventHandler();

    public InputEventHandler() {}

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onMouseInput(MouseInputEvent.Pre event) {
        if (!(event.gui instanceof GuiContainer gui)) return;

        int button = Mouse.getEventButton();
        boolean isPressed = Mouse.getEventButtonState();

        if (button != -1 && isPressed) {
            Minecraft mc = Minecraft.getMinecraft();

            final int x = Mouse.getEventX() * gui.width / mc.displayWidth;
            final int y = gui.height - Mouse.getEventY() * gui.height / mc.displayHeight - 1;

            Slot slot = gui.getSlotAtPosition(x, y);

            if (slot != null && slot.getHasStack()) {
                ItemStack stack = slot.getStack();

                if (stack.getItem() instanceof IItemToggle toggle) {
                    if (toggle.canMouseClicked(stack, button) && toggle.isModifierKeyDown(stack)) {
                        int sendSlotNumber = slot.slotNumber;

                        if (gui instanceof GuiContainerCreative creativeGui) {
                            int selectedTabIndex = creativeGui.func_147056_g();

                            if (selectedTabIndex == 11) {
                                if (slot.inventory instanceof InventoryPlayer) {
                                    sendSlotNumber = slot.getSlotIndex();
                                } else {
                                    sendSlotNumber = -1;
                                }
                            } else {
                                if (slot.slotNumber >= 45 && slot.slotNumber <= 53) {
                                    sendSlotNumber = slot.slotNumber - 45 + 36;
                                } else {
                                    sendSlotNumber = -1;
                                }
                            }
                        }

                        if (sendSlotNumber != -1) {
                            OKCore._instance.getPacketHandler()
                                .sendToServer(new PacketItemToggle(sendSlotNumber, button));

                            if (gui instanceof IGuiInputHandle handle) {
                                handle.setMouseHandled(true);
                            }

                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseInputGuiScreen(MouseInputEvent.Process event) {
        if (event.gui instanceof IContainerEventHandler handler) {
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
                if (handler.mouseScrolled(x, y, delta)) {
                    handled = true;
                }
            }

            if (button != -1) {
                if (buttonState) {
                    // Click
                    if (handler.mouseClicked(x, y, button)) {
                        handled = true;
                    }
                } else {
                    // Released
                    if (handler.mouseReleased(x, y, button)) {
                        handled = true;
                    }
                }
            } else {
                // 3. Drag & Move
                if (handler.isDragging() && handler.getFocused() != null) {
                    double dragX = (double) Mouse.getEventDX() * gui.width / mc.displayWidth;
                    double dragY = (double) (-Mouse.getEventDY()) * gui.height / mc.displayHeight;

                    if (handler.mouseDragged(x, y, 0, dragX, dragY)) {
                        handled = true;
                    }
                } else {
                    handler.mouseMoved(x, y);
                }
            }

            if (handled) {
                event.setCanceled(true);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyboardInputGuiScreen(KeyboardInputEvent.Process event) {
        if (event.gui instanceof IContainerEventHandler handler) {
            boolean keyState = Keyboard.getEventKeyState();
            int keyCode = Keyboard.getEventKey();
            char eventChar = Keyboard.getEventCharacter();
            boolean isRepeat = Keyboard.isRepeatEvent();

            int modifiers = getModifiers();

            boolean handled = false;

            if (keyState || isRepeat) {
                if (keyCode != Keyboard.KEY_NONE) {
                    if (handler.keyPressed(keyCode, 0, modifiers)) {
                        handled = true;
                    }
                }

                if (isValidChar(eventChar)) {
                    if (handler.charTyped(eventChar, modifiers)) {
                        handled = true;
                    }
                }
            } else {
                if (keyCode != Keyboard.KEY_NONE) {
                    if (handler.keyReleased(keyCode, 0, modifiers)) {
                        handled = true;
                    }
                }
            }

            if (handled) {
                event.setCanceled(true);
            }
        }
    }

    private static int getModifiers() {
        int modifiers = 0;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            modifiers |= 1; // Shift mask
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
            modifiers |= 2; // Ctrl mask
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
            modifiers |= 4; // Alt mask
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA)) {
            modifiers |= 8; // Cmd/Windows Key mask
        }
        return modifiers;
    }

    private static boolean isValidChar(char c) {
        return c >= 32 && c != 127 || c == '\b' || c == '\r' || c == '\n' || c == '\t';
    }

}
