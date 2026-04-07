package ruiseki.okcore.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTHelpers {

    private NBTHelpers() {}

    public static Optional<Integer> getInteger(ItemStack stack, String key) {
        return getTagValue(stack, key, NBTTagCompound::getInteger);
    }

    public static Optional<Integer> getInteger(NBTTagCompound tag, String key) {
        return getTagValue(tag, key, NBTTagCompound::getInteger);
    }

    public static Optional<int[]> getIntegerArray(NBTTagCompound tag, String key) {
        return getTagValue(tag, key, NBTTagCompound::getIntArray);
    }

    private static <T> Optional<T> getTagValue(ItemStack stack, String key,
        BiFunction<NBTTagCompound, String, T> getValue) {
        return getTagValue(stack, "", key, getValue);
    }

    public static <T> Optional<T> getTagValue(ItemStack stack, String parentKey, String key,
        BiFunction<NBTTagCompound, String, T> getValue) {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            return Optional.empty();
        }

        if (!parentKey.isEmpty()) {
            NBTBase parentTag = tag.getTag(parentKey);
            if (!(parentTag instanceof NBTTagCompound)) {
                return Optional.empty();
            }
            tag = (NBTTagCompound) parentTag;
        }

        return getTagValue(tag, key, getValue);
    }

    public static Optional<Boolean> getBoolean(NBTTagCompound tag, String key) {
        return getTagValue(tag, key, NBTTagCompound::getBoolean);
    }

    public static Optional<NBTTagCompound> getCompoundTag(NBTTagCompound tag, String key) {
        return getTagValue(tag, key, NBTTagCompound::getCompoundTag);
    }

    public static <T> Optional<T> getTagValue(NBTTagCompound tag, String key,
        BiFunction<NBTTagCompound, String, T> getValue) {
        if (!tag.hasKey(key)) {
            return Optional.empty();
        }

        return Optional.of(getValue.apply(tag, key));
    }

    public static <E, C extends Collection<E>> Optional<C> getCollection(ItemStack stack, String parentKey,
        String tagName, byte listType, Function<NBTBase, Optional<E>> getElement, Supplier<C> initCollection) {
        return getTagValue(stack, parentKey, tagName, (c, n) -> c.getTagList(n, listType)).map(listNbt -> {
            C ret = initCollection.get();
            for (int i = 0; i < listNbt.tagCount(); i++) {
                getElement.apply(listNbt.getCompoundTagAt(i))
                    .ifPresent(ret::add);
            }
            return ret;
        });
    }

    public static <E, C extends Collection<E>> Optional<C> getCollection(NBTTagCompound tag, String key, byte listType,
        Function<NBTBase, Optional<E>> getElement, Supplier<C> initCollection) {
        return getTagValue(tag, key, (c, n) -> c.getTagList(n, listType)).map(listNbt -> {
            C ret = initCollection.get();
            for (int i = 0; i < listNbt.tagCount(); i++) {
                getElement.apply(listNbt.getCompoundTagAt(i))
                    .ifPresent(ret::add);
            }
            return ret;
        });
    }

    public static Optional<NBTTagCompound> getCompoundTag(ItemStack stack, String parentKey, String tagName) {
        return getTagValue(stack, parentKey, tagName, NBTTagCompound::getCompoundTag);
    }

    public static Optional<NBTTagCompound> getCompoundTag(ItemStack stack, String tagName) {
        return getTagValue(stack, tagName, NBTTagCompound::getCompoundTag);
    }

    public static <T extends Enum<T>> Optional<T> getEnumConstant(ItemStack stack, String parentKey, String key,
        Function<String, T> deserialize) {
        return getTagValue(stack, parentKey, key, (t, k) -> deserialize.apply(t.getString(k)));
    }

    public static <T extends Enum<T>> Optional<T> getEnumConstant(ItemStack stack, String key,
        Function<String, T> deserialize) {
        return getTagValue(stack, key, (t, k) -> deserialize.apply(t.getString(k)));
    }

    public static <T extends Enum<T>> Optional<T> getEnumConstant(NBTTagCompound tag, String key,
        Function<String, T> deserialize) {
        return getTagValue(tag, key, (t, k) -> deserialize.apply(t.getString(k)));
    }

    public static Optional<Boolean> getBoolean(ItemStack stack, String parentKey, String key) {
        return getTagValue(stack, parentKey, key, NBTTagCompound::getBoolean);
    }

    public static Optional<Boolean> getBoolean(ItemStack stack, String key) {
        return getTagValue(stack, key, NBTTagCompound::getBoolean);
    }

    public static Optional<Long> getLong(ItemStack stack, String key) {
        return getTagValue(stack, key, NBTTagCompound::getLong);
    }

    public static Optional<Long> getLong(NBTTagCompound tag, String key) {
        return getTagValue(tag, key, NBTTagCompound::getLong);
    }

    public static void setCompoundNBT(ItemStack stack, String key, NBTTagCompound tag) {
        setCompoundNBT(stack, "", key, tag);
    }

    public static void setCompoundNBT(ItemStack stack, String parentKey, String key, NBTTagCompound value) {
        NBTTagCompound tag = getOrCreateTag(stack);
        if (parentKey.isEmpty()) {
            getOrCreateTag(stack).setTag(key, tag);
            return;
        }
        getOrCreateTagElement(stack, parentKey).setTag(key, tag);
    }

    public static void setBoolean(ItemStack stack, String parentKey, String key, boolean value) {
        if (parentKey.isEmpty()) {
            setBoolean(stack, key, value);
            return;
        }
        setBoolean(getOrCreateTagElement(stack, parentKey), key, value);
    }

    public static void setBoolean(ItemStack stack, String key, boolean value) {
        setBoolean(getOrCreateTag(stack), key, value);
    }

    public static NBTTagCompound setBoolean(NBTTagCompound tag, String key, boolean value) {
        tag.setBoolean(key, value);
        return tag;
    }

    public static NBTTagCompound setInteger(NBTTagCompound tag, String key, int value) {
        tag.setInteger(key, value);
        return tag;
    }

    public static NBTTagCompound setString(NBTTagCompound tag, String key, String value) {
        tag.setString(key, value);
        return tag;
    }

    public static void setLong(ItemStack stack, String key, long value) {
        getOrCreateTag(stack).setLong(key, value);
    }

    public static void setInteger(ItemStack stack, String key, int value) {
        getOrCreateTag(stack).setInteger(key, value);
    }

    public static void removeTag(ItemStack stack, String key) {
        if (stack.getTagCompound() == null) {
            return;
        }
        stack.getTagCompound()
            .removeTag(key);
    }

    public static Optional<String> getString(NBTTagCompound tag, String key) {
        return getTagValue(tag, key, NBTTagCompound::getString);
    }

    public static Optional<String> getString(ItemStack stack, String key) {
        return getTagValue(stack, key, NBTTagCompound::getString);
    }

    public static Optional<Float> getFloat(ItemStack stack, String key) {
        return getTagValue(stack, key, NBTTagCompound::getFloat);
    }

    public static <K, V> Optional<Map<K, V>> getMap(ItemStack stack, String key, Function<String, K> getKey,
        BiFunction<String, NBTBase, Optional<V>> getValue) {
        NBTTagCompound tag = stack.getTagCompound();

        if (tag == null) {
            return Optional.empty();
        }

        return getMap(tag, key, getKey, getValue);
    }

    public static <K, V> Optional<Map<K, V>> getMap(NBTTagCompound tag, String key, Function<String, K> getKey,
        BiFunction<String, NBTBase, Optional<V>> getValue) {
        return getMap(tag, key, getKey, getValue, HashMap::new);
    }

    public static <K, V> Optional<Map<K, V>> getMap(NBTTagCompound tag, String key, Function<String, K> getKey,
        BiFunction<String, NBTBase, Optional<V>> getValue, Supplier<Map<K, V>> initMap) {
        NBTTagCompound mapNbt = tag.getCompoundTag(key);

        Map<K, V> map = initMap.get();

        for (String tagName : mapNbt.func_150296_c()) {
            getValue.apply(tagName, mapNbt.getTag(tagName))
                .ifPresent(value -> map.put(getKey.apply(tagName), value));
        }

        return Optional.of(map);
    }

    public static <K, V> NBTTagCompound setMap(NBTTagCompound tag, String key, Map<K, V> map,
        Function<K, String> getStringKey, Function<V, NBTBase> getNbtValue) {
        NBTTagCompound mapNbt = new NBTTagCompound();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            mapNbt.setTag(getStringKey.apply(entry.getKey()), getNbtValue.apply(entry.getValue()));
        }
        tag.setTag(key, mapNbt);
        return tag;
    }

    public static <T> void setList(ItemStack stack, String parentKey, String key, Collection<T> values,
        Function<T, NBTBase> getNbtValue) {
        NBTTagList list = new NBTTagList();
        values.forEach(v -> list.appendTag(getNbtValue.apply(v)));
        if (parentKey.isEmpty()) {
            getOrCreateTag(stack).setTag(key, list);
        } else {
            getOrCreateTagElement(stack, parentKey).setTag(key, list);
        }
    }

    public static <T> void setList(NBTTagCompound tag, String key, Collection<T> values,
        Function<T, NBTBase> getNbtValue) {
        NBTTagList list = new NBTTagList();
        values.forEach(v -> list.appendTag(getNbtValue.apply(v)));
        tag.setTag(key, list);
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public static NBTTagCompound getOrCreateTagElement(ItemStack stack, String key) {
        NBTTagCompound root = getOrCreateTag(stack);

        if (!root.hasKey(key)) {
            NBTTagCompound child = new NBTTagCompound();
            root.setTag(key, child);
            return child;
        }

        NBTBase base = root.getTag(key);
        if (!(base instanceof NBTTagCompound)) {
            NBTTagCompound child = new NBTTagCompound();
            root.setTag(key, child);
            return child;
        }

        return (NBTTagCompound) base;
    }
}
