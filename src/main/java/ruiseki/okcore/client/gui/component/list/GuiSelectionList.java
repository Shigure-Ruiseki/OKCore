package ruiseki.okcore.client.gui.component.list;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.client.gui.container.GuiScreenExtended;

@SideOnly(Side.CLIENT)
public abstract class GuiSelectionList<E extends GuiSelectionList.Entry<E>> extends GuiScreenExtended {

    protected static final ResourceLocation OPTIONS_BACKGROUND = new ResourceLocation(
        "textures/gui/options_background.png");

    protected final Minecraft minecraft;
    protected final int itemHeight;
    private final List<E> children = new TrackedList();
    protected int width;
    protected int height;
    protected int y0;
    protected int y1;
    protected int x1;
    protected int x0;
    protected boolean centerListVertically = true;
    private double scrollAmount;
    private boolean renderSelection = true;
    private boolean renderHeader;
    protected int headerHeight;
    private boolean scrolling;
    @Nullable
    private E selected;
    private boolean renderBackground = true;
    private boolean renderTopAndBottom = true;
    @Nullable
    private E hovered;

    public GuiSelectionList(Minecraft mc, int width, int height, int top, int bottom, int itemHeight) {
        this.minecraft = mc;
        this.width = width;
        this.height = height;
        this.y0 = top;
        this.y1 = bottom;
        this.itemHeight = itemHeight;
        this.x0 = 0;
        this.x1 = width;
    }

    public void setRenderSelection(boolean renderSelection) {
        this.renderSelection = renderSelection;
    }

    protected void setRenderHeader(boolean renderHeader, int headerHeight) {
        this.renderHeader = renderHeader;
        this.headerHeight = renderHeader ? headerHeight : 0;
    }

    public int getRowWidth() {
        return 220;
    }

    @Nullable
    public E getSelected() {
        return this.selected;
    }

    public void setSelected(@Nullable E selected) {
        this.selected = selected;
    }

    public E getFirstElement() {
        return this.children.isEmpty() ? null : this.children.get(0);
    }

    public void setRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
    }

    public void setRenderTopAndBottom(boolean renderTopAndBottom) {
        this.renderTopAndBottom = renderTopAndBottom;
    }

    public final List<E> children() {
        return this.children;
    }

    protected void clearEntries() {
        this.children.clear();
        this.selected = null;
    }

    protected void replaceEntries(Collection<E> children) {
        this.clearEntries();
        this.children.addAll(children);
    }

    protected E getEntry(int index) {
        return this.children()
            .get(index);
    }

    protected int addEntry(E entry) {
        this.children.add(entry);
        return this.children.size() - 1;
    }

    protected void addEntryToTop(E entry) {
        double d0 = (double) this.getMaxScroll() - this.getScrollAmount();
        this.children.add(0, entry);
        this.setScrollAmount((double) this.getMaxScroll() - d0);
    }

    protected boolean removeEntryFromTop(E entry) {
        double d0 = (double) this.getMaxScroll() - this.getScrollAmount();
        boolean flag = this.removeEntry(entry);
        this.setScrollAmount((double) this.getMaxScroll() - d0);
        return flag;
    }

    protected int getItemCount() {
        return this.children()
            .size();
    }

    protected boolean isSelectedItem(int index) {
        return Objects.equals(
            this.getSelected(),
            this.children()
                .get(index));
    }

    @Nullable
    protected final E getEntryAtPosition(double mouseX, double mouseY) {
        int halfWidth = this.getRowWidth() / 2;
        int midX = this.x0 + this.width / 2;
        int left = midX - halfWidth;
        int right = midX + halfWidth;
        int relY = MathHelper.floor_double(mouseY - (double) this.y0) - this.headerHeight
            + (int) this.getScrollAmount()
            - 4;
        int index = relY / this.itemHeight;
        return (mouseX < (double) this.getScrollbarPosition() && mouseX >= (double) left
            && mouseX <= (double) right
            && index >= 0
            && relY >= 0
            && index < this.getItemCount()) ? this.children()
                .get(index) : null;
    }

    public void updateSize(int width, int height, int top, int bottom) {
        this.width = width;
        this.height = height;
        this.y0 = top;
        this.y1 = bottom;
        this.x0 = 0;
        this.x1 = width;
    }

    public void setLeftPos(int left) {
        this.x0 = left;
        this.x1 = left + this.width;
    }

    protected int getMaxPosition() {
        return this.getItemCount() * this.itemHeight + this.headerHeight;
    }

    protected void clickedHeader(int mouseX, int mouseY) {}

    protected void renderHeader(int mouseX, int mouseY) {}

    protected void renderBackground(int mouseX, int mouseY) {}

    protected void renderDecorations(int mouseX, int mouseY) {}

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mouseX, mouseY);
        int scrollbarLeft = this.getScrollbarPosition();
        int scrollbarRight = scrollbarLeft + 6;
        this.hovered = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        if (this.renderBackground) {
            this.drawContainerBackground();
        }

        int rowLeft = this.getRowLeft();
        int rowTopBase = this.y0 + 4 - (int) this.getScrollAmount();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.enableScissor();
        if (this.renderHeader) {
            this.renderHeader(rowLeft, rowTopBase);
        }

        this.renderList(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        if (this.renderTopAndBottom) {
            this.overlayBackground();
        }

        int maxScroll = this.getMaxScroll();
        if (maxScroll > 0) {
            int heightRange = this.y1 - this.y0;
            int barHeight = MathHelper.clamp_int(
                (int) ((float) (heightRange * heightRange) / (float) this.getMaxPosition()),
                32,
                heightRange - 8);
            int barTop = (int) this.getScrollAmount() * (heightRange - barHeight) / maxScroll + this.y0;

            if (barTop < this.y0) {
                barTop = this.y0;
            }

            drawRect(scrollbarLeft, this.y0, scrollbarRight, this.y1, 0xFF000000);
            drawRect(scrollbarLeft, barTop, scrollbarRight, barTop + barHeight, 0xFF808080);
            drawRect(scrollbarLeft, barTop, scrollbarRight - 1, barTop + barHeight - 1, 0xFFC0C0C0);
        }

        this.renderDecorations(mouseX, mouseY);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected void enableScissor() {
        Minecraft mc = Minecraft.getMinecraft();
        int scale = new net.minecraft.client.gui.ScaledResolution(mc, mc.displayWidth, mc.displayHeight)
            .getScaleFactor();
        GL11.glScissor(
            this.x0 * scale,
            mc.displayHeight - (this.y1 * scale),
            (this.x1 - this.x0) * scale,
            (this.y1 - this.y0) * scale);
    }

    protected void drawContainerBackground() {
        Tessellator tessellator = Tessellator.instance;
        this.minecraft.getTextureManager()
            .bindTexture(OPTIONS_BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float scale = 32.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(2105376);
        tessellator
            .addVertexWithUV(this.x0, this.y1, 0.0D, this.x0 / scale, (this.y1 + (int) this.getScrollAmount()) / scale);
        tessellator
            .addVertexWithUV(this.x1, this.y1, 0.0D, this.x1 / scale, (this.y1 + (int) this.getScrollAmount()) / scale);
        tessellator
            .addVertexWithUV(this.x1, this.y0, 0.0D, this.x1 / scale, (this.y0 + (int) this.getScrollAmount()) / scale);
        tessellator
            .addVertexWithUV(this.x0, this.y0, 0.0D, this.x0 / scale, (this.y0 + (int) this.getScrollAmount()) / scale);
        tessellator.draw();
    }

    protected void overlayBackground() {
        Tessellator tessellator = Tessellator.instance;
        this.minecraft.getTextureManager()
            .bindTexture(OPTIONS_BACKGROUND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(4210752);
        tessellator.addVertexWithUV(0, this.y0, 0.0D, 0.0D, this.y0 / 32.0F);
        tessellator.addVertexWithUV(this.width, this.y0, 0.0D, this.width / 32.0F, this.y0 / 32.0F);
        tessellator.addVertexWithUV(this.width, 0, 0.0D, this.width / 32.0F, 0.0D);
        tessellator.addVertexWithUV(0, 0, 0.0D, 0.0D, 0.0D);

        tessellator.addVertexWithUV(0, this.height, 0.0D, 0.0D, this.height / 32.0F);
        tessellator.addVertexWithUV(this.width, this.height, 0.0D, this.width / 32.0F, this.height / 32.0F);
        tessellator.addVertexWithUV(this.width, this.y1, 0.0D, this.width / 32.0F, this.y1 / 32.0F);
        tessellator.addVertexWithUV(0, this.y1, 0.0D, 0.0D, this.y1 / 32.0F);
        tessellator.draw();

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 0, 1);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        this.drawGradientRect(this.x0, this.y0, this.x1, this.y0 + 4, 0xFF000000, 0x00000000);
        this.drawGradientRect(this.x0, this.y1 - 4, this.x1, this.y1, 0x00000000, 0xFF000000);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
    }

    protected void centerScrollOn(E entry) {
        this.setScrollAmount(
            (double) (this.children()
                .indexOf(entry) * this.itemHeight + this.itemHeight / 2
                - (this.y1 - this.y0) / 2));
    }

    protected void ensureVisible(E entry) {
        int i = this.getRowTop(
            this.children()
                .indexOf(entry));
        int j = i - this.y0 - 4 - this.itemHeight;
        if (j < 0) {
            this.scroll(j);
        }

        int k = this.y1 - i - this.itemHeight - this.itemHeight;
        if (k < 0) {
            this.scroll(-k);
        }
    }

    private void scroll(int amount) {
        this.setScrollAmount(this.getScrollAmount() + (double) amount);
    }

    public double getScrollAmount() {
        return this.scrollAmount;
    }

    public void setScrollAmount(double scroll) {
        this.scrollAmount = MathHelper.clamp_double(scroll, 0.0D, (double) this.getMaxScroll());
    }

    public int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }

    public int getScrollBottom() {
        return (int) this.getScrollAmount() - this.height - this.headerHeight;
    }

    protected void updateScrollingState(double mouseX, double mouseY, int button) {
        this.scrolling = button == 0 && mouseX >= (double) this.getScrollbarPosition()
            && mouseX < (double) (this.getScrollbarPosition() + 6);
    }

    protected int getScrollbarPosition() {
        return this.width / 2 + 124;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.updateScrollingState(mouseX, mouseY, button);
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            E entry = this.getEntryAtPosition(mouseX, mouseY);
            if (entry != null) {
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    this.setSelected(entry);
                    return true;
                }
            } else if (button == 0) {
                this.clickedHeader(
                    (int) (mouseX - (double) (this.x0 + this.width / 2 - this.getRowWidth() / 2)),
                    (int) (mouseY - (double) this.y0) + (int) this.getScrollAmount() - 4);
                return true;
            }

            return this.scrolling;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.getSelected() != null) {
            this.getSelected()
                .mouseReleased(mouseX, mouseY, button);
        }
        this.scrolling = false;
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.scrolling) {
            if (mouseY < (double) this.y0) {
                this.setScrollAmount(0.0D);
            } else if (mouseY > (double) this.y1) {
                this.setScrollAmount((double) this.getMaxScroll());
            } else {
                double maxScroll = (double) Math.max(1, this.getMaxScroll());
                int heightRange = this.y1 - this.y0;
                int barHeight = MathHelper.clamp_int(
                    (int) ((float) (heightRange * heightRange) / (float) this.getMaxPosition()),
                    32,
                    heightRange - 8);
                double scrollFactor = Math.max(1.0D, maxScroll / (double) (heightRange - barHeight));
                this.setScrollAmount(this.getScrollAmount() + dragY * scrollFactor);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.setScrollAmount(this.getScrollAmount() - delta * (double) this.itemHeight / 2.0D);
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseY >= (double) this.y0 && mouseY <= (double) this.y1
            && mouseX >= (double) this.x0
            && mouseX <= (double) this.x1;
    }

    protected void renderList(int mouseX, int mouseY, float partialTicks) {
        int rowLeft = this.getRowLeft();
        int rowWidth = this.getRowWidth();
        int slotHeight = this.itemHeight - 4;
        int count = this.getItemCount();

        for (int i = 0; i < count; ++i) {
            int rowTop = this.getRowTop(i);
            int rowBottom = this.getRowBottom(i);
            if (rowBottom >= this.y0 && rowTop <= this.y1) {
                this.renderItem(mouseX, mouseY, partialTicks, i, rowLeft, rowTop, rowWidth, slotHeight);
            }
        }
    }

    protected void renderItem(int mouseX, int mouseY, float partialTicks, int index, int rowLeft, int rowTop,
        int rowWidth, int rowHeight) {
        E entry = this.getEntry(index);
        entry.renderBack(
            index,
            rowLeft,
            rowTop,
            rowWidth,
            rowHeight,
            mouseX,
            mouseY,
            Objects.equals(this.hovered, entry),
            partialTicks);
        if (this.renderSelection && this.isSelectedItem(index)) {
            int outlineColor = 0xFFFFFFFF;
            this.renderSelection(rowTop, rowWidth, rowHeight, outlineColor, 0xFF000000);
        }

        entry.render(
            index,
            rowLeft,
            rowTop,
            rowWidth,
            rowHeight,
            mouseX,
            mouseY,
            Objects.equals(this.hovered, entry),
            partialTicks);
    }

    protected void renderSelection(int y, int width, int height, int outlineColor, int fillColor) {
        int left = this.x0 + (this.width - width) / 2;
        int right = this.x0 + (this.width + width) / 2;
        drawRect(left, y - 2, right, y + height + 2, outlineColor);
        drawRect(left + 1, y - 1, right - 1, y + height + 1, fillColor);
    }

    public int getRowLeft() {
        return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
    }

    public int getRowRight() {
        return this.getRowLeft() + this.getRowWidth();
    }

    protected int getRowTop(int index) {
        return this.y0 + 4 - (int) this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
    }

    protected int getRowBottom(int index) {
        return this.getRowTop(index) + this.itemHeight;
    }

    @Nullable
    protected E remove(int index) {
        E entry = this.children.get(index);
        return (this.removeEntry(entry) ? entry : null);
    }

    protected boolean removeEntry(E entry) {
        boolean flag = this.children.remove(entry);
        if (flag && entry == this.getSelected()) {
            this.setSelected((E) null);
        }
        return flag;
    }

    @Nullable
    protected E getHovered() {
        return this.hovered;
    }

    void bindEntryToSelf(GuiSelectionList.Entry<E> entry) {
        entry.list = this;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getTop() {
        return this.y0;
    }

    public int getBottom() {
        return this.y1;
    }

    public int getLeft() {
        return this.x0;
    }

    public int getRight() {
        return this.x1;
    }

    @SideOnly(Side.CLIENT)
    public abstract static class Entry<E extends GuiSelectionList.Entry<E>> implements IGuiEventListener {

        @Deprecated
        protected GuiSelectionList<E> list;

        @Override
        public void setFocused(boolean focused) {}

        @Override
        public boolean isFocused() {
            return this.list != null && this.list.getSelected() == this;
        }

        public abstract void render(int index, int top, int left, int width, int height, int mouseX, int mouseY,
            boolean selected, float partialTicks);

        public void renderBack(int x, int top, int left, int width, int height, int mouseX, int mouseY,
            boolean selected, float partialTicks) {}

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.list != null && Objects.equals(this.list.getEntryAtPosition(mouseX, mouseY), this);
        }
    }

    @SideOnly(Side.CLIENT)
    class TrackedList extends AbstractList<E> {

        private final List<E> delegate = Lists.newArrayList();

        @Override
        public E get(int index) {
            return this.delegate.get(index);
        }

        @Override
        public int size() {
            return this.delegate.size();
        }

        @Override
        public E set(int index, E element) {
            E previous = this.delegate.set(index, element);
            GuiSelectionList.this.bindEntryToSelf(element);
            return previous;
        }

        @Override
        public void add(int index, E element) {
            this.delegate.add(index, element);
            GuiSelectionList.this.bindEntryToSelf(element);
        }

        @Override
        public E remove(int index) {
            return this.delegate.remove(index);
        }
    }
}
