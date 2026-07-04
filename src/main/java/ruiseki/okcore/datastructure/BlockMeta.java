package ruiseki.okcore.datastructure;

import java.util.Objects;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * A mutable implementation of {@link IImmutableBlockMeta}. If your API should return a mutable pair, return this
 * instead. Must follow the same contracts as the immutable version if this is ever upcast to a
 * {@link IImmutableBlockMeta} in your API. If this type is exposed instead of the immutable interface, assume that the
 * contained values can change.
 */
public class BlockMeta implements IImmutableBlockMeta {

    @Nonnull
    private Block block;
    private int meta;

    public BlockMeta(@Nonnull Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public BlockMeta(@Nonnull Block block) {
        this(block, 0);
    }

    public BlockMeta(IImmutableBlockMeta bm) {
        this(bm.getBlock(), bm.getBlockMeta());
    }

    @Override
    public @NotNull Block getBlock() {
        return this.block;
    }

    @Override
    public int getBlockMeta() {
        return this.meta;
    }

    @Deprecated
    public int getMeta() {
        return getBlockMeta();
    }

    /**
     * Note: see the header comment in {@link IImmutableBlockMeta} for this method's contract.
     */
    public BlockMeta setBlock(@Nonnull Block block) {
        this.block = Objects.requireNonNull(block);

        return this;
    }

    /**
     * Note: see the header comment in {@link IImmutableBlockMeta} for this method's contract.
     */
    public BlockMeta setBlockMeta(int meta) {
        this.meta = meta;

        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + block.hashCode();
        result = prime * result + meta;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        BlockMeta other = (BlockMeta) obj;
        if (!this.getBlock()
            .equals(other.getBlock())) return false;
        return this.getBlockMeta() == other.getBlockMeta();
    }

    @Override
    public String toString() {
        return "BlockMeta [block=" + block + ", meta=" + meta + "]";
    }
}
