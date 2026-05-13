package ruiseki.okcore.guide;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.asm.ModAnnotation;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.item.ItemGuideBook;

public class GuideHandler {

    public static final List<Pair<Book, IGuideBook>> BOOK_CLASSES = Lists.newArrayList();

    public static void gatherBooks(ASMDataTable dataTable) {
        for (EventPriority priority : EventPriority.values())
            for (ASMDataTable.ASMData data : dataTable.getAll(GuideBook.class.getCanonicalName())) {
                try {
                    Class<?> genericClass = Class.forName(data.getClassName());
                    if (!IGuideBook.class.isAssignableFrom(genericClass)) continue;

                    IGuideBook guideBook = (IGuideBook) genericClass.getDeclaredConstructor()
                        .newInstance();
                    ModAnnotation.EnumHolder holder = (ModAnnotation.EnumHolder) data.getAnnotationInfo()
                        .get("priority");
                    String enumValue = "NORMAL";
                    if (holder != null) {
                        try {
                            java.lang.reflect.Field valueField = holder.getClass()
                                .getDeclaredField("value");
                            valueField.setAccessible(true);
                            enumValue = (String) valueField.get(holder);
                        } catch (Exception e) {
                            OKCore.okLog(Level.ERROR, e.getMessage());
                        }
                    }
                    EventPriority bookPriority = EventPriority.valueOf(enumValue);

                    if (priority != bookPriority) continue;
                    Book book = guideBook.buildBook();
                    if (book == null) continue;
                    GuideRegistry.registerBook(book);
                    BOOK_CLASSES.add(Pair.of(book, guideBook));
                } catch (Exception e) {
                    OKCore.okLog(Level.ERROR, "Error registering book for class " + data.getClassName(), e);
                }
            }

        GuideRegistry.setIndexedBooks(
            Lists.newArrayList(
                GuideRegistry.getBookMap()
                    .values()));
    }

    public static void registerGuideBookItems() {
        for (Book book : GuideRegistry.getBookMap()
            .values()) {

            Item guideItem = new ItemGuideBook(book);
            guideItem.setUnlocalizedName(
                book.getRegistryName()
                    .toString().replace(":", "_"));

            GameRegistry.registerItem(
                guideItem,
                book.getRegistryName()
                    .getResourcePath().replace(":", "_"));
            GuideRegistry.setBookForStack(book, new ItemStack(guideItem));
        }
    }
}
