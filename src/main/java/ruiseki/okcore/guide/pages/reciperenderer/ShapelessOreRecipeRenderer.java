package ruiseki.okcore.guide.pages.reciperenderer;

import java.util.ArrayList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class ShapelessOreRecipeRenderer extends BasicRecipeRenderer<ShapelessOreRecipe> {

    public ShapelessOreRecipeRenderer(ShapelessOreRecipe recipe) {
        super(recipe);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRenderer) {
        super.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRenderer);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int i = 3 * y + x;
                if (i >= recipe.getRecipeSize()) {} else {
                    int stackX = (x + 1) * 17 + (guiLeft + 29);
                    int stackY = (y + 1) * 17 + (guiTop + 40);
                    Object component = recipe.getInput()
                        .get(i);
                    if (component != null) {
                        if (component instanceof ItemStack) {
                            GuiHelpers.drawItemStack((ItemStack) component, stackX, stackY);
                            if (GuiHelpers.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                                tooltips = GuiHelpers.getTooltip((ItemStack) component);
                            }
                        } else {
                            ArrayList<ItemStack> list = (ArrayList<ItemStack>) component;
                            if (!list.isEmpty()) {
                                ItemStack stack = list.get(getRandomizedCycle(x + (y * 3), list.size()));
                                GuiHelpers.drawItemStack(stack, stackX, stackY);
                                if (GuiHelpers.isMouseBetween(mouseX, mouseY, stackX, stackY, 15, 15)) {
                                    tooltips = GuiHelpers.getTooltip(stack);
                                }
                            }
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
