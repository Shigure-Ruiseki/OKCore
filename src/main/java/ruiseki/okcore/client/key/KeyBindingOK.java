package ruiseki.okcore.client.key;

import net.minecraft.client.settings.KeyBinding;

import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.helper.ControllingHelpers;

public class KeyBindingOK extends KeyBinding {

    private IKeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;

    public KeyBindingOK(String description, KeyModifier keyModifier, int keyCode, String category) {
        this(description, KeyConflictContext.UNIVERSAL, keyModifier, keyCode, category);
    }

    public KeyBindingOK(String description, IKeyConflictContext keyConflictContext, int keyCode, String category) {
        this(description, keyConflictContext, KeyModifier.NONE, keyCode, category);
    }

    public KeyBindingOK(String description, IKeyConflictContext keyConflictContext, KeyModifier keyModifier,
        int keyCode, String category) {
        super(description, keyCode, category);
        this.keyConflictContext = keyConflictContext;
        if (Mods.Controlling.isModLoaded()) {
            ControllingHelpers.setDefaultComboKeyBinding(this, keyModifier);
        }
    }

    public KeyModifier getKeyModifier() {
        if (!Mods.Controlling.isModLoaded()) return KeyModifier.NONE;
        return ControllingHelpers.getKeyModifier(this);
    }

    public KeyModifier getKeyModifierDefault() {
        if (!Mods.Controlling.isModLoaded()) return KeyModifier.NONE;
        return ControllingHelpers.getKeyModifierDefault(this);
    }

    public void setKeyModifierAndCode(KeyModifier keyModifier, int keyCode) {
        this.setKeyCode(keyCode);
        KeyBinding.resetKeyBindingArrayAndHash();
        if (Mods.Controlling.isModLoaded()) {
            ControllingHelpers.setComboKeyBinding(this, keyModifier, keyCode);
        }
    }

    public void setKeyConflictContext(IKeyConflictContext keyConflictContext) {
        this.keyConflictContext = keyConflictContext;
    }

    public IKeyConflictContext getKeyConflictContext() {
        return keyConflictContext;
    }

    public void setToDefault() {
        setKeyModifierAndCode(getKeyModifierDefault(), getKeyCodeDefault());
    }

    public boolean isSetToDefaultValue() {
        return getKeyCode() == getKeyCodeDefault() && getKeyModifier() == getKeyModifierDefault();
    }

    public boolean isActiveAndMatches(int keyCode) {
        return keyCode != 0 && keyCode == this.getKeyCode()
            && getKeyConflictContext().isActive()
            && getKeyModifier().isActive(getKeyConflictContext());
    }

    public String getDisplayName() {
        return getKeyModifier().getLocalizedComboName(getKeyCode());
    }
}
