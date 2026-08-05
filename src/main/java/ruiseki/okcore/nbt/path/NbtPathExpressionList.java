package ruiseki.okcore.nbt.path;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import ruiseki.okcore.nbt.path.navigate.INbtPathNavigation;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionExecutionContext;

/**
 * An NBT path expression that consists of a list of chained sub-expressions.
 */
public class NbtPathExpressionList implements INbtPathExpression {

    private final INbtPathExpression[] subExpressions;

    public NbtPathExpressionList(INbtPathExpression... subExpressions) {
        this.subExpressions = subExpressions;
    }

    @Override
    public NbtPathExpressionMatches matchContexts(Stream<NbtPathExpressionExecutionContext> executionContexts) {
        NbtPathExpressionMatches matches = new NbtPathExpressionMatches(executionContexts);
        for (INbtPathExpression subExpression : subExpressions) {
            matches = subExpression.matchContexts(matches.getContexts());
        }
        return matches;
    }

    public INbtPathExpression[] getSubExpressions() {
        return subExpressions;
    }

    @Override
    public INbtPathNavigation asNavigation(@Nullable INbtPathNavigation child) throws NbtParseException {
        INbtPathNavigation current = null;
        for (int i = subExpressions.length - 1; i >= 0; i--) {
            if (current != null) {
                // Inner node
                current = subExpressions[i].asNavigation(current);
            } else {
                // Leaf
                current = subExpressions[i].asNavigation(null);
            }
        }
        return current;
    }

}
