package ruiseki.okcore.nbt.path;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.okcore.nbt.path.parse.INbtPathExpressionParseHandler;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerAllChildren;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerBooleanRelationalEqual;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerBooleanRelationalGreaterThan;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerBooleanRelationalGreaterThanOrEqual;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerBooleanRelationalLessThan;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerBooleanRelationalLessThanOrEqual;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerChild;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerChildBrackets;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerCurrent;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerFilterExpression;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerLength;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerListElement;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerListSlice;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerParent;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerRoot;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerStringEqual;
import ruiseki.okcore.nbt.path.parse.NbtPathExpressionParseHandlerUnion;

/**
 * Utility class for parsing NBT path expressions.
 */
public class NbtPath {

    private static final List<INbtPathExpressionParseHandler> PARSE_HANDLERS = Lists.newArrayList(
        new NbtPathExpressionParseHandlerRoot(),
        new NbtPathExpressionParseHandlerLength(),
        new NbtPathExpressionParseHandlerChild(),
        new NbtPathExpressionParseHandlerChildBrackets(),
        new NbtPathExpressionParseHandlerParent(),
        new NbtPathExpressionParseHandlerAllChildren(),
        new NbtPathExpressionParseHandlerCurrent(),
        new NbtPathExpressionParseHandlerListElement(),
        new NbtPathExpressionParseHandlerListSlice(),
        new NbtPathExpressionParseHandlerUnion(),
        new NbtPathExpressionParseHandlerBooleanRelationalLessThan(),
        new NbtPathExpressionParseHandlerBooleanRelationalLessThanOrEqual(),
        new NbtPathExpressionParseHandlerBooleanRelationalGreaterThan(),
        new NbtPathExpressionParseHandlerBooleanRelationalGreaterThanOrEqual(),
        new NbtPathExpressionParseHandlerBooleanRelationalEqual(),
        new NbtPathExpressionParseHandlerStringEqual(),
        new NbtPathExpressionParseHandlerFilterExpression());

    /**
     * Parse an NBT path expression string into an in-memory representation.
     * 
     * @param nbtPathExpression An NBT path expression string
     * @return An in-memory representation of the given expression.
     * @throws NbtParseException An exception that can be thrown if parsing failed.
     */
    public static INbtPathExpression parse(String nbtPathExpression) throws NbtParseException {
        List<INbtPathExpression> expressions = Lists.newArrayList();

        int pos = 0;
        while (pos < nbtPathExpression.length()) {
            boolean handled = false;
            for (INbtPathExpressionParseHandler parseHandler : NbtPath.PARSE_HANDLERS) {
                INbtPathExpressionParseHandler.HandleResult handleResult = parseHandler
                    .handlePrefixOf(nbtPathExpression, pos);
                if (handleResult.isValid()) {
                    pos += handleResult.getConsumedExpressionLength();
                    expressions.add(handleResult.getPrefixExpression());
                    handled = true;
                    break;
                }
            }

            if (!handled) {
                throw new NbtParseException(
                    String.format("Failed to parse expression at pos '%s'", pos, nbtPathExpression));
            }
        }

        return new NbtPathExpressionList(expressions.toArray(new INbtPathExpression[0]));
    }

}
