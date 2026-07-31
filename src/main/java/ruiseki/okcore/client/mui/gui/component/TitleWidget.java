package ruiseki.okcore.client.mui.gui.component;

import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widgets.TextWidget;

import ruiseki.okcore.client.OKCGuiTextures;

public class TitleWidget extends TextWidget<TitleWidget> {

    public TitleWidget(String text) {
        super(text);
        this.padding(5, 5, 3, 1);
        pos(4, -12);
        widthRel(0.8f);
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawBackground(context, widgetTheme);
        OKCGuiTextures.TITLE_TEXTURE.draw(0, 0, this.getArea().width, this.getArea().height);
    }
}
