package ruiseki.okcore.helper;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class PacketHelpers {

    private static final ThreadLocal<Boolean> WRITING_CLIENT_TO_SERVER_ITEM_STACK = ThreadLocal
        .withInitial(() -> false);

    private PacketHelpers() {}

    /**
     * Most ItemStack serialization is Server to Client, and must go through PacketBuffer.writeItemStack which uses
     * Item.getNBTShareTag.
     * One exception is items from the creative menu, which must be sent from Client to Server with their full NBT.
     * <br/>
     * This method keeps PacketBuffer.writeItemStackToBuffer available to other protocol mixins, but tells OKCore's
     * share-tag mixin to use the full tag.
     */
    public static void writeItemStackFromClientToServer(PacketBuffer buffer, ItemStack stack) throws IOException {
        if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
            buffer.writeShort(-1);
            return;
        }

        WRITING_CLIENT_TO_SERVER_ITEM_STACK.set(true);

        try {
            buffer.writeItemStackToBuffer(stack);
        } finally {
            WRITING_CLIENT_TO_SERVER_ITEM_STACK.set(false);
        }
    }

    public static boolean isWritingClientToServerItemStack() {
        return WRITING_CLIENT_TO_SERVER_ITEM_STACK.get();
    }
}
