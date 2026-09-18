package ruiseki.okcore.client.gui.component.list;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.IContainerEventHandler;
import ruiseki.okcore.client.gui.IGuiEventListener;

@SideOnly(Side.CLIENT)
public abstract class GuiObjectSelectionList<E extends GuiObjectSelectionList.Entry<E>> extends GuiSelectionList<E> {

    public GuiObjectSelectionList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        super(mc, width, height, top, bottom, itemHeight);
    }

    @SideOnly(Side.CLIENT)
    public abstract static class Entry<E extends GuiObjectSelectionList.Entry<E>> extends GuiSelectionList.Entry<E>
        implements IContainerEventHandler {

        @Nullable
        private IGuiEventListener focused;
        private boolean dragging;

        public boolean isDragging() {
            return this.dragging;
        }

        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return IContainerEventHandler.super.mouseClicked(mouseX, mouseY, button);
        }

        public void setFocused(@Nullable IGuiEventListener listener) {
            if (this.focused != null) {
                this.focused.setFocused(false);
            }

            if (listener != null) {
                listener.setFocused(true);
            }

            this.focused = listener;
        }

        @Nullable
        public IGuiEventListener getFocused() {
            return this.focused;
        }
    }
}
