package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.capability.minecraft.InventoryHandlerWrapper;
import ruiseki.okcore.item.capability.minecraft.InventoryItemSink;
import ruiseki.okcore.item.capability.minecraft.InventoryItemSource;

@NotNullByDefault
@Mixin(TileEntityChest.class)
@Implements(@Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"))
public abstract class MixinTileEntityChest extends MixinTileEntity implements ICapabilitySerializable {

    @Unique
    private final LazyOptional<?>[] okcore$itemHandlers = new LazyOptional<?>[7];

    @Unique
    private int okcore$getDirectionIndex(@Nullable ForgeDirection facing) {
        return facing == null ? 6 : facing.ordinal();
    }

    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> okcorecap$getCapability(Capability<T> capability, @Nullable ForgeDirection facing) {
        TileEntityChest self = (TileEntityChest) (Object) this;
        Block block = self.getBlockType();

        if (block instanceof BlockChest chest) {
            IInventory inventory = chest.func_149951_m(self.getWorldObj(), self.xCoord, self.yCoord, self.zCoord);
            if (inventory != null) {
                int idx = okcore$getDirectionIndex(facing);

                if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
                    if (okcore$itemHandlers[idx] == null) {
                        okcore$itemHandlers[idx] = LazyOptional
                            .of(() -> new InventoryHandlerWrapper(inventory, facing));
                    }
                    return (LazyOptional<T>) okcore$itemHandlers[idx];
                }

                if (capability == CapabilityItemHandler.ITEM_SINK_CAPABILITY) {
                    return (LazyOptional<T>) LazyOptional.of(() -> new InventoryItemSink(inventory, facing));
                }
                if (capability == CapabilityItemHandler.ITEM_SOURCE_CAPABILITY) {
                    return (LazyOptional<T>) LazyOptional.of(() -> new InventoryItemSource(inventory, facing));
                }
            }
        }

        return super.okcorecap$getCapability(capability, facing);
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        return super.okcorecap$serializeNBT();
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        super.okcorecap$deserializeNBT(tag);
    }
}
