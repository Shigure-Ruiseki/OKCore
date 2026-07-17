package ruiseki.okcore.client.key;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import org.lwjgl.input.Keyboard;

import ruiseki.okcore.helper.KeyBoardHelpers;
import ruiseki.okcore.helper.LangHelpers;

public enum KeyModifier {

    CONTROL {

        @Override
        public boolean matches(int keyCode) {
            if (Minecraft.isRunningOnMac) {
                return keyCode == Keyboard.KEY_LMETA || keyCode == Keyboard.KEY_RMETA;
            } else {
                return keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL;
            }
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return KeyBoardHelpers.isCtrlKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return LangHelpers.localize(KeyBoardHelpers.getCtrlLang(), keyName);
        }
    },
    SHIFT {

        @Override
        public boolean matches(int keyCode) {
            return keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return KeyBoardHelpers.isShiftKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return LangHelpers.localize("control_gui.shift", keyName);
        }
    },
    ALT {

        @Override
        public boolean matches(int keyCode) {
            return keyCode == Keyboard.KEY_LMENU || keyCode == Keyboard.KEY_RMENU;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return KeyBoardHelpers.isAltKeyDown();
        }

        @Override
        public String getLocalizedComboName(int keyCode) {
            String keyName = GameSettings.getKeyDisplayString(keyCode);
            return LangHelpers.localize("control_gui.alt", keyName);
        }
    },
    NONE {

        @Override
        public boolean matches(int keyCode) {
            return false;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            if (conflictContext != null && !conflictContext.conflicts(KeyConflictContext.IN_GAME)) {
                for (KeyModifier keyModifier : MODIFIER_VALUES) {
                    if (keyModifier.isActive(conflictContext)) {
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public String getLocalizedComboName(int keyCode) {
            return GameSettings.getKeyDisplayString(keyCode);
        }
    };

    public static final KeyModifier[] MODIFIER_VALUES = { SHIFT, CONTROL, ALT };

    public static KeyModifier getActiveModifier() {
        for (KeyModifier keyModifier : MODIFIER_VALUES) {
            if (keyModifier.isActive(null)) {
                return keyModifier;
            }
        }
        return NONE;
    }

    public static boolean isKeyCodeModifier(int keyCode) {
        for (KeyModifier keyModifier : MODIFIER_VALUES) {
            if (keyModifier.matches(keyCode)) {
                return true;
            }
        }
        return false;
    }

    public static KeyModifier valueFromString(String stringValue) {
        try {
            return valueOf(stringValue);
        } catch (NullPointerException | IllegalArgumentException ignored) {
            return NONE;
        }
    }

    public abstract boolean matches(int keyCode);

    public abstract boolean isActive(@Nullable IKeyConflictContext conflictContext);

    public abstract String getLocalizedComboName(int keyCode);
}
