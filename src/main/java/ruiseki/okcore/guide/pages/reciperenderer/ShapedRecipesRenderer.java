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
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRenderer) {
        super.draw(book, category, entry, pageLeft, pageTop, mouseX, mouseY, guiBase, fontRenderer);
        for (int y = 0; y < recipe.recipeHeight; y++) {
            for (int x = 0; x < recipe.recipeWidth; x++) {
                int stackX = (x + 1) * 17 + (pageLeft - 39 + 53) + x;
                int stackY = (y + 1) * 17 + (pageTop - 13 + 38) + y;
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
