package ruiseki.okcore.tag.entry;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.INetworkSerializer;

public interface ITagEntrySerializer<T, E extends TagEntry<T>> extends INetworkSerializer<E> {

    E read(ResourceLocation id, int meta);

    default void toNetwork(ExtendedBuffer buffer, E entry) throws IOException {
        buffer.writeResourceLocation(entry.getId());
        buffer.writeVarIntToBuffer(entry.getMeta());
    }

    default E fromNetwork(ExtendedBuffer buffer) throws IOException {
        ResourceLocation id = buffer.readResourceLocation();
        int meta = buffer.readVarIntFromBuffer();

        return this.read(id, meta);
    }

    String getKey();
}
