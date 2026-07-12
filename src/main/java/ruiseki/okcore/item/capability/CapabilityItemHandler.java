package ruiseki.okcore.item.capability;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.capability.wrapper.ItemHandlerWrapper;

public class CapabilityItemHandler implements IInitListener {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER = null;

    @SubscribeEvent
    public void attachMCTECapability(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity tile = event.getObject();
        if (tile instanceof IInventory inventory) {
            event.addCapability(
                new ResourceLocation(Reference.MOD_ID, "item_handler_wrapper"),
                new ItemHandlerWrapper(inventory));
        }
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep != IInitListener.Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IItemHandler.class);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
