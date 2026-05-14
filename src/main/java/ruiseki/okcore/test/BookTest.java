package ruiseki.okcore.test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import ruiseki.okcore.Reference;
import ruiseki.okcore.config.ModConfig;
import ruiseki.okcore.guide.GuideBook;
import ruiseki.okcore.guide.IGuideBook;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.category.CategoryItemStack;
import ruiseki.okcore.guide.entry.EntryItemStack;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Entry;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.guide.pages.PageEntity;
import ruiseki.okcore.guide.pages.PageImage;
import ruiseki.okcore.guide.pages.PageItemStack;
import ruiseki.okcore.guide.pages.PageText;
import ruiseki.okcore.guide.pages.PageTextImage;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.PageHelpers;

@GuideBook
public class BookTest implements IGuideBook {

    public static Book book;

    @Override
    public boolean shouldRegister() {
        return ModConfig.useBookTest;
    }

    @Override
    public @Nullable Book buildBook() {
        book = new Book(new ResourceLocation(Reference.MOD_ID, "book_test"));
        book.setAuthor("Rui");
        book.setItemName("Display Name");
        book.setColor(Color.PINK);
        book.setTitle("Title message");
        book.setHeader("Hello there");

        Map<ResourceLocation, EntryAbstract> entries = new HashMap<>();
        List<CategoryAbstract> categories = new ArrayList<>();

        List<IPage> pages = Lists.newArrayList();
        pages.add(new PageText("Hello, this is\nsome text with a new line."));
        pages.add(
            new PageText(
                "Hello, this is some text without a new line. It is long so it should probably be automatically wrapped"));
        pages.addAll(
            PageHelpers.pagesForLongText(
                "Hello, this is some text. It is very long so it should be split across multiple pages. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua."));
        pages.addAll(
            PageHelpers.pagesForLongText(
                "Hello, this is some text. It is very long so it should be split across multiple pages. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua.",
                Blocks.coal_block));

        pages.add(new PageItemStack("These are all logs", "logWood")); // Tags.Items.NATURAL_LOGS
        pages.add(
            new PageTextImage(
                LangHelpers.localize("test.string"),
                new ResourceLocation(Reference.MOD_ID, "textures/test/testimage.png"),
                true));
        pages.add(
            new PageTextImage(
                LangHelpers.localize("test.string"),
                new ResourceLocation(Reference.MOD_ID, "textures/test/testimage.png"),
                true,
                64,
                64,
                true));
        pages.add(
            new PageTextImage(
                LangHelpers.localize("test.string"),
                new ResourceLocation(Reference.MOD_ID, "textures/test/testimage.png"),
                false));
        pages.add(
            new PageTextImage(
                LangHelpers.localize("test.string"),
                new ResourceLocation(Reference.MOD_ID, "textures/test/testimage.png"),
                false,
                64,
                64,
                true));
        pages.add(new PageImage(new ResourceLocation(Reference.MOD_ID, "textures/test/testimage.png")));
        pages.add(new PageImage(new ResourceLocation(Reference.MOD_ID, "textures/test/testimage.png"), 64, 64, true));

        pages.add(new PageEntity("Blaze"));
        pages.add(new PageEntity("Zombie", "This is a zombie") {

            @Override
            protected void prepareEntity(World world) {
                if (world != null && entity == null) {
                    EntityZombie zombie = new EntityZombie(world);
                    zombie.setCurrentItemOrArmor(0, new ItemStack(Items.iron_sword));
                    this.entity = zombie;
                }
            }
        });

        Entry entry = new EntryItemStack(pages, LangHelpers.localize("test.entry"), new ItemStack(Items.potato));
        entries.put(new ResourceLocation(Reference.MOD_ID, "entry"), entry);

        categories.add(
            new CategoryItemStack(entries, LangHelpers.localize("test.category"), new ItemStack(Items.wooden_door)));
        categories
            .add(new CategoryItemStack(entries, LangHelpers.localize("test.category"), new ItemStack(Blocks.pumpkin)));
        categories.add(
            new CategoryItemStack(entries, LangHelpers.localize("test.category"), new ItemStack(Items.wooden_axe)));
        categories
            .add(new CategoryItemStack(entries, LangHelpers.localize("test.category"), new ItemStack(Blocks.log)));
        categories
            .add(new CategoryItemStack(entries, LangHelpers.localize("test.category"), new ItemStack(Items.bone)));
        categories
            .add(new CategoryItemStack(entries, LangHelpers.localize("test.category"), new ItemStack(Items.wheat)));
        for (CategoryAbstract category : categories) {
            book.addCategory(category);
        }

        return book;
    }
}
