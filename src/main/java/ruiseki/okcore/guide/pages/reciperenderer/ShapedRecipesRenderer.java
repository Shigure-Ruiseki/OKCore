package ruiseki.okcore.guide.pages.reciperenderer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class ShapedRecipesRenderer extends BasicRecipeRenderer<ShapedRecipes> {

    public ShapedRecipesRenderer(ShapedRecipes recipe) {
        super(recipe);
    }

    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRenderer) {
        super.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRenderer);
        for (int y = 0; y < recipe.recipeHeight; y++) {
            for (int x = 0; x < recipe.recipeWidth; x++) {
                int stackX = (x + 1) * 17 + (guiLeft + 29);
                int stackY = (y + 1) * 17 + (guiTop + 40);
                ItemStack stack = recipe.recipeItems[y * recipe.recipeWidth + x];
                if (stack != null) {
                    GuiHelpers.drawItemStack(stack, stackX, stackY);
                    if (GuiHelpers.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                        tooltips = GuiHelpers.getTooltip(stack);
                    }
                }
            }
        }
    }
}
