package ruiseki.okcore.tag.entry;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Predicate;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.INetworkMaterial;
import ruiseki.okcore.tag.ResourceKey;

public abstract class TagEntry<T> implements Predicate<TagEntry<?>>, INetworkMaterial {

    public static final int WILDCARD = OreDictionary.WILDCARD_VALUE;

    protected ResourceLocation id;
    protected int meta;

    protected TagEntry(ResourceLocation id, int meta) {
        this.id = id;
        this.meta = meta;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public int getMeta() {
        return this.meta;
    }

    public abstract Class<T> getType();

    public abstract String getKey();

    public abstract ResourceKey<?> getRegistryKey();

    public abstract TagEntry<T> create(ResourceLocation id, int meta);

    public TagEntry<T> empty() {
        return this.create(new ResourceLocation("minecraft", "air"), 0);
    }

    public abstract T to();

    @Override
    public void toNetwork(ExtendedBuffer buffer) throws IOException {
        buffer.writeResourceLocation(id);
        buffer.writeVarIntToBuffer(meta);
    }

    @Override
    public void fromNetwork(ExtendedBuffer buffer) throws IOException {
        this.id = buffer.readResourceLocation();
        this.meta = buffer.readVarIntFromBuffer();
    }

    @Override
    public boolean test(TagEntry<?> other) {
        if (other == null) return false;
        if (this.getType() != other.getType()) return false;

        if (!this.id.equals(other.id)) return false;
        return this.meta == WILDCARD || other.meta == WILDCARD || this.meta == other.meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagEntry<?>other)) return false;
        return this.getType() == other.getType() && this.meta == other.meta && this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.meta, this.getType());
    }
}
