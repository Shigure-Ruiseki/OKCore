package ruiseki.okcore.test;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.Reference;
import ruiseki.okcore.guide.GuideBook;
import ruiseki.okcore.guide.IGuideBook;
import ruiseki.okcore.guide.category.CategoryItemStack;
import ruiseki.okcore.guide.entry.EntryItemStack;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.pages.PageIRecipe;
import ruiseki.okcore.guide.pages.PageText;

@GuideBook
public class BookTest implements IGuideBook {

    public static Book book;

    @Override
    public @Nullable Book buildBook() {
        book = new Book(new ResourceLocation(Reference.MOD_ID, "book_test"));
        book.setAuthor("Rui");
        book.setColor(0x7EF67F);
        book.setTitle("some.guide.title");
        book.setHeader("some.header.text");

        CategoryAbstract testCategory = new CategoryItemStack("test.category.name", new ItemStack(Items.apple))
            .withKeyBase("okcore");

        testCategory.addEntry("entry", new EntryItemStack("test.entry.name", new ItemStack(Items.potato)));
        testCategory.getEntry("entry")
            .addPage(new PageText("Hello, this is\nsome text"));
        testCategory.getEntry("entry")
            .addPage(PageIRecipe.newShaped(new ItemStack(Items.boat), "X X", "XXX", 'X', new ItemStack(Blocks.planks)));
        book.addCategory(testCategory);

        return book;
    }
}
