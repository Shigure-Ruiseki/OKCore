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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.capability.wrapper.InventoryHandlerWrapper;

@NotNullByDefault
@Mixin(TileEntityChest.class)
@Implements(@Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"))
public abstract class MixinTileEntityChest extends MixinTileEntity implements ICapabilitySerializable {

    @Unique
    private final LazyOptional<IItemHandler>[] okcore$itemWrappers = new LazyOptional[7];

    @Inject(method = "<init>", at = @At("RETURN"))
    private void okcore$init(CallbackInfo ci) {
        for (int i = 0; i < okcore$itemWrappers.length; i++) {
            okcore$itemWrappers[i] = LazyOptional.empty();
        }
    }

    @Unique
    private int okcore$getDirectionIndex(@Nullable ForgeDirection facing) {
        return facing == null || facing == ForgeDirection.UNKNOWN ? 6 : facing.ordinal();
    }

    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> okcorecap$getCapability(Capability<T> capability, @Nullable ForgeDirection facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            int idx = okcore$getDirectionIndex(facing);
            LazyOptional<IItemHandler> cachedCap = okcore$itemWrappers[idx];

            if (!cachedCap.isPresent()) {
                TileEntityChest self = (TileEntityChest) (Object) this;
                Block block = self.getBlockType();

                if (block instanceof BlockChest chest) {
                    IInventory inventory = chest
                        .func_149951_m(self.getWorldObj(), self.xCoord, self.yCoord, self.zCoord);
                    if (inventory != null) {
                        ForgeDirection dir = facing == null ? ForgeDirection.UNKNOWN : facing;
                        cachedCap = LazyOptional.of(() -> new InventoryHandlerWrapper(inventory, dir));
                        okcore$itemWrappers[idx] = cachedCap;
                    }
                }
            }

            if (cachedCap.isPresent()) {
                return cachedCap.cast();
            }
        }

        return super.okcorecap$getCapability(capability, facing);
    }

    @Inject(method = "invalidate", at = @At("RETURN"))
    private void okcore$invalidate(CallbackInfo ci) {
        for (int i = 0; i < okcore$itemWrappers.length; i++) {
            if (okcore$itemWrappers[i] != null) {
                okcore$itemWrappers[i].invalidate();
                okcore$itemWrappers[i] = LazyOptional.empty();
            }
        }
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        return super.okcorecap$serializeNBT();
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        super.okcorecap$deserializeNBT(tag);
    }
}
