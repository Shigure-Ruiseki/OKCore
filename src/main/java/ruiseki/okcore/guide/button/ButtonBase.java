package ruiseki.okcore.guide.button;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiButton;

import ruiseki.okcore.guide.gui.GuiBase;

public class ButtonBase extends GuiButton {

    public GuiBase guiBase;
    public Consumer<ButtonBase> onPressConsumer;

    public ButtonBase(int id, int x, int y, GuiBase guiBase, Consumer<ButtonBase> consumer) {
        super(id, x, y, "");
        this.guiBase = guiBase;
        this.onPressConsumer = consumer;
    }

    public void onPress() {
        if (this.onPressConsumer != null) {
            this.onPressConsumer.accept(this);
        }
    }
}
