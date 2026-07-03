package ruiseki.okcore.tag.entry;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.network.ExtendedBuffer;

public interface ITagEntrySerializer<T, E extends TagEntry<T>> {

    E read(ResourceLocation id, int meta);

    default E read(ExtendedBuffer buffer) throws IOException {
        ResourceLocation id = buffer.readResourceLocation();
        int meta = buffer.readVarIntFromBuffer();

        return this.read(id, meta);
    }

    String getKey();
}
