package ruiseki.okcore.helper;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import ruiseki.okcore.guide.GuideRegistry;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;

public class GuideHelpers {

    /**
     * Obtains a new ItemStack associated with the provided book.
     *
     * @param book - The book to get an ItemStack for.
     * @return - The ItemStack associated with the provided book.
     */
    public static ItemStack getStackFromBook(Book book) {
        if (book == null) return null;
        return GuideRegistry.getBookToStack()
            .get(book);
    }

    public static Map<ResourceLocation, Book> getBooks() {
        return ImmutableMap.copyOf(GuideRegistry.getBookMap());
    }

    public static Map<Book, ItemStack> getBookToStack() {
        return ImmutableMap.copyOf(GuideRegistry.getBookToStack());
    }

    public static List<Book> getIndexedBooks() {
        return ImmutableList.copyOf(GuideRegistry.getIndexedBooks());
    }

    public static CategoryAbstract getCategoryFromName(Book book, String unlocName) {
        if (book == null || unlocName == null) return null;

        for (CategoryAbstract category : book.getCategories()) {
            if (unlocName.equals(category.name)) return category;
        }
        return null;
    }

    public static EntryAbstract getEntryFromName(CategoryAbstract category, String unlocName) {
        if (category == null || unlocName == null) return null;

        for (EntryAbstract entry : category.entries.values()) {
            if (unlocName.equals(entry.name)) return entry;
        }
        return null;
    }

}
