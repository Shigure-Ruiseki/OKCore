package ruiseki.okcore.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.okcore.guide.impl.Book;

/**
 * Base class for all {@link Book} related events.
 *
 * {@link #book} is the book being opened.
 * {@link #stack} is the ItemStack of the Book.
 * {@link #player} is the player opening the book.
 */
public class BookEvent extends Event {

    private final Book book;
    private final ItemStack stack;
    private final EntityPlayer player;

    protected BookEvent(Book book, ItemStack stack, EntityPlayer player) {
        this.book = book;
        this.stack = stack;
        this.player = player;
    }

    public Book getBook() {
        return book;
    }

    public ItemStack getStack() {
        return stack;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    /**
     * Called whenever a book is opened.
     *
     * {@link #canceledText} is a status message sent to the player when the book fails to open.
     */
    @Cancelable
    public static class Open extends BookEvent {

        private static final IChatComponent DEFAULT_CANCEL = new ChatComponentTranslation("text.open.failed")
            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));

        private IChatComponent canceledText = DEFAULT_CANCEL;

        public Open(Book book, ItemStack stack, EntityPlayer player) {
            super(book, stack, player);
        }

        @NotNull
        public IChatComponent getCanceledText() {
            return canceledText;
        }

        public void setCanceledText(@NotNull IChatComponent canceledText) {
            this.canceledText = canceledText;
        }
    }
}
