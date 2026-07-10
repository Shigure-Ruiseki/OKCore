package ruiseki.okcore.tag;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import ruiseki.okcore.network.ExtendedBuffer;

public record TagEntry(ResourceLocation id, int meta) {

    public static final int WILDCARD = OreDictionary.WILDCARD_VALUE;

    public static TagEntry create(ResourceLocation id, int meta) {
        return new TagEntry(id, meta);
    }

    public void toNetwork(ExtendedBuffer buffer) {
        buffer.writeResourceLocation(id);
        buffer.writeVarIntToBuffer(meta);
    }

    public static TagEntry fromNetwork(ExtendedBuffer buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        int meta = buffer.readVarIntFromBuffer();
        return create(id, meta);
    }
}
