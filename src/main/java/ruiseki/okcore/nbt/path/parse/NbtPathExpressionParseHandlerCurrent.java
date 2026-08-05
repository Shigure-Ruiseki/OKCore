package ruiseki.okcore.nbt.path.parse;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import ruiseki.okcore.nbt.path.INbtPathExpression;
import ruiseki.okcore.nbt.path.NbtPathExpressionMatches;

/**
 * A handler that picks the current position in the NBT tree via "@".
 */
public class NbtPathExpressionParseHandlerCurrent implements INbtPathExpressionParseHandler {

    @Nullable
    @Override
    public HandleResult handlePrefixOf(String nbtPathExpression, int pos) {
        if (nbtPathExpression.charAt(pos) != '@') {
            return HandleResult.INVALID;
        }

        return new HandleResult(Expression.INSTANCE, 1);
    }

    public static class Expression implements INbtPathExpression {

        public static final Expression INSTANCE = new Expression();

        @Override
        public NbtPathExpressionMatches matchContexts(Stream<NbtPathExpressionExecutionContext> executionContexts) {
            return new NbtPathExpressionMatches(executionContexts);
        }
    }
}
