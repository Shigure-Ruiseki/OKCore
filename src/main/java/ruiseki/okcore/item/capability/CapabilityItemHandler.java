package ruiseki.okcore.item.capability;

import net.minecraft.inventory.IInventory;
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
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.item.IItemHandler;
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

                private final LazyOptional<IItemHandler>[] invHandlerCache = new LazyOptional[7];
                private final LazyOptional<IItemSink>[] invSinkCache = new LazyOptional[7];
                private final LazyOptional<IItemSource>[] invSourceCache = new LazyOptional[7];

                private final LazyOptional<IItemHandler>[] dsuHandlerCache = new LazyOptional[7];
                private final LazyOptional<IItemSink>[] dsuSinkCache = new LazyOptional[7];
                private final LazyOptional<IItemSource>[] dsuSourceCache = new LazyOptional[7];

                private final LazyOptional<IItemSink>[] ductSinkCache = new LazyOptional[7];

                private int getIndex(@Nullable ForgeDirection facing) {
                    return facing == null ? 6 : facing.ordinal();
                }

                @Override
                public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                    @Nullable ForgeDirection facing) {
                    int idx = getIndex(facing);

                    if (finalInv != null) {
                        if (capability == ITEM_HANDLER_CAPABILITY) {
                            if (invHandlerCache[idx] == null) {
                                invHandlerCache[idx] = LazyOptional
                                    .of(() -> new InventoryHandlerWrapper(finalInv, facing));
                            }
                            return invHandlerCache[idx].cast();
                        }
                        if (capability == ITEM_SINK_CAPABILITY) {
                            if (invSinkCache[idx] == null) {
                                invSinkCache[idx] = LazyOptional.of(() -> new InventoryItemSink(finalInv, facing));
                            }
                            return invSinkCache[idx].cast();
                        }
                        if (capability == ITEM_SOURCE_CAPABILITY) {
                            if (invSourceCache[idx] == null) {
                                invSourceCache[idx] = LazyOptional.of(() -> new InventoryItemSource(finalInv, facing));
                            }
                            return invSourceCache[idx].cast();
                        }
                    }

                    if (finalDSU != null) {
                        if (capability == ITEM_HANDLER_CAPABILITY) {
                            if (dsuHandlerCache[idx] == null) {
                                dsuHandlerCache[idx] = LazyOptional.of(() -> new DeepStorageHandlerWrapper(finalDSU));
                            }
                            return dsuHandlerCache[idx].cast();
                        }
                        if (capability == ITEM_SINK_CAPABILITY) {
                            if (dsuSinkCache[idx] == null) {
                                dsuSinkCache[idx] = LazyOptional.of(() -> new DSUItemSink(finalDSU));
                            }
                            return dsuSinkCache[idx].cast();
                        }
                        if (capability == ITEM_SOURCE_CAPABILITY) {
                            if (dsuSourceCache[idx] == null) {
                                dsuSourceCache[idx] = LazyOptional.of(() -> new DSUItemSource(finalDSU));
                            }
                            return dsuSourceCache[idx].cast();
                        }
                    }

                    if (finalDuct != null) {
                        if (capability == ITEM_SINK_CAPABILITY) {
                            if (ductSinkCache[idx] == null) {
                                ductSinkCache[idx] = LazyOptional.of(() -> new ItemDuctSink(finalDuct, facing));
                            }
                            return ductSinkCache[idx].cast();
                        }
                    }

                    return LazyOptional.empty();
                }
            });
        }
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep != IInitListener.Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IItemHandler.class);
        CapabilityManager.INSTANCE.register(IItemSink.class);
        CapabilityManager.INSTANCE.register(IItemSource.class);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
