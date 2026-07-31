package ruiseki.okcore.client.mui.gui.component.button;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.value.sync.EnumSyncValue;

import ruiseki.okcore.client.OKCGuiTextures;
import ruiseki.okcore.enums.RedstoneMode;

public class RedstoneModeButton extends CyclicVariantButton {

    private static final List<Variant> VARIANTS = Arrays.asList(
        new Variant(IKey.lang("gui.button.redstone_mode.always_on"), OKCGuiTextures.ALWAYS_ON),
        new Variant(IKey.lang("gui.button.redstone_mode.high_on"), OKCGuiTextures.HIGH_ON),
        new Variant(IKey.lang("gui.button.redstone_mode.high_off"), OKCGuiTextures.HIGH_OFF),
        new Variant(IKey.lang("gui.button.redstone_mode.always_off"), OKCGuiTextures.ALWAYS_OFF));

    public RedstoneModeButton(EnumSyncValue<RedstoneMode, ?> syncValue) {
        super(
            VARIANTS,
            syncValue.getValue()
                .getIndex(),
            1,
            16,
            value -> syncValue.setValue(RedstoneMode.values()[value]));
        this.size(18);
    }
}
