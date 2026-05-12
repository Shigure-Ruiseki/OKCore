package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.capability.minecraft.InventoryHandlerWrapper;
import ruiseki.okcore.item.capability.minecraft.InventoryItemSink;
import ruiseki.okcore.item.capability.minecraft.InventoryItemSource;

@Mixin(TileEntityChest.class)
@Implements(@Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"))
public abstract class MixinTileEntityChest extends MixinTileEntity {

    public <T> T okcorecap$getCapability(Capability<T> capability, ForgeDirection facing) {
        TileEntityChest self = (TileEntityChest) (Object) this;
        Block block = self.getBlockType();
        if (block instanceof BlockChest chest) {
            IInventory inventory = chest.func_149951_m(self.getWorldObj(), self.xCoord, self.yCoord, self.zCoord);
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                return (T) new InventoryHandlerWrapper(inventory, facing);
            if (capability == CapabilityItemHandler.ITEM_SINK_CAPABILITY)
                return (T) new InventoryItemSink(inventory, facing);
            if (capability == CapabilityItemHandler.ITEM_SOURCE_CAPABILITY)
                return (T) new InventoryItemSource(inventory, facing);
        }
        return super.okcorecap$getCapability(capability, facing);
    }

    public boolean okcorecap$hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            || capability == CapabilityItemHandler.ITEM_SINK_CAPABILITY
            || capability == CapabilityItemHandler.ITEM_SOURCE_CAPABILITY) return true;
        return super.okcorecap$hasCapability(capability, facing);
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        return super.okcorecap$serializeNBT();
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        super.okcorecap$deserializeNBT(tag);
    }
}
