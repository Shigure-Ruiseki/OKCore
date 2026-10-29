
package ruiseki.okcore.client.key;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import org.lwjgl.input.Keyboard;

import ruiseki.okcore.helper.KeyBoardHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Represents the modifier keys supported by the OKCore key binding system.
 *
 * <p>
 * Modifiers are used to distinguish key combinations involving Control,
 * Shift, and Alt. The {@link #NONE} value represents a key binding
 * without an explicit modifier.
 * </p>
 *
 * <p>
 * Control key matching is platform-dependent. On macOS, the
 * {@link #CONTROL} modifier matches the Command (Meta) keys instead
 * of the physical Control keys.
 * </p>
 *
 * <p>
 * Modifier activation may depend on an optional
 * {@link IKeyConflictContext}. The context is particularly relevant
 * when evaluating {@link #NONE}.
 * </p>
 *
 * @see IKeyConflictContext
 * @see KeyConflictContext
 * @see KeyBoardHelpers
 */
public enum KeyModifier {

    /**
     * Represents the Control modifier.
     *
     * <p>
     * Matches the left and right Control keys on most platforms.
     * On macOS, matches the left and right Command (Meta) keys.
     * </p>
     */
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

    /**
     * Represents the Shift modifier.
     *
     * <p>
     * Matches either the left or right Shift key.
     * </p>
     */
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

    /**
     * Represents the Alt modifier.
     *
     * <p>
     * Matches either the left or right Alt key.
     * </p>
     */
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

    /**
     * Represents the absence of an explicit key modifier.
     *
     * <p>
     * Unlike other modifiers, this value does not match any physical
     * modifier key.
     * </p>
     *
     * <p>
     * When the conflict context does not conflict with
     * {@link KeyConflictContext#IN_GAME}, this modifier is active
     * only when none of the supported modifiers are active.
     * Otherwise, it is always considered active.
     * </p>
     */
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

    /**
     * The supported physical modifiers in priority order.
     *
     * <p>
     * Shift takes precedence over Control, followed by Alt when
     * determining the active modifier.
     * </p>
     *
     * <p>
     * The {@link #NONE} value is intentionally excluded.
     * </p>
     */
    public static final KeyModifier[] MODIFIER_VALUES = { SHIFT, CONTROL, ALT };

    /**
     * Returns the highest-priority currently active modifier.
     *
     * <p>
     * Modifiers are evaluated in the order defined by
     * {@link #MODIFIER_VALUES}. If multiple modifiers are pressed,
     * the first matching modifier is returned.
     * </p>
     *
     * @return the active modifier, or {@link #NONE} if no supported
     *         modifier is active
     */
    public static KeyModifier getActiveModifier() {
        for (KeyModifier keyModifier : MODIFIER_VALUES) {
            if (keyModifier.isActive(null)) {
                return keyModifier;
            }
        }
        return NONE;
    }

    /**
     * Determines whether the specified key code represents a
     * supported modifier key.
     *
     * <p>
     * The key code is compared against each modifier in
     * {@link #MODIFIER_VALUES}.
     * </p>
     *
     * @param keyCode the LWJGL keyboard key code to check
     * @return {@code true} if the key code matches Control,
     *         Shift, or Alt; otherwise {@code false}
     */
    public static boolean isKeyCodeModifier(int keyCode) {
        for (KeyModifier keyModifier : MODIFIER_VALUES) {
            if (keyModifier.matches(keyCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves a modifier from its enum constant name.
     *
     * <p>
     * The lookup is case-sensitive. Invalid or {@code null} input
     * returns {@link #NONE} instead of throwing an exception.
     * </p>
     *
     * @param stringValue the modifier name to resolve
     * @return the matching modifier, or {@link #NONE} if the
     *         supplied name is invalid or {@code null}
     */
    public static KeyModifier valueFromString(String stringValue) {
        try {
            return valueOf(stringValue);
        } catch (NullPointerException | IllegalArgumentException ignored) {
            return NONE;
        }
    }

    /**
     * Determines whether this modifier matches the specified key code.
     *
     * <p>
     * This method identifies the physical key associated with the
     * modifier. It does not check whether the key is currently pressed.
     * </p>
     *
     * @param keyCode the LWJGL keyboard key code
     * @return {@code true} if the key code belongs to this modifier
     */
    public abstract boolean matches(int keyCode);

    /**
     * Determines whether this modifier is currently active.
     *
     * <p>
     * Implementations may use the supplied conflict context to
     * determine how modifier activation should be interpreted.
     * A {@code null} context is permitted.
     * </p>
     *
     * @param conflictContext the optional key conflict context
     * @return {@code true} if this modifier is considered active
     */
    public abstract boolean isActive(@Nullable IKeyConflictContext conflictContext);

    /**
     * Returns a localized display name for a key combination
     * involving this modifier.
     *
     * <p>
     * For physical modifiers, the result combines the localized
     * modifier name with the display name of the supplied key.
     * For {@link #NONE}, only the key display name is returned.
     * </p>
     *
     * @param keyCode the key code associated with the combination
     * @return the localized key combination display name
     */
    public abstract String getLocalizedComboName(int keyCode);
}
