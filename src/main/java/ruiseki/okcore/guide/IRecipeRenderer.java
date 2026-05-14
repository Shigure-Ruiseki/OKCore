package ruiseki.okcore.guide;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.crafting.IRecipe;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;

public interface IRecipeRenderer {

    @SideOnly(Side.CLIENT)
    void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj);

    @SideOnly(Side.CLIENT)
    void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj);

    abstract class RecipeRendererBase<T extends IRecipe> implements IRecipeRenderer {

        protected T recipe;
        protected List<String> tooltips = Lists.newArrayList();

        public RecipeRendererBase(T recipe) {
            this.recipe = recipe;
        }

        @Override
        public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop,
            int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
            guiBase.drawHoveringText(tooltips, mouseX, mouseY, fontRendererObj);
            tooltips.clear();
        }
    }
}
