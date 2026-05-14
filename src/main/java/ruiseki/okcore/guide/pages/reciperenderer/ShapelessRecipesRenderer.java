package ruiseki.okcore.guide.pages.reciperenderer;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.StatCollector;

import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class ShapelessRecipesRenderer extends BasicRecipeRenderer<ShapelessRecipes> {

    public ShapelessRecipesRenderer(ShapelessRecipes recipe) {
        super(recipe);
    }

    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRenderer) {
        super.draw(book, category, entry, pageLeft, pageTop, mouseX, mouseY, guiBase, fontRenderer);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int i = 3 * y + x;
                if (i < recipe.getRecipeSize()) {
                    int stackX = (x + 1) * 17 + (pageLeft - 39 + 53) + x;
                    int stackY = (y + 1) * 17 + (pageTop - 13 + 38) + y;
                    ItemStack stack = recipe.recipeItems.get(i);
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

    @Override
    protected String getRecipeName() {
        return StatCollector.translateToLocal("text.shapeless.crafting");
    }
}
