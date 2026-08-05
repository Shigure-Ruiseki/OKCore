package ruiseki.okcore.nbt.path.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import ruiseki.okcore.nbt.path.INbtPathExpression;
import ruiseki.okcore.nbt.path.NbtPathExpressionMatches;
import ruiseki.okcore.nbt.path.navigate.INbtPathNavigation;
import ruiseki.okcore.nbt.path.navigate.NbtPathNavigationLeafWildcard;
import ruiseki.okcore.nbt.path.navigate.NbtPathNavigationLinkWildcard;

/**
 * A handler that handles follows all child links of a tag via "*".
 */
public class NbtPathExpressionParseHandlerAllChildren implements INbtPathExpressionParseHandler {

    @Nullable
    @Override
    public HandleResult handlePrefixOf(String nbtPathExpression, int pos) {
        if (nbtPathExpression.charAt(pos) != '*') {
            return HandleResult.INVALID;
        }

        return new HandleResult(NbtPathExpressionParseHandlerAllChildren.Expression.INSTANCE, 1);
    }

    public static class Expression implements INbtPathExpression {

        public static final NbtPathExpressionParseHandlerAllChildren.Expression INSTANCE = new NbtPathExpressionParseHandlerAllChildren.Expression();

        @Override
        public NbtPathExpressionMatches matchContexts(Stream<NbtPathExpressionExecutionContext> executionContexts) {
            return new NbtPathExpressionMatches(executionContexts.flatMap(executionContext -> {
                NBTBase nbt = executionContext.getCurrentTag();
                if (nbt == null) {
                    return null;
                }

                if (nbt.getId() == Constants.NBT.TAG_LIST) {
                    NBTTagList tagList = (NBTTagList) nbt;
                    List<NBTBase> children = new ArrayList<>();
                    for (int i = 0; i < tagList.tagCount(); i++) {
                        children.add(tagList.getCompoundTagAt(i));
                    }
                    return children.stream()
                        .map((subTag) -> new NbtPathExpressionExecutionContext(subTag, executionContext));

                } else if (nbt.getId() == Constants.NBT.TAG_COMPOUND) {
                    NBTTagCompound tagCompound = (NBTTagCompound) nbt;
                    Set<String> keys = tagCompound.func_150296_c();
                    return keys.stream()
                        .map((key) -> new NbtPathExpressionExecutionContext(tagCompound.getTag(key), executionContext));
                }
                return null;
            })
                .filter(Objects::nonNull));
        }

        @Override
        public INbtPathNavigation asNavigation(@Nullable INbtPathNavigation child) {
            return child == null ? NbtPathNavigationLeafWildcard.INSTANCE : new NbtPathNavigationLinkWildcard(child);
        }
    }
}
