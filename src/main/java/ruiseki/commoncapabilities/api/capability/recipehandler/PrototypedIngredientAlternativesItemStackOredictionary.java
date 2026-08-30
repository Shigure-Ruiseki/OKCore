package ruiseki.commoncapabilities.api.capability.recipehandler;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.okcore.helper.ItemHelpers;

/**
 * An oredictionary-based {@link IPrototypedIngredientAlternatives} implementation.
 *
 * @author rubensworks
 */
public class PrototypedIngredientAlternativesItemStackOredictionary
    implements IPrototypedIngredientAlternatives<ItemStack, Integer> {

    public static final PrototypedIngredientAlternativesItemStackOredictionary.Serializer SERIALIZER = new PrototypedIngredientAlternativesItemStackOredictionary.Serializer();
    static {
        SERIALIZERS.put((byte) 1, SERIALIZER);
    }

    private static final LoadingCache<String, List<ItemStack>> CACHE_OREDICT = CacheBuilder.newBuilder()
        .expireAfterWrite(1, TimeUnit.MINUTES)
        .build(new CacheLoader<String, List<ItemStack>>() {

            @Override
            public List<ItemStack> load(String key) throws Exception {
                return OreDictionary.getOres(key);
            }
        });

    private final List<String> keys;
    private final Integer matchCondition;
    private final long quantity;

    public PrototypedIngredientAlternativesItemStackOredictionary(List<String> keys, Integer matchCondition,
        long quantity) {
        this.keys = keys;
        this.matchCondition = matchCondition;
        this.quantity = quantity;
    }

    public Collection<IPrototypedIngredient<ItemStack, Integer>> getAlternatives() {
        IIngredientMatcher<ItemStack, Integer> matcher = IngredientComponent.ITEMSTACK.getMatcher();
        return this.keys.stream()
            .flatMap((key) -> {
                try {
                    return CACHE_OREDICT.get(key)
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
        return obj instanceof PrototypedIngredientAlternativesItemStackOredictionary
            && this.keys.equals(((PrototypedIngredientAlternativesItemStackOredictionary) obj).keys)
            && Objects.equals(
                this.matchCondition,
                ((PrototypedIngredientAlternativesItemStackOredictionary) obj).matchCondition)
            && Objects.equals(this.quantity, ((PrototypedIngredientAlternativesItemStackOredictionary) obj).quantity);
    }

    @Override
    public int hashCode() {
        return 1235 | this.keys.hashCode() << 2 | matchCondition | (int) quantity;
    }

    public long getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "[PrototypedIngredientAlternativesList: " + this.keys.toString() + "]";
    }

    public static class Serializer implements
        IPrototypedIngredientAlternatives.ISerializer<PrototypedIngredientAlternativesItemStackOredictionary> {

        @Override
        public byte getId() {
            return 1;
        }

        @Override
        public <T, M> NBTBase serialize(IngredientComponent<T, M> ingredientComponent,
            PrototypedIngredientAlternativesItemStackOredictionary alternatives) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagList keys = new NBTTagList();
            for (String key : alternatives.keys) {
                keys.appendTag(new NBTTagString(key));
            }
            tag.setTag("keys", keys);
            tag.setInteger("match", alternatives.matchCondition);
            tag.setLong("quantity", alternatives.quantity);
            return tag;
        }

        @Override
        public <T, M> PrototypedIngredientAlternativesItemStackOredictionary deserialize(
            IngredientComponent<T, M> ingredientComponent, NBTBase tag) {
            NBTTagCompound tagCompound = (NBTTagCompound) tag;
            if (!tagCompound.hasKey("keys")) {
                throw new IllegalArgumentException("A oredict prototyped alternatives did not contain valid keys");
            }
            if (!tagCompound.hasKey("match")) {
                throw new IllegalArgumentException("A oredict prototyped alternatives did not contain a valid match");
            }
            NBTTagList keysTag = tagCompound.getTagList("keys", Constants.NBT.TAG_STRING);
            List<String> keys = Lists.newArrayList();
            for (Object key : keysTag.tagList) {
                keys.add(((NBTTagString) key).func_150285_a_());
            }
            int matchCondition = tagCompound.getInteger("match");
            long quantity = tagCompound.hasKey("quantity") ? tagCompound.getLong("quantity") : 1; // TODO: remove check
                                                                                                  // in 1.13
            return new PrototypedIngredientAlternativesItemStackOredictionary(keys, matchCondition, quantity);
        }
    }
}
