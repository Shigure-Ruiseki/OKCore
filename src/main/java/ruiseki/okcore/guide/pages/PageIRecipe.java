package ruiseki.okcore.guide.pages;

import java.util.Objects;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.IRecipeRenderer;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.gui.GuiEntry;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Page;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.guide.pages.reciperenderer.ShapedOreRecipeRenderer;
import ruiseki.okcore.guide.pages.reciperenderer.ShapedRecipesRenderer;
import ruiseki.okcore.guide.pages.reciperenderer.ShapelessOreRecipeRenderer;
import ruiseki.okcore.guide.pages.reciperenderer.ShapelessRecipesRenderer;

public class PageIRecipe extends Page {

    public IRecipe recipe;
    public IRecipeRenderer iRecipeRenderer;
    protected boolean isValid;

    /**
     * Use this if you are creating a page for a standard recipe, one of:
     * <p>
     * <ul>
     * <li>{@link ShapedRecipes}</li>
     * <li>{@link ShapelessRecipes}</li>
     * <li>{@link ShapedOreRecipe}</li>
     * <li>{@link ShapelessOreRecipe}</li>
     * </ul>
     *
     * @param recipe - Recipe to draw
     */
    public PageIRecipe(IRecipe recipe) {
        this(recipe, getRenderer(recipe));
    }

    /**
     * @param recipe          - Recipe to draw
     * @param iRecipeRenderer - Your custom Recipe drawer
     */
    public PageIRecipe(IRecipe recipe, IRecipeRenderer iRecipeRenderer) {
        this.recipe = recipe;
        this.iRecipeRenderer = iRecipeRenderer;
        isValid = recipe != null && iRecipeRenderer != null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        if (isValid) {
            super.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
            iRecipeRenderer.draw(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop,
        int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        if (isValid) {
            super.drawExtras(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
            iRecipeRenderer
                .drawExtras(book, category, entry, guiLeft, guiTop, mouseX, mouseY, guiBase, fontRendererObj);
        }
    }

    @Override
    public boolean canSee(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player,
        ItemStack bookStack, GuiEntry guiEntry) {
        return isValid;
    }

    public static PageIRecipe newShaped(ItemStack output, Object... input) {
        return new PageIRecipe(new ShapedOreRecipe(output, input));
    }

    public static PageIRecipe newShapeless(ItemStack output, Object... input) {
        return new PageIRecipe(new ShapelessOreRecipe(output, input));
    }

    static IRecipeRenderer getRenderer(IRecipe recipe) {
        if (recipe == null) {
            OKCore.okLog(Level.ERROR, "Cannot get renderer for null recipe.");
            return null;
        } else if (recipe instanceof ShapedRecipes) {
            return new ShapedRecipesRenderer((ShapedRecipes) recipe);
        } else if (recipe instanceof ShapelessRecipes) {
            return new ShapelessRecipesRenderer((ShapelessRecipes) recipe);
        } else if (recipe instanceof ShapedOreRecipe) {
            return new ShapedOreRecipeRenderer((ShapedOreRecipe) recipe);
        } else if (recipe instanceof ShapelessOreRecipe) {
            return new ShapelessOreRecipeRenderer((ShapelessOreRecipe) recipe);
        } else {
            OKCore.okLog(
                Level.ERROR,
                "Cannot get renderer for recipe type " + recipe.getClass()
                    .toString());
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageIRecipe that)) return false;
        if (!super.equals(o)) return false;

        if (!Objects.equals(recipe, that.recipe)) return false;
        return Objects.equals(iRecipeRenderer, that.iRecipeRenderer);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (recipe != null ? recipe.hashCode() : 0);
        result = 31 * result + (iRecipeRenderer != null ? iRecipeRenderer.hashCode() : 0);
        return result;
    }
}
