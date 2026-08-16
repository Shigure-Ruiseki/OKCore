package ruiseki.okcore.nbt.path;

import java.util.stream.Stream;

import net.minecraft.nbt.NBTBase;

import ruiseki.okcore.nbt.path.parse.NbtPathExpressionExecutionContext;

/**
 * A data object representing the result of executing
 * an NBT path expression against an NBT tag.
 */
public class NbtPathExpressionMatches {

    public static NbtPathExpressionMatches EMPTY = NbtPathExpressionMatches.forAll();

    private final Stream<NbtPathExpressionExecutionContext> matches;

    public NbtPathExpressionMatches(Stream<NbtPathExpressionExecutionContext> matches) {
        this.matches = matches;
    }

    public Stream<NbtPathExpressionExecutionContext> getContexts() {
        return matches;
    }

    public Stream<NBTBase> getMatches() {
        return getContexts().map(NbtPathExpressionExecutionContext::getCurrentTag);
    }

    public static NbtPathExpressionMatches forAll(NbtPathExpressionExecutionContext... nbts) {
        return new NbtPathExpressionMatches(Stream.of(nbts));
    }

}
