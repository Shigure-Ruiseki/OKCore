package ruiseki.okcore.helper;

import net.minecraft.client.settings.KeyBinding;

import com.blamejared.controlling.api.ComboModifier;
import com.blamejared.controlling.api.ControllingApi;

import ruiseki.okcore.client.key.KeyModifier;

public class ControllingHelpers {

    public static boolean setDefaultComboKeyBinding(KeyBinding keyBinding, KeyModifier modifier) {
        ComboModifier comboMod = toComboModifier(modifier);
        return ControllingApi.setDefaultComboKeyBinding(keyBinding, comboMod);
    }

    public static boolean setComboKeyBinding(KeyBinding keyBinding, KeyModifier modifier, int keyCode) {
        return ControllingApi.setComboKeyBinding(keyBinding, toComboModifier(modifier), keyCode);
    }

    public static ComboModifier toComboModifier(KeyModifier modifier) {
        if (modifier == null) {
            return ComboModifier.NONE;
        }
        return switch (modifier) {
            case CONTROL -> ComboModifier.CONTROL;
            case SHIFT -> ComboModifier.SHIFT;
            case ALT -> ComboModifier.ALT;
            default -> ComboModifier.NONE;
        };
    }
}
