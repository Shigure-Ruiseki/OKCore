package ruiseki.okcore.tag.entry;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

public abstract class TagEntry<T> implements Predicate<TagEntry<?>>, Supplier<T> {

    public static final int WILDCARD = OreDictionary.WILDCARD_VALUE;

    @NotNull
    protected final ResourceLocation id;
    protected final int meta;

    protected TagEntry(@NotNull ResourceLocation id, int meta) {
        this.id = Objects.requireNonNull(id, "TagEntry ID cannot be null!");
        this.meta = meta;
    }

    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    public int getMeta() {
        return this.meta;
    }

    public abstract Class<T> getType();

    public abstract T get();

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
        return this.test(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.meta, this.getType());
    }

    @Override
    public String toString() {
        return this.id + ":" + (this.meta == WILDCARD ? "#wildcard" : this.meta);
    }
}
