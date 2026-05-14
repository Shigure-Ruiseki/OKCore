package ruiseki.okcore.guide.impl;

import java.awt.Color;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import lombok.Data;
import ruiseki.okcore.Reference;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;

@Data
public class Book {

    private final ResourceLocation registryName;
    private final List<CategoryAbstract> categories = Lists.newArrayList();
    private String title = "item.book.name";
    private String header;
    private String itemName;
    private String author;
    private ResourceLocation pageTexture = new ResourceLocation(Reference.PREFIX_GUI + "book_colored.png");
    private ResourceLocation outlineTexture = new ResourceLocation(Reference.PREFIX_GUI + "book_greyscale.png");
    public String itemTexture;
    private Color color = new Color(171, 70, 30);
    private boolean spawnWithBook;
    private CreativeTabs creativeTab = CreativeTabs.tabMisc;

    /**
     *
     * @param registryName The registry name for the book to build. Should use your modid as the domain.
     */
    public Book(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    /**
     * Adds a new {@link CategoryAbstract} to this book. You should either pre-build or keep a reference of this
     * category
     * so you may populate it with entries.
     *
     * Categories are displayed in the order they are added.
     *
     * @param category The category to add to this book.
     * @return the builder instance for chaining.
     */
    public Book addCategory(CategoryAbstract category) {
        category.setBook(this);
        this.categories.add(category);
        return this;
    }

    /**
     * Sets the title of this book to be displayed in the GUI.
     *
     * @param guideTitle The title of this guide.
     * @return the builder instance for chaining.
     */
    public Book setTitle(String guideTitle) {
        this.title = guideTitle;
        return this;
    }

    /**
     * Sets the header text of this book. The header is displayed at the top of the home page above the category
     * listing.
     *
     * By default, this is the same as {@link #title}.
     *
     * @param header The header text to display.
     * @return the builder instance for chaining.
     */
    public Book setHeader(String header) {
        this.header = header;
        return this;
    }

    public void setItemTexture(String itemTexture) {
        this.itemTexture = itemTexture;
    }

    /**
     * Sets the unlocalized name for the item containing this book.
     *
     * By default, this is the same as {@link #title}.
     *
     * @param itemName The unlocalized name for this item.
     * @return the builder instance for chaining.
     */
    public Book setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    /**
     * The author of this book. If your books are lore-heavy, using an actual author name is acceptable. If not, you can
     * just use your mod name.
     *
     * By default, this uses the name of the mod container obtained from looking up the domain of {@link #registryName}.
     *
     * @param author The author of this book.
     * @return the builder instance for chaining.
     */
    public Book setAuthor(String author) {
        this.author = author;
        return this;
    }

    /**
     * The texture to use for the pages themselves. These are un-colored and drawn just how they appear in the texture
     * file.
     * The dimensions should remain the same as the default texture.
     *
     * By default, this uses the same page texture as vanilla books.
     *
     * @param pageTexture The page texture to use for this guide.
     * @return the builder instance for chaining.
     */
    public Book setPageTexture(ResourceLocation pageTexture) {
        this.pageTexture = pageTexture;
        return this;
    }

    /**
     * The texture to use for the border of the book. These are colored with {@link #color}. The dimensions should
     * remain
     * the same as the default texture.
     *
     * By default, this uses a greyscale version of the outline of vanilla books.
     *
     * @param outlineTexture The outline texture to use for this guide.
     * @return the builder instance for chaining.
     */
    public Book setOutlineTexture(ResourceLocation outlineTexture) {
        this.outlineTexture = outlineTexture;
        return this;
    }

    /**
     * Sets the color to overlay on the book model and GUI border.
     *
     * By default, this is a reddish-brown color.
     *
     * @param color The color to overlay with.
     * @return the builder instance for chaining.
     */
    public Book setColor(Color color) {
        this.color = color;
        return this;
    }

    /**
     * An overload that takes an RGB color instead of a {@link Color} instance.
     *
     * @see #setColor(int)
     *
     * @param color The color to overlay with.
     * @return the builder instance for chaining.
     */
    public Book setColor(int color) {
        return setColor(new Color(color));
    }

    /**
     * Sets the default config option for whether new players should spawn with this book in their inventory. Players
     * may
     * override this in the config if they wish.
     *
     * By default, books will not spawn in the player's inventory.
     *
     * @return the builder instance for chaining.
     */
    public Book setSpawnWithBook() {
        this.spawnWithBook = true;
        return this;
    }

    /**
     * Sets the Creative Tab this book should appear in.
     *
     * By default, all books will appear in {@link CreativeTabs#tabMisc}.
     *
     * @param creativeTab The creative tab this book should display in.
     * @return the builder instance for chaining.
     */
    public Book setCreativeTab(CreativeTabs creativeTab) {
        this.creativeTab = creativeTab;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(
            "categoryList",
            Joiner.on(", ")
                .join(categories))
            .append("title", title)
            .append("header", header)
            .append("itemName", itemName)
            .append("author", author)
            .append("pageTexture", pageTexture)
            .append("outlineTexture", outlineTexture)
            .append("color", color)
            .append("spawnWithBook", spawnWithBook)
            .append("registryName", registryName)
            .append("creativeTab", creativeTab)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;

        return getRegistryName().equals(book.getRegistryName());

    }

    @Override
    public int hashCode() {
        return getRegistryName().hashCode();
    }

}
