package ruiseki.okcore.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class KeyBoardHelpers {

    public static boolean isCtrlKeyDown() {
        return GuiScreen.isCtrlKeyDown();
    }

    public static String getCtrlLang() {
        return Minecraft.isRunningOnMac ? "control_gui.control.mac" : "control_gui.control";
    }

    public static boolean isShiftKeyDown() {
        return GuiScreen.isShiftKeyDown();
    }

    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }

    public static boolean isMetaDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
    }

    public static boolean isCut(int keyCode) {
        return keyCode == Keyboard.KEY_X && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isPaste(int keyCode) {
        return keyCode == Keyboard.KEY_V && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isCopy(int keyCode) {
        return keyCode == Keyboard.KEY_C && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static boolean isSelectAll(int keyCode) {
        return keyCode == Keyboard.KEY_A && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
    }

    public static int getModifiers() {
        int modifiers = 0;
        if (isShiftKeyDown()) modifiers |= 1;
        if (isCtrlKeyDown()) modifiers |= 2;
        if (isAltKeyDown()) modifiers |= 4;
        if (isMetaDown()) modifiers |= 8;
        return modifiers;
    }

    public static boolean isValidChar(char c) {
        return c >= 32 && c != 127;
    }
}
