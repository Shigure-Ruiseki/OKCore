package ruiseki.okcore.guide.pages.reciperenderer;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import ruiseki.okcore.Reference;
import ruiseki.okcore.guide.IRecipeRenderer.RecipeRendererBase;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class BasicRecipeRenderer<T extends IRecipe> extends RecipeRendererBase<T> {

    private long lastCycle = -1;
    private int cycleIdx = 0;
    private Random rand = new Random();
    private String customDisplay;

    public BasicRecipeRenderer(T recipe) {
        super(recipe);
    }

    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRenderer) {
        Minecraft mc = Minecraft.getMinecraft();

        long time = mc.theWorld.getTotalWorldTime();
        if (lastCycle < 0 || lastCycle < time - 20) {
            if (lastCycle > 0) {
                cycleIdx++;
                cycleIdx = Math.max(0, cycleIdx);
            }
            lastCycle = mc.theWorld.getTotalWorldTime();
        }

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(new ResourceLocation(Reference.PREFIX_GUI + "guide/recipe_elements.png"));
        guiBase.drawTexturedModalRect(pageLeft - 39 + 68, pageTop - 13 + 53, 0, 48, 102, 56);
        guiBase.drawCenteredString(fontRenderer, getRecipeName(), guiBase.pageXCenter(), pageTop, 0);

        int stationX = pageLeft - 39 + 125;
        int stationY = pageTop - 13 + 55;

        int outputX = pageLeft - 39 + 148;
        int outputY = pageTop - 13 + 73;
        GuiHelpers.drawItemStack(recipe.getRecipeOutput(), outputX, outputY);
        if (GuiHelpers.isMouseBetween(mouseX, mouseY, outputX, outputY, 15, 15)) {
            tooltips = GuiHelpers.getTooltip(recipe.getRecipeOutput());
        }
    }

    protected int getRandomizedCycle(int index, int max) {
        rand.setSeed(index);
        return (index + rand.nextInt(max) + cycleIdx) % max;
    }

    protected String getRecipeName() {
        return StatCollector.translateToLocal("text.shaped.crafting");
    }
}
