package ruiseki.okcore.helper;

import java.io.IOException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public class PacketHelpers {

    private PacketHelpers() {}

    /**
     * Most ItemStack serialization is Server to Client, and must go through PacketBuffer.writeItemStack which uses
     * Item.getNBTShareTag.
     * One exception is items from the creative menu, which must be sent from Client to Server with their full NBT.
     * <br/>
     * This method matches PacketBuffer.writeItemStack but without the Item.getNBTShareTag patch.
     * It is compatible with PacketBuffer.readItemStack.
     */
    public static void writeItemStackFromClientToServer(PacketBuffer buffer, ItemStack stack) throws IOException {
        if (stack == null) {
            buffer.writeShort(-1);
        } else {
            buffer.writeShort(Item.getIdFromItem(stack.getItem()));
            buffer.writeByte(stack.stackSize);
            buffer.writeShort(stack.getItemDamage());
            NBTTagCompound nbttagcompound = null;

            if (stack.getItem()
                .isDamageable()
                || stack.getItem()
                    .getShareTag()) {
                nbttagcompound = stack.getTagCompound();
            }

            buffer.writeNBTTagCompoundToBuffer(nbttagcompound);
        }
    }
}
