package ruiseki.okcore.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Strings;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.Reference;
import ruiseki.okcore.event.BookEvent;
import ruiseki.okcore.guide.GuideRegistry;
import ruiseki.okcore.guide.IGuideItem;
import ruiseki.okcore.guide.IGuideLinked;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.GuideHelpers;
import ruiseki.okcore.helper.LangHelpers;

public class ItemGuideBook extends Item implements IGuideItem {

    @NotNull
    private final Book book;

    @SideOnly(Side.CLIENT)
    public IIcon pagesIcon;

    @SideOnly(Side.CLIENT)
    public IIcon customIcon;

    public ItemGuideBook(@NotNull Book book) {
        this.book = book;
        setUnlocalizedName("guide_book");
        setMaxStackSize(1);
        setCreativeTab(book.getCreativeTab());
        setHasSubtypes(true);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack heldStack, World world, EntityPlayer player) {
        BookEvent.Open event = new BookEvent.Open(book, heldStack, player);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            player.addChatComponentMessage(event.getCanceledText());
            return heldStack;
        }

        player.openGui(
            OKCore.instance,
            GuideHelpers.getIndexedBooks()
                .indexOf(book),
            world,
            (int) player.posX,
            (int) player.posY,
            (int) player.posZ);
        return heldStack;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {

        if (!player.isSneaking()) return false;
        Block block = world.getBlock(x, y, z);

        if (block instanceof IGuideLinked guideLinked) {
            ResourceLocation entryKey = guideLinked.getLinkedEntry(world, x, y, z, player, stack);
            if (entryKey == null) return false;
            for (CategoryAbstract category : book.getCategories()) {
                if (category.entries.containsKey(entryKey)) {
                    if (world.isRemote) {
                        GuiHelpers.openBookClient(book, category, category.entries.get(entryKey), player, stack);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return !Strings.isNullOrEmpty(book.getItemName()) ? LangHelpers.localize(getBook(stack).getItemName())
            : super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        if (!Strings.isNullOrEmpty(book.getAuthor())) list.add(LangHelpers.localize(book.getAuthor()));
        if (!Strings.isNullOrEmpty(book.getAuthor()) && flag) list.add(
            book.getRegistryName()
                .toString());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        itemIcon = ir.registerIcon(Reference.PREFIX_MOD + "book_cover");
        pagesIcon = ir.registerIcon(Reference.PREFIX_MOD + "book_pages");

        if (!Strings.isNullOrEmpty(book.itemTexture)) {
            this.customIcon = ir.registerIcon(book.itemTexture);
        }
    }

    @Override
    public int getRenderPasses(int metadata) {
        return requiresMultipleRenderPasses() ? 2 : 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return customIcon == null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass == 0 && customIcon == null) {
            if (book.getColor() != null) {
                return book.getColor().getRGB();
            }
        }
        return super.getColorFromItemStack(stack, pass);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        if (customIcon != null) return customIcon;
        return pass == 0 ? itemIcon : pagesIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int pass) {
        return getIconFromDamageForRenderPass(stack.getItemDamage(), pass);
    }

    @Override
    public Book getBook(ItemStack stack) {
        return book;
    }
}
