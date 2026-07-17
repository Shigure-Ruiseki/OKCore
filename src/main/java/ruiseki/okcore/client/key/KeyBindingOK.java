package ruiseki.okcore.client.key;

import net.minecraft.client.settings.KeyBinding;

import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.helper.ControllingHelpers;

public class KeyBindingOK extends KeyBinding {

    private KeyModifier keyModifierDefault = KeyModifier.NONE;
    private KeyModifier keyModifier = KeyModifier.NONE;

    public KeyBindingOK(String description, KeyModifier keyModifier, int keyCode, String category) {
        super(description, keyCode, category);
        this.keyModifier = keyModifier;
        this.keyModifierDefault = keyModifier;
        if (Mods.Controlling.isModLoaded()) {
            ControllingHelpers.setDefaultComboKeyBinding(this, keyModifier);
        }
    }

    public KeyModifier getKeyModifier() {
        return keyModifier;
    }

    public KeyModifier getKeyModifierDefault() {
        return keyModifierDefault;
    }

    public void setKeyModifierAndCode(KeyModifier keyModifier, int keyCode) {
        this.setKeyCode(keyCode);
        if (keyModifier.matches(keyCode)) keyModifier = KeyModifier.NONE;
        this.keyModifier = keyModifier;
        KeyBinding.resetKeyBindingArrayAndHash();
        if (Mods.Controlling.isModLoaded()) {
            ControllingHelpers.setComboKeyBinding(this, keyModifier, keyCode);
        }
    }

    public void setToDefault() {
        setKeyModifierAndCode(getKeyModifierDefault(), getKeyCodeDefault());
    }

    public boolean isSetToDefaultValue() {
        return getKeyCode() == getKeyCodeDefault() && getKeyModifier() == getKeyModifierDefault();
    }

    public boolean isActiveAndMatches(int keyCode) {
        return keyCode != 0 && keyCode == this.getKeyCode() && getKeyModifier().isActive();
    }

    public String getDisplayName() {
        return getKeyModifier().getLocalizedComboName(getKeyCode());
    }
}
