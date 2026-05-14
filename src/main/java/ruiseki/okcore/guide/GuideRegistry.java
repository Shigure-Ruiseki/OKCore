package ruiseki.okcore.guide;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.Reference;
import ruiseki.okcore.guide.impl.Book;

public class GuideRegistry {

    private static final Map<ResourceLocation, Book> BOOKS = Maps.newHashMap();
    private static final Map<Book, ItemStack> BOOK_TO_STACK = Maps.newHashMap();
    private static List<Book> indexedBooks = Lists.newArrayList();

    public static List<Book> getIndexedBooks() {
        return indexedBooks;
    }

    public static Map<ResourceLocation, Book> getBookMap() {
        return BOOKS;
    }

    public static Map<Book, ItemStack> getBookToStack() {
        return BOOK_TO_STACK;
    }

    public static void registerBook(Book book) {
        if (!isCallerAuthorized()) return;

        if (book == null || book.getRegistryName() == null) {
            OKCore.okLog(Level.WARN, "Attempted to register a null book or book without registry name.");
            return;
        }

        BOOKS.put(book.getRegistryName(), book);
    }

    public static void setBookForStack(Book book, ItemStack stack) {
        if (!isCallerAuthorized()) return;

        if (book != null && stack != null) {
            BOOK_TO_STACK.put(book, stack);
        }
    }

    public static void setIndexedBooks(List<Book> books) {
        if (!isCallerAuthorized()) return;

        if (books != null) {
            indexedBooks = Lists.newArrayList(books);
        }
    }

    private static boolean isCallerAuthorized() {
        ModContainer activeMod = Loader.instance()
            .activeModContainer();
        if (activeMod == null || !activeMod.getModId()
            .equals(Reference.MOD_ID)) {
            String modId = activeMod == null ? "Unknown" : activeMod.getModId();
            OKCore.okLog(Level.ERROR, "Mod " + modId + " tried to access an internal-only method in GuideRegistry.");
            return false;
        }
        return true;
    }
}
