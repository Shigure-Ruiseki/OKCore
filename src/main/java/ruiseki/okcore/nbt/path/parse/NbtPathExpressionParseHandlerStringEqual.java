package ruiseki.okcore.nbt.path.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import ruiseki.okcore.nbt.path.INbtPathExpression;
import ruiseki.okcore.nbt.path.NbtPathExpressionMatches;

/**
 * A handler that handles boolean expressions in the form of " == "abc"".
 */
public class NbtPathExpressionParseHandlerStringEqual implements INbtPathExpressionParseHandler {

    private static final Pattern REGEX_EQUAL = Pattern.compile("^ *== *\"([^\"]*)\"");

    @Nullable
    @Override
    public HandleResult handlePrefixOf(String nbtPathExpression, int pos) {
        Matcher matcher = REGEX_EQUAL.matcher(nbtPathExpression)
            .region(pos, nbtPathExpression.length());
        if (!matcher.find()) {
            return HandleResult.INVALID;
        }

        String targetString = matcher.group(1);
        return new HandleResult(
            new Expression(targetString),
            matcher.group()
                .length());
    }

    public static class Expression implements INbtPathExpression {

        private final String targetString;

        public Expression(String targetString) {
            this.targetString = targetString;
        }

        String getTargetString() {
            return targetString;
        }

        @Override
        public NbtPathExpressionMatches matchContexts(Stream<NbtPathExpressionExecutionContext> executionContexts) {
            return new NbtPathExpressionMatches(executionContexts.map(executionContext -> {
                NBTBase nbt = executionContext.getCurrentTag();
                if (nbt.getId() == Constants.NBT.TAG_STRING) {
                    NBTTagString tag = (NBTTagString) nbt;
                    return new NbtPathExpressionExecutionContext(
                        new NBTTagByte(getTargetString().equals(tag.func_150285_a_()) ? (byte) 1 : (byte) 0),
                        executionContext);
                }
                return new NbtPathExpressionExecutionContext(new NBTTagByte((byte) 0), executionContext);
            }));
        }

    }
}
