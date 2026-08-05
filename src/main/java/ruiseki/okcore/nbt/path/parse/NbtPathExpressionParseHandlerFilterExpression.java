package ruiseki.okcore.nbt.path.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import ruiseki.okcore.nbt.path.INbtPathExpression;
import ruiseki.okcore.nbt.path.NbtParseException;
import ruiseki.okcore.nbt.path.NbtPath;
import ruiseki.okcore.nbt.path.NbtPathExpressionMatches;

/**
 * A handler that handles filter expressions in the form of "[?(expression)]", such as "[?(@.childName)]" or
 * "[?(@.childName < 10)]".
 */
public class NbtPathExpressionParseHandlerFilterExpression implements INbtPathExpressionParseHandler {

    private static final Pattern REGEX_EXPRESSION = Pattern.compile("^\\[\\?\\(([^\\)^\\(]+)\\)\\]");

    @Nullable
    @Override
    public HandleResult handlePrefixOf(String nbtPathExpression, int pos) {
        Matcher matcher = REGEX_EXPRESSION.matcher(nbtPathExpression)
            .region(pos, nbtPathExpression.length());
        if (!matcher.find()) {
            return HandleResult.INVALID;
        }

        String expressionString = matcher.group(1);
        try {
            INbtPathExpression expression = NbtPath.parse(expressionString);
            return new HandleResult(new Expression(expression), 5 + expressionString.length());
        } catch (NbtParseException e) {
            return HandleResult.INVALID;
        }
    }

    public static class Expression implements INbtPathExpression {

        private final INbtPathExpression expression;

        public Expression(INbtPathExpression expression) {
            this.expression = expression;
        }

        public INbtPathExpression getExpression() {
            return expression;
        }

        @Override
        public NbtPathExpressionMatches matchContexts(Stream<NbtPathExpressionExecutionContext> executionContexts) {
            return new NbtPathExpressionMatches(executionContexts.map(executionContext -> {
                NBTBase nbt = executionContext.getCurrentTag();
                if (nbt == null) {
                    return null;
                }

                if (nbt.getId() == Constants.NBT.TAG_LIST) {
                    NBTTagList tag = (NBTTagList) nbt;
                    NBTTagList newTagList = new NBTTagList();

                    List<NBTBase> subTags = new ArrayList<>();
                    for (int i = 0; i < tag.tagCount(); i++) {
                        subTags.add(tag.getCompoundTagAt(i));
                    }

                    subTags.stream()
                        .filter(subTag -> getExpression().test(subTag))
                        .forEach(newTagList::appendTag);

                    return new NbtPathExpressionExecutionContext(newTagList, executionContext);

                } else if (nbt.getId() == Constants.NBT.TAG_COMPOUND) {
                    NBTTagCompound tag = (NBTTagCompound) nbt;
                    NBTTagList newTagList = new NBTTagList();
                    Set<String> keys = tag.func_150296_c();
                    keys.stream()
                        .map(tag::getTag)
                        .filter(subTag -> getExpression().test(subTag))
                        .forEach(newTagList::appendTag);

                    return new NbtPathExpressionExecutionContext(newTagList, executionContext);
                }
                return null;
            })
                .filter(Objects::nonNull));
        }

    }
}
