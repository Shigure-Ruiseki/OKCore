package ruiseki.okcore.nbt.path.parse;

import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import ruiseki.okcore.nbt.path.INbtPathExpression;
import ruiseki.okcore.nbt.path.NbtPathExpressionMatches;

/**
 * A handler that handles child path expressions for ".length",
 * to retrieve the length of lists and tag compounds.
 */
public class NbtPathExpressionParseHandlerLength implements INbtPathExpressionParseHandler {

    @Nullable
    @Override
    public HandleResult handlePrefixOf(String nbtPathExpression, int pos) {
        if (!nbtPathExpression.substring(pos, Math.min(pos + 7, nbtPathExpression.length()))
            .equals(".length")) {
            return HandleResult.INVALID;
        }

        return new HandleResult(Expression.INSTANCE, 7);
    }

    public static class Expression implements INbtPathExpression {

        public static final NbtPathExpressionParseHandlerLength.Expression INSTANCE = new NbtPathExpressionParseHandlerLength.Expression();

        @Override
        public NbtPathExpressionMatches matchContexts(Stream<NbtPathExpressionExecutionContext> executionContexts) {
            return new NbtPathExpressionMatches(executionContexts.map(executionContext -> {
                NBTBase nbt = executionContext.getCurrentTag();
                if (nbt.getId() == Constants.NBT.TAG_LIST) {
                    NBTTagList tag = (NBTTagList) nbt;
                    return new NbtPathExpressionExecutionContext(new NBTTagInt(tag.tagCount()), executionContext);
                } else if (nbt.getId() == Constants.NBT.TAG_COMPOUND) {
                    NBTTagCompound tag = (NBTTagCompound) nbt;
                    return new NbtPathExpressionExecutionContext(new NBTTagInt(tag.tagMap.size()), executionContext);
                }
                return null;
            })
                .filter(Objects::nonNull));
        }

    }
}
