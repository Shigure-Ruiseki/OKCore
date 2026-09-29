package ruiseki.commoncapabilities.api.capability.recipehandler;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.NotNull;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;

/**
 * A tag-based {@link IPrototypedIngredientAlternatives} implementation.
 *
 * @author rubensworks
 */
public class PrototypedIngredientAlternativesItemStackTag
    implements IPrototypedIngredientAlternatives<ItemStack, Integer> {

    public static final PrototypedIngredientAlternativesItemStackTag.Serializer SERIALIZER = new PrototypedIngredientAlternativesItemStackTag.Serializer();
    static {
        SERIALIZERS.put((byte) 2, SERIALIZER);
    }

    private static final LoadingCache<String, List<ItemStack>> CACHE_TAG = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<String, List<ItemStack>>() {

            @Override
            public List<ItemStack> load(@NotNull String key) {
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(key));
                return TagHelpers.toItemStacks(tagKey);
            }
        });

    private final List<String> keys;
    private final Integer matchCondition;
    private final long quantity;

    public PrototypedIngredientAlternativesItemStackTag(List<String> keys, Integer matchCondition, long quantity) {
        this.keys = keys;
        this.matchCondition = matchCondition;
        this.quantity = quantity;
    }

    @Override
    public Collection<IPrototypedIngredient<ItemStack, Integer>> getAlternatives() {
        IIngredientMatcher<ItemStack, Integer> matcher = IngredientComponent.ITEMSTACK.getMatcher();
        return this.keys.stream()
            .flatMap((key) -> {
                try {
                    return CACHE_TAG.get(key)
                        .stream();
                } catch (ExecutionException e) {
                    return Stream.empty();
                }
            })
            .flatMap(
                itemStack -> ItemHelpers.getVariants(itemStack)
                    .stream())
            .map(itemStack -> matcher.withQuantity(itemStack, getQuantity()))
            .map(itemStack -> new PrototypedIngredient<>(IngredientComponent.ITEMSTACK, itemStack, this.matchCondition))
            .collect(Collectors.toList());
    }

    @Override
    public ISerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PrototypedIngredientAlternativesItemStackTag
            && this.keys.equals(((PrototypedIngredientAlternativesItemStackTag) obj).keys)
            && Objects.equals(this.matchCondition, ((PrototypedIngredientAlternativesItemStackTag) obj).matchCondition)
            && Objects.equals(this.quantity, ((PrototypedIngredientAlternativesItemStackTag) obj).quantity);
    }

    @Override
    public int hashCode() {
        return 1235 | this.keys.hashCode() << 2 | (matchCondition != null ? matchCondition : 0) | (int) quantity;
    }

    public List<String> getKeys() {
        return keys;
    }

    public Integer getMatchCondition() {
        return matchCondition;
    }

    public long getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "[PrototypedIngredientAlternativesTag: " + this.keys.toString() + "]";
    }

    public static class Serializer
        implements IPrototypedIngredientAlternatives.ISerializer<PrototypedIngredientAlternativesItemStackTag> {

        @Override
        public byte getId() {
            return 2; // ID = 2 cho Tag
        }

        @Override
        public <T, M> NBTBase serialize(IngredientComponent<T, M> ingredientComponent,
            PrototypedIngredientAlternativesItemStackTag alternatives) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList keys = new NBTTagList();
            for (String key : alternatives.keys) {
                keys.appendTag(new NBTTagString(key));
            }
            tag.setTag("keys", keys);
            tag.setInteger("match", alternatives.matchCondition != null ? alternatives.matchCondition : 0);
            tag.setLong("quantity", alternatives.quantity);
            return tag;
        }

        @Override
        public <T, M> PrototypedIngredientAlternativesItemStackTag deserialize(
            IngredientComponent<T, M> ingredientComponent, NBTBase tag) {
            NBTTagCompound tagCompound = (NBTTagCompound) tag;
            if (!tagCompound.hasKey("keys")) {
                throw new IllegalArgumentException("A tagged prototyped alternatives did not contain valid keys");
            }
            if (!tagCompound.hasKey("match")) {
                throw new IllegalArgumentException("A tagged prototyped alternatives did not contain a valid match");
            }
            NBTTagList keysTag = tagCompound.getTagList("keys", Constants.NBT.TAG_STRING);
            List<String> keys = Lists.newArrayList();
            for (int i = 0; i < keysTag.tagCount(); i++) {
                keys.add(keysTag.getStringTagAt(i));
            }
            int matchCondition = tagCompound.getInteger("match");
            long quantity = tagCompound.hasKey("quantity") ? tagCompound.getLong("quantity") : 1;
            return new PrototypedIngredientAlternativesItemStackTag(keys, matchCondition, quantity);
        }
    }
}
