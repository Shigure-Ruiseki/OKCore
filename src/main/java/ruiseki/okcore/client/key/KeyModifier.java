package ruiseki.okcore.client.key;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import org.lwjgl.input.Keyboard;

import ruiseki.okcore.helper.LangHelpers;

public enum KeyModifier {

    NONE(-1, -1) {

        @Override
        public String getLocalizedComboName(int keyCode) {
            return GameSettings.getKeyDisplayString(keyCode);
        }
    },
    CONTROL(Keyboard.KEY_LCONTROL, Keyboard.KEY_RCONTROL) {

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            String localization = Minecraft.isRunningOnMac ? "control_gui.control.mac" : "control_gui.control";
            return LangHelpers.localize(localization, keyName);
        }
    },
    SHIFT(Keyboard.KEY_LSHIFT, Keyboard.KEY_RSHIFT) {

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return LangHelpers.localize("control_gui.shift", keyName);
        }
    },
    ALT(Keyboard.KEY_LMENU, Keyboard.KEY_RMENU) {

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return LangHelpers.localize("control_gui.alt", keyName);
        }
    };

    public static final KeyModifier[] VALUES = values();

    private final int leftKeyCode;
    private final int rightKeyCode;

    KeyModifier(int leftKeyCode, int rightKeyCode) {
        this.leftKeyCode = leftKeyCode;
        this.rightKeyCode = rightKeyCode;
    }

    public boolean isActive() {
        return this != NONE && (Keyboard.isKeyDown(leftKeyCode) || Keyboard.isKeyDown(rightKeyCode));
    }

    public boolean matches(int keyCode) {
        return this != NONE && (keyCode == leftKeyCode || keyCode == rightKeyCode);
    }

    public static KeyModifier fromKeyCode(int keyCode) {
        return switch (keyCode) {
            case Keyboard.KEY_LCONTROL, Keyboard.KEY_RCONTROL -> CONTROL;
            case Keyboard.KEY_LSHIFT, Keyboard.KEY_RSHIFT -> SHIFT;
            case Keyboard.KEY_LMENU, Keyboard.KEY_RMENU -> ALT;
            default -> NONE;
        };
    }

    public static KeyModifier getActiveModifier() {
        if (CONTROL.isActive()) {
            return CONTROL;
        }
        if (SHIFT.isActive()) {
            return SHIFT;
        }
        if (ALT.isActive()) {
            return ALT;
        }
        return NONE;
    }

    public static boolean isAnyModifierActive() {
        return CONTROL.isActive() || SHIFT.isActive() || ALT.isActive();
    }

    public static boolean isKeyCodeModifier(int keyCode) {
        return fromKeyCode(keyCode) != NONE;
    }

    public abstract String getLocalizedComboName(int keyCode);
}
