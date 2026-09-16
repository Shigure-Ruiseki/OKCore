package ruiseki.okcore.client.gui.container;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.client.IContainerEventHandler;
import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.client.gui.IRenderable;
import ruiseki.okcore.client.gui.component.button.GuiButtonExtended;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.inventory.IValueNotifiable;
import ruiseki.okcore.inventory.container.ExtendedInventoryContainer;
import ruiseki.okcore.network.packet.PacketButtonClick;

/**
 * An extended GUI container.
 *
 * @author rubensworks
 */
public abstract class GuiContainerExtended<T extends ExtendedInventoryContainer> extends GuiContainer
    implements IValueNotifiable, IRenderable, IContainerEventHandler {

    protected T container;
    protected ResourceLocation texture;
    protected int offsetX = 0;
    protected int offsetY = 0;

    @Nullable
    private IGuiEventListener focused;
    private boolean isDragging;

    private final List<IGuiEventListener> children = Lists.newArrayList();
    public final List<IRenderable> renderables = Lists.newArrayList();

    /**
     * Make a new instance.
     *
     * @param container The container to make the GUI for.
     */
    public GuiContainerExtended(T container) {
        super(container);
        container.setGuiValueListener(this);
        this.container = container;
        this.texture = constructGuiTexture();
    }

    public T getContainer() {
        return this.container;
    }

    protected abstract ResourceLocation constructGuiTexture();

    /**
     * Get the texture path of the GUI.
     *
     * @return The path of the GUI for this block.
     */
    public ResourceLocation getGuiTexture() {
        return this.texture;
    }

    @Override
    public void initGui() {
        this.xSize = getBaseXSize() + offsetX * 2;
        this.ySize = getBaseYSize() + offsetY * 2;
        super.initGui();
    }

    protected int getBaseXSize() {
        return 176;
    }

    protected int getBaseYSize() {
        return 166;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        for (IRenderable renderable : this.renderables) {
            renderable.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft + offsetX, guiTop + offsetY, 0, 0, xSize - 2 * offsetX, ySize - 2 * offsetY);
    }

    public boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
        int k1 = this.guiLeft;
        int l1 = this.guiTop;
        pointX -= k1;
        pointY -= l1;
        return pointX >= left && pointX < left + right && pointY >= top && pointY < top + bottom;
    }

    @Override
    public final boolean func_146978_c(int left, int top, int right, int bottom, int pointX, int pointY) {
        return isPointInRegion(left, top, right, bottom, pointX, pointY);
    }

    public boolean isPointInRegion(Rectangle region, Point mouse) {
        return isPointInRegion(region.x, region.y, region.width, region.height, mouse.x, mouse.y);
    }

    public void drawTexturedModalRectScalable(int destX, int destY, int destWidth, int destHeight, int srcX, int srcY,
        int srcWidth, int srcHeight) {
        float f = 0.00390625F; // 1 / 256.0F
        float f1 = 0.00390625F;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        // Vertex 1: Bottom-Left
        tessellator.addVertexWithUV(
            destX,
            destY + destHeight,
            this.zLevel,
            (double) (srcX * f),
            (double) ((srcY + srcHeight) * f1));

        // Vertex 2: Bottom-Right
        tessellator.addVertexWithUV(
            destX + destWidth,
            destY + destHeight,
            this.zLevel,
            (double) ((srcX + srcWidth) * f),
            (double) ((srcY + srcHeight) * f1));

        // Vertex 3: Top-Right
        tessellator.addVertexWithUV(
            destX + destWidth,
            destY,
            this.zLevel,
            (double) ((srcX + srcWidth) * f),
            (double) (srcY * f1));

        // Vertex 4: Top-Left
        tessellator.addVertexWithUV(destX, destY, this.zLevel, (double) (srcX * f), (double) (srcY * f1));

        tessellator.draw();
    }

    public void drawTooltip(List<String> lines, int x, int y) {
        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();

        int tooltipWidth = 0;
        int tempWidth;
        int xStart;
        int yStart;

        for (String line : lines) {
            tempWidth = this.fontRendererObj.getStringWidth(line);

            if (tempWidth > tooltipWidth) {
                tooltipWidth = tempWidth;
            }
        }

        xStart = x + 12;
        yStart = y - 12;
        int tooltipHeight = 8;

        if (lines.size() > 1) {
            tooltipHeight += 2 + (lines.size() - 1) * 10;
        }

        if (this.guiLeft + xStart + tooltipWidth + 6 > this.width) {
            xStart = this.width - tooltipWidth - this.guiLeft - 6;
        }

        if (this.guiTop + yStart + tooltipHeight + 6 > this.height) {
            yStart = this.height - tooltipHeight - this.guiTop - 6;
        }

        this.zLevel = 300.0F;
        itemRender.zLevel = 300.0F;
        int color1 = -267386864;
        this.drawGradientRect(xStart - 3, yStart - 4, xStart + tooltipWidth + 3, yStart - 3, color1, color1);
        this.drawGradientRect(
            xStart - 3,
            yStart + tooltipHeight + 3,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 4,
            color1,
            color1);
        this.drawGradientRect(
            xStart - 3,
            yStart - 3,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3,
            color1,
            color1);
        this.drawGradientRect(xStart - 4, yStart - 3, xStart - 3, yStart + tooltipHeight + 3, color1, color1);
        this.drawGradientRect(
            xStart + tooltipWidth + 3,
            yStart - 3,
            xStart + tooltipWidth + 4,
            yStart + tooltipHeight + 3,
            color1,
            color1);
        int color2 = 1347420415;
        int color3 = (color2 & 16711422) >> 1 | color2 & -16777216;
        this.drawGradientRect(
            xStart - 3,
            yStart - 3 + 1,
            xStart - 3 + 1,
            yStart + tooltipHeight + 3 - 1,
            color2,
            color3);
        this.drawGradientRect(
            xStart + tooltipWidth + 2,
            yStart - 3 + 1,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3 - 1,
            color2,
            color3);
        this.drawGradientRect(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart - 3 + 1, color2, color2);
        this.drawGradientRect(
            xStart - 3,
            yStart + tooltipHeight + 2,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3,
            color3,
            color3);

        for (int stringIndex = 0; stringIndex < lines.size(); ++stringIndex) {
            String line = lines.get(stringIndex);

            if (stringIndex == 0) {
                line = "§" + Integer.toHexString(15) + line;
            } else {
                line = "§7" + line;
            }

            this.fontRendererObj.drawStringWithShadow(line, xStart, yStart, -1);

            if (stringIndex == 0) {
                yStart += 2;
            }

            yStart += 10;
        }

        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GlStateManager.popMatrix();

        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

    /**
     * Call this to create a button pressable callback so that the container is notified as well,
     * assuming it has a corresponding registered
     * {@link ruiseki.okcore.inventory.container.button.IContainerButtonAction} registered in the container
     * by the same button id.
     *
     * @param buttonId        The button id.
     * @param clientPressable An optional pressable that should be called client-side.
     * @return The created pressable.
     */
    protected GuiButtonExtended.OnPress createServerPressable(String buttonId,
        @Nullable GuiButtonExtended.OnPress clientPressable) {
        return (button) -> {
            if (clientPressable != null) {
                clientPressable.onPress(button);
            }
            if (getContainer().onButtonClick(buttonId)) {
                OKCore._instance.getPacketHandler()
                    .sendToServer(new PacketButtonClick(buttonId));
            }
        };
    }

    @Override
    public void onUpdate(int valueId, NBTTagCompound value) {

    }

    /**
     * Will send client-side onUpdate events for all stored values
     */
    protected void refreshValues() {
        for (int id : getContainer().getValueIds()) {
            onUpdate(id, getContainer().getValue(id));
        }
    }

    /**
     * @return The total gui left offset.
     */
    public int getGuiLeftTotal() {
        return this.guiLeft + offsetX;
    }

    /**
     * @return The total gui top offset.
     */
    public int getGuiTopTotal() {
        return this.guiTop + offsetY;
    }

    @Override
    public String getGuiModId() {
        return getContainer().getGuiModId();
    }

    @Override
    public int getGuiId() {
        return getContainer().getGuiId();
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int button) {
        return mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
    }

    @Nullable
    public Slot getSlotUnderMouse() {
        return this.theSlot;
    }

    protected <T extends IGuiEventListener & IRenderable> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        return this.addWidget(widget);
    }

    protected <T extends IRenderable> T addRenderableOnly(T widget) {
        this.renderables.add(widget);
        return widget;
    }

    protected <T extends IGuiEventListener> T addWidget(T widget) {
        this.children.add(widget);
        return widget;
    }

    protected void removeWidget(IGuiEventListener widget) {
        if (widget instanceof IRenderable) {
            this.renderables.remove(widget);
        }

        this.children.remove(widget);
    }

    protected void clearWidgets() {
        this.renderables.clear();
        this.children.clear();
    }

    @Override
    public List<IGuiEventListener> getChildren() {
        return children;
    }

    public final boolean isDragging() {
        return this.isDragging;
    }

    public final void setDragging(boolean dragging) {
        this.isDragging = dragging;
    }

    @Nullable
    public IGuiEventListener getFocused() {
        return this.focused;
    }

    public void setFocused(@Nullable IGuiEventListener focused) {
        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (focused != null) {
            focused.setFocused(true);
        }

        this.focused = focused;
    }
}
