package ruiseki.okcore.guide.pages;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class PageItemStack extends PageText {

    public ItemStack stack;

    /**
     * @param draw  - Unlocalized text to draw
     * @param stack - ItemStack to render
     */
    public PageItemStack(String draw, ItemStack stack) {
        super(draw, 60);
        this.stack = stack;
    }

    /**
     * @param draw - Unlocalized text to draw
     * @param item - Item to render
     */
    public PageItemStack(String draw, Item item) {
        this(draw, new ItemStack(item));
    }

    /**
     * @param draw  - Unlocalized text to draw
     * @param block - Block to render
     */
    public PageItemStack(String draw, Block block) {
        this(draw, new ItemStack(block));
    }

    /**
     * @param draw  - Unlocalized text to draw
     * @param entry - OreDict entry to render
     */
    public PageItemStack(String draw, String entry) {
        super(draw, 60);
        this.stack = new ItemStack(Blocks.fire);

        if (!OreDictionary.getOres(entry)
            .isEmpty()) {
            for (int i = 0; i < OreDictionary.getOres(entry)
                .size(); i++) {
                ItemStack stack = OreDictionary.getOres(entry)
                    .get(i);
                this.stack = stack;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop,
        int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        GuiHelpers.drawScaledItemStack(stack, guiLeft + 75, guiTop + 20, 3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageItemStack that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (stack != null ? stack.hashCode() : 0);
        return result;
    }
}
