package ruiseki.okcore.item.capability;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cofh.api.transport.IItemDuct;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.ItemStackHandler;
import ruiseki.okcore.item.capability.cofh.ItemDuctSink;
import ruiseki.okcore.item.capability.mfr.DSUItemSink;
import ruiseki.okcore.item.capability.mfr.DSUItemSource;
import ruiseki.okcore.item.capability.mfr.DeepStorageHandlerWrapper;
import ruiseki.okcore.item.capability.minecraft.InventoryHandlerWrapper;
import ruiseki.okcore.item.capability.minecraft.InventoryItemSink;
import ruiseki.okcore.item.capability.minecraft.InventoryItemSource;

@SuppressWarnings("unchecked")
public class CapabilityItemHandler implements IInitListener {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    @CapabilityInject(IItemSink.class)
    public static Capability<IItemSink> ITEM_SINK_CAPABILITY = null;

    @CapabilityInject(IItemSource.class)
    public static Capability<IItemSource> ITEM_SOURCE_CAPABILITY = null;

    public static final ResourceLocation INVENTORY_CAP = new ResourceLocation(Reference.MOD_ID, "inventory");

    @SubscribeEvent
    public void attachMCCapability(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity tile = event.getObject();

        boolean isInv = tile instanceof IInventory;
        boolean isDSU = tile instanceof IDeepStorageUnit;
        boolean isDuct = tile instanceof IItemDuct;

        if (isInv || isDSU || isDuct) {
            final IInventory finalInv = isInv ? (IInventory) tile : null;
            final IDeepStorageUnit finalDSU = isDSU ? (IDeepStorageUnit) tile : null;
            final IItemDuct finalDuct = isDuct ? (IItemDuct) tile : null;

            event.addCapability(INVENTORY_CAP, new ICapabilityProvider() {

                @Override
                public boolean hasCapability(@NotNull Capability<?> capability, @NotNull ForgeDirection facing) {
                    return capability == ITEM_HANDLER_CAPABILITY || capability == ITEM_SINK_CAPABILITY
                        || capability == ITEM_SOURCE_CAPABILITY;
                }

                @Override
                public <T> T getCapability(@NotNull Capability<T> capability, @NotNull ForgeDirection facing) {
                    if (!hasCapability(capability, facing)) return null;
                    if (finalInv != null) {
                        if (capability == ITEM_HANDLER_CAPABILITY) {
                            return (T) new InventoryHandlerWrapper(finalInv, facing);
                        }
                        if (capability == ITEM_SINK_CAPABILITY) {
                            return (T) new InventoryItemSink(finalInv, facing);
                        }
                        if (capability == ITEM_SOURCE_CAPABILITY) {
                            return (T) new InventoryItemSource(finalInv, facing);
                        }
                    }

                    if (finalDSU != null) {
                        if (capability == ITEM_HANDLER_CAPABILITY) {
                            return (T) new DeepStorageHandlerWrapper(finalDSU);
                        }
                        if (capability == ITEM_SINK_CAPABILITY) {
                            return (T) new DSUItemSink(finalDSU);
                        }
                        if (capability == ITEM_SOURCE_CAPABILITY) {
                            return (T) new DSUItemSource(finalDSU);
                        }
                    }

                    if (finalDuct != null) {
                        if (capability == ITEM_SINK_CAPABILITY) {
                            return (T) new ItemDuctSink(finalDuct, facing);
                        }
                    }
                    return null;
                }
            });
        }
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep != IInitListener.Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IItemHandler.class, new Capability.IStorage<IItemHandler>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IItemHandler> capability, IItemHandler instance,
                ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemHandler> capability, IItemHandler instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, ItemStackHandler::new);
        CapabilityManager.INSTANCE.register(IItemSink.class, new Capability.IStorage<IItemSink>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IItemSink> capability, IItemSink instance,
                ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemSink> capability, IItemSink instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, () -> new InventoryItemSink(null, null));
        CapabilityManager.INSTANCE.register(IItemSource.class, new Capability.IStorage<IItemSource>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IItemSource> capability, IItemSource instance,
                ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemSource> capability, IItemSource instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, () -> new InventoryItemSource(null, null));
        MinecraftForge.EVENT_BUS.register(this);
    }
}
