package ruiseki.okcore.guide.gui;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ruiseki.okcore.guide.button.ButtonBack;
import ruiseki.okcore.guide.button.ButtonBase;
import ruiseki.okcore.guide.button.ButtonNext;
import ruiseki.okcore.guide.button.ButtonPrev;
import ruiseki.okcore.guide.button.ButtonSearch;
import ruiseki.okcore.guide.impl.Book;

public abstract class GuiBase extends GuiScreen implements IGuideBookScreen {

    protected final Book book;
    public final int screenWidth = 197;
    public final int screenHeight = 181;
    private final int pageWidth = 167;
    private final int pageHeight = 145;
    private final int pageXOffset = 39;
    private final int pageYOffset = 13;
    private final int backgroundXOffset = 25;
    private final int backgroundYOffset = 0;
    private ResourceLocation pageTexture;
    private ResourceLocation outlineTexture;
    private int screenTop, screenLeft;
    private int pageTop, pageLeft;
    public EntityPlayer player;
    private ButtonNext buttonNext;
    private ButtonPrev buttonPrev;
    private ButtonBack buttonBack;
    private ButtonSearch buttonSearch;
    private int currentPage;

    public GuiBase(Book book, EntityPlayer player) {
        this.book = book;
        this.player = player;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen((GuiScreen) null);
            this.mc.setIngameFocus();
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        screenLeft = (this.width - this.screenWidth) / 2;
        screenTop = (this.height - this.screenHeight) / 2;
        this.pageLeft = screenLeft + pageXOffset - backgroundXOffset;
        this.pageTop = screenTop + pageYOffset - backgroundYOffset;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(pageTexture);
        drawTexturedModalRect(screenLeft, screenTop, backgroundXOffset, backgroundYOffset, screenWidth, screenHeight);

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(outlineTexture);
        drawTexturedModalRectWithColor(
            screenLeft,
            screenTop,
            backgroundXOffset,
            backgroundYOffset,
            screenWidth,
            screenHeight,
            book.getColor());

        super.drawScreen(mouseX, mouseY, renderPartialTicks);

        if (getPageCount() > 1) {
            drawCenteredString(
                fontRendererObj,
                String.format("%d/%d", currentPage + 1, getPageCount()),
                screenLeft + screenWidth / 2,
                screenTop + 5 * screenHeight / 6,
                0);
        }
    }

    public void drawTexturedModalRectWithColor(int x, int y, int textureX, int textureY, int width, int height,
        Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        GL11.glColor3f((float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(
            (double) (x),
            (double) (y + height),
            (double) this.zLevel,
            (double) ((float) (textureX) * f),
            (double) ((float) (textureY + height) * f1));
        tessellator.addVertexWithUV(
            (double) (x + width),
            (double) (y + height),
            (double) this.zLevel,
            (double) ((float) (textureX + width) * f),
            (double) ((float) (textureY + height) * f1));
        tessellator.addVertexWithUV(
            (double) (x + width),
            (double) (y),
            (double) this.zLevel,
            (double) ((float) (textureX + width) * f),
            (double) ((float) (textureY) * f1));
        tessellator.addVertexWithUV(
            (double) (x),
            (double) (y),
            (double) this.zLevel,
            (double) ((float) (textureX) * f),
            (double) ((float) (textureY) * f1));
        tessellator.draw();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
        GL11.glPopMatrix();
    }

    @Override
    public void drawHoveringText(List<String> list, int x, int y, FontRenderer font) {
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        super.drawHoveringText(list, x, y, font);
        GL11.glPopAttrib();
    }

    @Override
    public void drawCenteredString(FontRenderer fontRenderer, String string, int x, int y, int color) {
        fontRenderer.drawString(string, x - fontRenderer.getStringWidth(string) / 2, y, color);
    }

    public void drawCenteredStringWithShadow(FontRenderer fontRenderer, String string, int x, int y, int color) {
        super.drawCenteredString(fontRenderer, string, x, y, color);
    }

    public void drawSplitString(String string, int x, int y, int maxLength, int color) {
        fontRendererObj.drawSplitString(string, x, y, maxLength, color);
    }

    @Override
    public void func_146283_a(List<String> p_146283_1_, int p_146283_2_, int p_146283_3_) {
        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
        super.func_146283_a(p_146283_1_, p_146283_2_, p_146283_3_);
        GL11.glPopAttrib();
    }

    @Override
    public void renderToolTip(ItemStack stack, int x, int y) {
        super.renderToolTip(stack, x, y);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public void setPage(int pageNum) {
        int i = MathHelper.clamp_int(pageNum, 0, getPageCount() - 1);
        if (i != this.currentPage) {
            this.currentPage = i;
            this.updateButtonVisibility();
        }
    }

    protected void addButtons(boolean back, boolean search) {
        this.buttonList.clear();
        addButton(
            buttonNext = new ButtonNext(
                1,
                pageLeft() + pageWidth() - 25,
                pageTop() + pageHeight() + 2,
                this,
                btn -> nextPage()));
        addButton(
            buttonPrev = new ButtonPrev(2, pageLeft() + 5, pageTop() + pageHeight() + 2, this, btn -> prevPage()));
        if (back) {
            addButton(buttonBack = new ButtonBack(0, pageLeft(), pageTop() - 14, this, btn -> goBack()));
        }
        if (search) {
            addButton(buttonSearch = new ButtonSearch(3, screenLeft - 15, screenTop, this, btn -> startSearch()));
        }
        updateButtonVisibility();
    }

    protected <T extends GuiButton> T addButton(T buttonIn) {
        this.buttonList.add(buttonIn);
        return buttonIn;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof ButtonBase) {
            ((ButtonBase) button).onPress();
        }
    }

    @Override
    public int pageWidth() {
        return pageWidth;
    }

    @Override
    public int pageHeight() {
        return pageHeight;
    }

    @Override
    public int pageLeft() {
        return pageLeft;
    }

    @Override
    public int pageTop() {
        return pageTop;
    }

    @Override
    public int pageXCenter() {
        return pageLeft + pageWidth / 2;
    }

    @Override
    public int pageYCenter() {
        return pageTop + pageHeight / 2;
    }

    @Override
    public int currentPage() {
        return currentPage;
    }

    @Override
    public EntityPlayer player() {
        return this.player;
    }

    protected void goBack() {
        this.mc.displayGuiScreen(new GuiHome(book, player()));
    }

    protected void nextPage() {
        if (currentPage < getPageCount() - 1) {
            currentPage++;
        }
        this.updateButtonVisibility();
    }

    protected void prevPage() {
        if (currentPage > 0) {
            currentPage--;
        }
        this.updateButtonVisibility();
    }

    protected int screenLeft() {
        return screenLeft;
    }

    protected int screenTop() {
        return screenTop;
    }

    protected void startSearch() {
        this.mc.displayGuiScreen(new GuiSearch(book, player(), this));
    }

    private void updateButtonVisibility() {
        buttonNext.visible = currentPage < getPageCount() - 1;
        buttonPrev.visible = currentPage > 0;
    }
}
