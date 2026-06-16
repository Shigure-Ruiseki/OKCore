package ruiseki.okcore.guide.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.event.guide.BookEvent;
import ruiseki.okcore.event.inventory.InventoryChangedEvent;
import ruiseki.okcore.guide.IGuideItem;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.inventory.ItemStackKey;
import ruiseki.okcore.persist.nbt.INBTSerializable;

public class CapabilityGuide implements IInitListener {

    @CapabilityInject(IGuideHandler.class)
    public static Capability<IGuideHandler> GUIDE_CAPABILITY = null;

    public static final ResourceLocation GUIDE_CAP = new ResourceLocation(Reference.MOD_ID, "guide");

    @SubscribeEvent
    public void attachGuideCapability(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (!(entity instanceof EntityPlayer)) return;

        event.addCapability(GUIDE_CAP, new GuideHandler());
    }

    @SubscribeEvent
    public void onPlayerDiscoverBook(BookEvent.Open event) {
        EntityPlayer player = event.player;
        IGuideHandler handler = EntityHelpers.getCapability(player, GUIDE_CAPABILITY, null);
        if (handler != null) handler.discoverBook(
            event.book.getRegistryName()
                .toString());
    }

    @SubscribeEvent
    public void onPlayerPickupBook(InventoryChangedEvent.Entered event) {
        EntityPlayer player = event.player;
        IGuideHandler handler = EntityHelpers.getCapability(player, GUIDE_CAPABILITY, null);
        if (handler == null) return;
        for (Object2IntMap.Entry<ItemStackKey> entry : event.changes.object2IntEntrySet()) {
            ItemStackKey key = entry.getKey();
            if (!(key.getItem() instanceof IGuideItem guideItem)) return;
            Book book = guideItem.getBook();
            if (book != null) {
                String bookId = book.getRegistryName()
                    .toString();
                if (!handler.hasDiscovered(bookId)) {
                    boolean isCanceled = MinecraftForge.EVENT_BUS.post(new BookEvent.Discover(book, player));
                    if (!isCanceled) handler.discoverBook(bookId);
                }
            }
        }
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep != Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IGuideHandler.class, new Capability.IStorage<>() {

            @Override
            public NBTBase writeNBT(Capability<IGuideHandler> capability, IGuideHandler instance, ForgeDirection side) {
                if (instance instanceof INBTSerializable serializable) return serializable.serializeNBT();
                return null;
            }

            @Override
            public void readNBT(Capability<IGuideHandler> capability, IGuideHandler instance, ForgeDirection side,
                NBTBase nbt) {
                if (instance instanceof INBTSerializable serializable)
                    serializable.deserializeNBT((NBTTagCompound) nbt);
            }
        }, GuideHandler::new);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
