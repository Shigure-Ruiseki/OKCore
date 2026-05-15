package ruiseki.okcore.guide.capability;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.guide.NBTBookTags;

public class GuideHandler implements IGuideHandler, ICapabilitySerializable {

    private final Set<String> discoveredBooks = new HashSet<>();
    private String lastEntry = "";
    private int lastCategory = -1;
    private int lastPage = 0;

    public GuideHandler() {}

    @Override
    public void discoverBook(String bookId) {
        if (bookId != null) this.discoveredBooks.add(bookId);
    }

    @Override
    public boolean hasDiscovered(String bookId) {
        return discoveredBooks.contains(bookId);
    }

    @Override
    public Set<String> getDiscoveredBooks() {
        return discoveredBooks;
    }

    @Override
    public void setLastPos(String entry, int category, int page) {
        this.lastEntry = entry;
        this.lastCategory = category;
        this.lastPage = page;
    }

    @Override
    public String getLastEntry() {
        return lastEntry;
    }

    @Override
    public int getLastCategory() {
        return lastCategory;
    }

    @Override
    public int getLastPage() {
        return lastPage;
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        return capability == CapabilityGuide.GUIDE_CAPABILITY;
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable ForgeDirection facing) {
        return hasCapability(capability, facing) ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(NBTBookTags.ENTRY_TAG, lastEntry);
        tag.setInteger(NBTBookTags.CATEGORY_TAG, lastCategory);
        tag.setInteger(NBTBookTags.PAGE_TAG, lastPage);

        NBTTagList list = new NBTTagList();
        for (String s : discoveredBooks) {
            list.appendTag(new NBTTagString(s));
        }
        tag.setTag(NBTBookTags.BOOK_TAG, list);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        this.lastEntry = tag.getString(NBTBookTags.ENTRY_TAG);
        this.lastCategory = tag.getInteger(NBTBookTags.CATEGORY_TAG);
        this.lastPage = tag.getInteger(NBTBookTags.PAGE_TAG);

        this.discoveredBooks.clear();
        NBTTagList list = tag.getTagList(NBTBookTags.BOOK_TAG, 8);
        for (int i = 0; i < list.tagCount(); i++) {
            this.discoveredBooks.add(list.getStringTagAt(i));
        }
    }
}
