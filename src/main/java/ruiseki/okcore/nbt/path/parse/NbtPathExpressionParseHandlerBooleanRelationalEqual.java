package ruiseki.okcore.nbt.path.parse;

/**
 * A handler that handles boolean expressions in the form of " == 10".
 */
public class NbtPathExpressionParseHandlerBooleanRelationalEqual
    extends NbtPathExpressionParseHandlerBooleanRelationalAdapter {

    public NbtPathExpressionParseHandlerBooleanRelationalEqual() {
        super("==");
    }

    protected boolean getRelationalValue(double left, double right) {
        return left == right;
    }
}
