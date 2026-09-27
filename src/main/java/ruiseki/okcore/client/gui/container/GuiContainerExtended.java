package ruiseki.okcore.client.gui.container;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.client.IContainerEventHandler;
import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.client.gui.component.IWidgetRenderable;
import ruiseki.okcore.client.gui.component.button.GuiButtonExtended;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.KeyBoardHelpers;
import ruiseki.okcore.inventory.IValueNotifiable;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.packet.PacketButtonClick;

/**
 * An extended GUI container.
 *
 * @author rubensworks
 */
public abstract class GuiContainerExtended<T extends ContainerExtended> extends GuiContainer
    implements IValueNotifiable, IContainerEventHandler, IContainerAccess<T> {

    protected T container;
    protected ResourceLocation texture;
    protected int offsetX = 0;
    protected int offsetY = 0;

    @Nullable
    private IGuiEventListener focused;
    private boolean isDragging;

    private final List<IGuiEventListener> children = Lists.newArrayList();
    public final List<IWidgetRenderable> renderables = Lists.newArrayList();

    // 1. CONSTRUCTORS & BASIC GETTERS

    public GuiContainerExtended(T container) {
        super(container);
        container.setGuiValueListener(this);
        this.container = container;
        this.texture = constructGuiTexture();
    }

    @Override
    public T getContainer() {
        return this.container;
    }

    @Override
    public ContainerType<?> getValueNotifiableType() {
        return getContainer().getType();
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

    protected int getBaseXSize() {
        return 176;
    }

    protected int getBaseYSize() {
        return 166;
    }

    public int getGuiLeftTotal() {
        return this.guiLeft + offsetX;
    }

    public int getGuiTopTotal() {
        return this.guiTop + offsetY;
    }

    // 2. INIT & LIFECYCLE

    @Override
    public void initGui() {
        this.xSize = getBaseXSize() + offsetX * 2;
        this.ySize = getBaseYSize() + offsetY * 2;
        super.initGui();
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

    // 3. RENDERING & DRAWING

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft + offsetX, guiTop + offsetY, 0, 0, xSize - 2 * offsetX, ySize - 2 * offsetY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        for (IWidgetRenderable renderable : this.renderables) {
            GuiHelpers.renderTooltip(
                this,
                renderable.getX(),
                renderable.getY(),
                renderable.getWidth(),
                renderable.getHeight(),
                mouseX,
                mouseY,
                () -> Collections.singletonList(renderable.getTooltip()));
        }
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

    // 4. WIDGET & CHILDREN MANAGEMENT

    protected <T extends IGuiEventListener & IWidgetRenderable> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        return this.addWidget(widget);
    }

    protected <T extends IWidgetRenderable> T addRenderableOnly(T widget) {
        this.renderables.add(widget);
        return widget;
    }

    protected <T extends IGuiEventListener> T addWidget(T widget) {
        this.children.add(widget);
        return widget;
    }

    protected void removeWidget(IGuiEventListener widget) {
        if (widget instanceof IWidgetRenderable) {
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

    // 5. STATE & HELPERS

    public boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
        int k1 = this.guiLeft;
        int l1 = this.guiTop;
        pointX -= k1;
        pointY -= l1;
        return pointX >= left && pointX < left + right && pointY >= top && pointY < top + bottom;
    }

    public boolean isPointInRegion(Rectangle region, Point mouse) {
        return isPointInRegion(region.x, region.y, region.width, region.height, mouse.x, mouse.y);
    }

    @Override
    public final boolean func_146978_c(int left, int top, int right, int bottom, int pointX, int pointY) {
        return isPointInRegion(left, top, right, bottom, pointX, pointY);
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int button) {
        return mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
    }

    @Nullable
    public Slot getSlotUnderMouse() {
        return this.theSlot;
    }

    @Override
    public final boolean isDragging() {
        return this.isDragging;
    }

    @Override
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

    // 6. EVENT HANDLING (MOUSE & KEYBOARD)

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (IContainerEventHandler.super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        boolean isPickBlockKey = button == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
        Slot slot = this.getSlotAtPosition((int) mouseX, (int) mouseY);
        long currentTime = Minecraft.getSystemTime();

        this.field_146993_M = this.field_146998_K == slot
            && currentTime - this.field_146997_J < DOUBLE_CLICK_THRESHOLD_MS
            && this.field_146992_L == button;
        this.field_146995_H = false; // skipNextRelease

        boolean handled = false;

        if (button == 0 || button == 1 || isPickBlockKey) {
            int guiLeft = this.guiLeft;
            int guiTop = this.guiTop;

            boolean isOutside = mouseX < guiLeft || mouseY < guiTop
                || mouseX >= guiLeft + this.xSize
                || mouseY >= guiTop + this.ySize;

            if (slot != null) {
                isOutside = false;
            }

            int slotIndex = slot != null ? slot.slotNumber : -1;
            if (isOutside) {
                slotIndex = -999;
            }

            // Touchscreen
            if (this.mc.gameSettings.touchscreen && isOutside && this.mc.thePlayer.inventory.getItemStack() == null) {
                this.mc.displayGuiScreen((GuiScreen) null);
                return true;
            }

            if (slotIndex >= -1) {
                handled = true; // Click Slot or Drop Item

                if (this.mc.gameSettings.touchscreen) {
                    if (slot != null && slot.getHasStack()) {
                        this.clickedSlot = slot;
                        this.draggedStack = null;
                        this.isRightMouseClick = button == 1;
                    } else {
                        this.clickedSlot = null;
                    }
                } else if (!this.field_147007_t) { // isQuickCrafting
                    if (this.mc.thePlayer.inventory.getItemStack() == null) {
                        if (isPickBlockKey) {
                            this.handleMouseClick(slot, slotIndex, button, 3);
                        } else {
                            boolean isShiftDown = slotIndex != -999 && KeyBoardHelpers.isShiftKeyDown();
                            byte mode = 0;

                            if (isShiftDown) {
                                this.field_146994_N = slot != null && slot.getHasStack() ? slot.getStack() : null;
                                mode = 1;
                            } else if (slotIndex == -999) {
                                mode = 4;
                            }

                            this.handleMouseClick(slot, slotIndex, button, mode);
                        }

                        this.field_146995_H = true;
                    } else {
                        this.field_147007_t = true;
                        this.field_146988_G = button;
                        this.field_147008_s.clear();

                        if (button == 0) {
                            this.field_146987_F = 0;
                        } else if (button == 1) {
                            this.field_146987_F = 1;
                        } else if (isPickBlockKey) {
                            this.field_146987_F = 2;
                        }
                    }
                }
            }
        }

        this.field_146998_K = slot;
        this.field_146997_J = currentTime;
        this.field_146992_L = button;

        return handled;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (IContainerEventHandler.super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }

        Slot slot = this.getSlotAtPosition((int) mouseX, (int) mouseY);
        int guiLeft = this.guiLeft;
        int guiTop = this.guiTop;
        boolean isOutside = mouseX < guiLeft || mouseY < guiTop
            || mouseX >= guiLeft + this.xSize
            || mouseY >= guiTop + this.ySize;
        int slotIndex = slot != null ? slot.slotNumber : -1;

        if (isOutside) {
            slotIndex = -999;
        }

        boolean handled = false;

        if (this.field_146993_M && slot != null
            && button == 0
            && this.inventorySlots.func_94530_a((ItemStack) null, slot)) {
            if (KeyBoardHelpers.isShiftKeyDown()) {
                if (slot.inventory != null && this.field_146994_N != null) {
                    for (Object obj : this.inventorySlots.inventorySlots) {
                        Slot slot1 = (Slot) obj;
                        if (slot1 != null && slot1.canTakeStack(this.mc.thePlayer)
                            && slot1.getHasStack()
                            && slot1.inventory == slot.inventory
                            && Container.func_94527_a(slot1, this.field_146994_N, true)) {
                            this.handleMouseClick(slot1, slot1.slotNumber, button, 1);
                        }
                    }
                }
            } else {
                this.handleMouseClick(slot, slotIndex, button, 6);
            }

            this.field_146993_M = false;
            this.field_146997_J = 0L;
            handled = true;
        } else {
            if (this.field_147007_t && this.field_146988_G != button) {
                this.field_147007_t = false;
                this.field_147008_s.clear();
                this.field_146995_H = true;
                return true;
            }

            if (this.field_146995_H) {
                this.field_146995_H = false;
                return true;
            }

            boolean isValidPlacement;

            if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
                if (button == 0 || button == 1) {
                    if (this.draggedStack == null && slot != this.clickedSlot) {
                        this.draggedStack = this.clickedSlot.getStack();
                    }

                    isValidPlacement = Container.func_94527_a(slot, this.draggedStack, false);

                    if (slotIndex != -1 && this.draggedStack != null && isValidPlacement) {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, button, 0);
                        this.handleMouseClick(slot, slotIndex, 0, 0);

                        if (this.mc.thePlayer.inventory.getItemStack() != null) {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, button, 0);
                            this.field_147011_y = (int) mouseX - guiLeft;
                            this.field_147010_z = (int) mouseY - guiTop;
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Minecraft.getSystemTime();
                        } else {
                            this.returningStack = null;
                        }
                    } else if (this.draggedStack != null) {
                        this.field_147011_y = (int) mouseX - guiLeft;
                        this.field_147010_z = (int) mouseY - guiTop;
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Minecraft.getSystemTime();
                    }

                    this.draggedStack = null;
                    this.clickedSlot = null;
                    handled = true;
                }
            } else if (this.field_147007_t && !this.field_147008_s.isEmpty()) {
                this.handleMouseClick(null, -999, Container.func_94534_d(0, this.field_146987_F), 5);

                for (Object obj : this.field_147008_s) {
                    Slot slot1 = (Slot) obj;
                    this.handleMouseClick(slot1, slot1.slotNumber, Container.func_94534_d(1, this.field_146987_F), 5);
                }

                this.handleMouseClick(null, -999, Container.func_94534_d(2, this.field_146987_F), 5);
                handled = true;
            } else if (this.mc.thePlayer.inventory.getItemStack() != null) {
                if (button == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                    this.handleMouseClick(slot, slotIndex, button, 3);
                } else {
                    isValidPlacement = slotIndex != -999 && KeyBoardHelpers.isShiftKeyDown();

                    if (isValidPlacement) {
                        this.field_146994_N = slot != null && slot.getHasStack() ? slot.getStack() : null;
                    }

                    this.handleMouseClick(slot, slotIndex, button, isValidPlacement ? 1 : 0);
                }
                handled = true;
            }
        }

        if (this.mc.thePlayer.inventory.getItemStack() == null) {
            this.field_146997_J = 0L;
        }

        this.field_147007_t = false;
        return handled;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (IContainerEventHandler.super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }

        Slot slot = this.getSlotAtPosition((int) mouseX, (int) mouseY);
        ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();
        boolean handled = false;

        if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
            if (button == 0 || button == 1) {
                if (this.draggedStack == null) {
                    if (slot != this.clickedSlot) {
                        this.draggedStack = this.clickedSlot.getStack()
                            .copy();
                    }
                } else if (this.draggedStack.stackSize > 1 && slot != null
                    && Container.func_94527_a(slot, this.draggedStack, false)) {
                        long i = Minecraft.getSystemTime();

                        if (this.field_146985_D == slot) {
                            if (i - this.field_146986_E > 500L) {
                                this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                                this.handleMouseClick(slot, slot.slotNumber, 1, 0);
                                this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                                this.field_146986_E = i + 750L;
                                --this.draggedStack.stackSize;
                            }
                        } else {
                            this.field_146985_D = slot;
                            this.field_146986_E = i;
                        }
                    }
                handled = true;
            }
        } else if (this.field_147007_t && slot != null
            && itemstack != null
            && itemstack.stackSize > this.field_147008_s.size()
            && Container.func_94527_a(slot, itemstack, true)
            && slot.isItemValid(itemstack)
            && this.inventorySlots.canDragIntoSlot(slot)) {
                this.field_147008_s.add(slot);
                this.func_146980_g();
                handled = true;
            }

        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return IContainerEventHandler.super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (IContainerEventHandler.super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
            return true;
        }

        boolean handled = this.checkHotbarKeys(keyCode);

        if (this.theSlot != null && this.theSlot.getHasStack()) {
            if (keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode()) {
                this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
                handled = true;
            } else if (keyCode == this.mc.gameSettings.keyBindDrop.getKeyCode()) {
                this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
                handled = true;
            }
        }

        return handled;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return IContainerEventHandler.super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return IContainerEventHandler.super.charTyped(codePoint, modifiers);
    }
}
