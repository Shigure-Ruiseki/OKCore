package ruiseki.okcore.guide;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.item.ItemGuideBook;

public class GuideHandler {

    public static final List<Pair<Book, IGuideBook>> BOOK_CLASSES = Lists.newArrayList();
    private static final List<IGuideBook> PENDING_BOOKS = Lists.newArrayList();

    public static void gatherBooks(ASMDataTable dataTable) {
        for (ASMDataTable.ASMData data : dataTable.getAll(GuideBook.class.getCanonicalName())) {
            try {
                Class<?> genericClass = Class.forName(data.getClassName());
                if (!IGuideBook.class.isAssignableFrom(genericClass)) continue;

                IGuideBook guideBook = (IGuideBook) genericClass.getDeclaredConstructor()
                    .newInstance();
                if (guideBook.shouldRegister()) {
                    PENDING_BOOKS.add(guideBook);
                } else {
                    OKCore.okLog(Level.INFO, "Skipping registration for book: " + data.getClassName() + " (Condition not met)");
                }

            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Error finding book class " + data.getClassName(), e);
            }
        }
    }

    public static void buildAllBooks() {
        for (IGuideBook guideBook : PENDING_BOOKS) {
            try {
                Book book = guideBook.buildBook();
                if (book == null) continue;

                GuideRegistry.registerBook(book);
                BOOK_CLASSES.add(Pair.of(book, guideBook));

                registerSingleBookItem(book);
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Error building book content", e);
            }
        }
        PENDING_BOOKS.clear();

        GuideRegistry.setIndexedBooks(
            Lists.newArrayList(
                GuideRegistry.getBookMap()
                    .values()));
    }

    private static void registerSingleBookItem(Book book) {
        Item guideItem = new ItemGuideBook(book);
        guideItem.setUnlocalizedName(
            book.getRegistryName()
                .toString()
                .replace(":", "_"));
        GameRegistry.registerItem(
            guideItem,
            book.getRegistryName()
                .getResourcePath()
                .replace(":", "_"));
        GuideRegistry.setBookForStack(book, new ItemStack(guideItem));
    }
}
