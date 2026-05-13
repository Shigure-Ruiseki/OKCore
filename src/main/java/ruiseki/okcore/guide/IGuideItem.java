package ruiseki.okcore.guide;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.guide.impl.Book;

public interface IGuideItem {

    Book getBook(ItemStack stack);
}
