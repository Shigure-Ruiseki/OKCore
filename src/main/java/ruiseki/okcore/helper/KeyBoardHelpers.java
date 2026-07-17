package ruiseki.okcore.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class KeyBoardHelpers {

    /**
     * Returns true if either windows ctrl key is down or if either mac meta key is down
     */
    public static boolean isCtrlKeyDown() {
        return GuiScreen.isCtrlKeyDown();
    }

    public static String getCtrlLang() {
        return Minecraft.isRunningOnMac ? "control_gui.control.mac" : "control_gui.control";
    }

    /**
     * Returns true if either shift key is down
     */
    public static boolean isShiftKeyDown() {
        return GuiScreen.isShiftKeyDown();
    }

    /**
     * Returns true if either alt key is down
     */
    public static boolean isAltKeyDown() {
        return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
    }
}
