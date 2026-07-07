package ruiseki.okcore.guide;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.capability.CapabilityGuide;
import ruiseki.okcore.guide.capability.IGuideHandler;
import ruiseki.okcore.guide.gui.GuiCategory;
import ruiseki.okcore.guide.gui.GuiEntry;
import ruiseki.okcore.guide.gui.GuiHome;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.GuideHelpers;

public class GuideGuiHandler implements IGuiHandler {

    public GuideGuiHandler() {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        IGuideHandler cap = CapabilityHelpers.getCapability(player, CapabilityGuide.GUIDE_CAPABILITY)
            .resolveOrNull();
        if (cap == null) return null;

        Book book = GuideHelpers.getIndexedBooks()
            .get(ID);
        if (book == null) return null;

        try {
            String lastEntry = cap.getLastEntry();
            int lastCategoryIdx = cap.getLastCategory();

            if (lastCategoryIdx >= 0 && lastCategoryIdx < book.getCategories()
                .size()) {
                CategoryAbstract category = book.getCategories()
                    .get(lastCategoryIdx);

                if (lastEntry != null && !lastEntry.isEmpty()
                    && category.entries.containsKey(new ResourceLocation(lastEntry))) {
                    EntryAbstract entry = category.entries.get(new ResourceLocation(lastEntry));
                    return new GuiEntry(book, category, entry, player);
                }

                return new GuiCategory(book, category, player, null);
            }
        } catch (Exception ignore) {}

        return new GuiHome(book, player);
    }
}
