package ruiseki.okcore.client.mui.gui.component;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;

public final class TooltipSpacerDrawable implements IDrawable {

    private final int width;
    private final int height;

    public TooltipSpacerDrawable(int width, int height) {
        this.width = Math.max(0, width);
        this.height = Math.max(0, height);
    }

    @Override
    public int getDefaultWidth() {
        return width;
    }

    @Override
    public int getDefaultHeight() {
        return height;
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        // Intentionally empty: reserves layout space.
    }
}
