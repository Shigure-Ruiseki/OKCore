package ruiseki.okcore.item.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import ruiseki.okcore.item.PlayerArmorInvWrapper;
import ruiseki.okcore.item.PlayerInvWrapper;
import ruiseki.okcore.item.PlayerMainInvWrapper;
import ruiseki.okcore.item.capability.cofh.ItemDuctSink;
import ruiseki.okcore.item.capability.mfr.DSUItemSink;
import ruiseki.okcore.item.capability.mfr.DSUItemSource;
import ruiseki.okcore.item.capability.mfr.DeepStorageHandlerWrapper;
import ruiseki.okcore.item.capability.minecraft.InventoryHandlerWrapper;

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
    public void attachMCTECapability(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity tile = event.getObject();

        boolean isInv = tile instanceof IInventory;
        boolean isDSU = tile instanceof IDeepStorageUnit;
        boolean isDuct = tile instanceof IItemDuct;

        if (isInv || isDSU || isDuct) {
            final IInventory finalInv = isInv ? (IInventory) tile : null;
            final IDeepStorageUnit finalDSU = isDSU ? (IDeepStorageUnit) tile : null;
            final IItemDuct finalDuct = isDuct ? (IItemDuct) tile : null;

            event.addCapability(INVENTORY_CAP, new ICapabilityProvider() {

                private final LazyOptional<InventoryHandlerWrapper>[] invCache = new LazyOptional[7];

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
                        if (capability == ITEM_HANDLER_CAPABILITY || capability == ITEM_SINK_CAPABILITY
                            || capability == ITEM_SOURCE_CAPABILITY) {

                            if (invCache[idx] == null) {
                                invCache[idx] = LazyOptional.of(() -> new InventoryHandlerWrapper(finalInv, facing));
                            }
                            return invCache[idx].cast();
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

    @SubscribeEvent
    public void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();

        if (entity instanceof EntityPlayer player) {

            event.addCapability(INVENTORY_CAP, new ICapabilityProvider() {

                private final LazyOptional<IItemHandler>[] entityCache = new LazyOptional[7];
                private final IItemHandler playerMainHandler = new PlayerMainInvWrapper(player.inventory);
                private final IItemHandler playerEquipmentHandler = new PlayerArmorInvWrapper(player.inventory);
                private final IItemHandler playerJoinedHandler = new PlayerInvWrapper(player.inventory);

                private int getIndex(@Nullable ForgeDirection facing) {
                    return facing == null ? 6 : facing.ordinal();
                }

                @Override
                public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                    @Nullable ForgeDirection facing) {
                    if (capability == ITEM_HANDLER_CAPABILITY || capability == ITEM_SINK_CAPABILITY
                        || capability == ITEM_SOURCE_CAPABILITY) {
                        int idx = getIndex(facing);
                        if (entityCache[idx] == null) {
                            if (facing == null) {
                                entityCache[idx] = LazyOptional.of(() -> playerJoinedHandler);
                            } else if (facing == ForgeDirection.UP || facing == ForgeDirection.DOWN) {
                                entityCache[idx] = LazyOptional.of(() -> playerMainHandler);
                            } else {
                                entityCache[idx] = LazyOptional.of(() -> playerEquipmentHandler);
                            }
                        }
                        return entityCache[idx].cast();
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
