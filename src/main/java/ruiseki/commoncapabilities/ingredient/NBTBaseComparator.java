package ruiseki.commoncapabilities.ingredient;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;

import ruiseki.okcore.nbt.path.navigate.INbtPathNavigation;

/**
 * A comparator implementation for NBT tags.
 *
 * @author rubensworks
 */
public class NBTBaseComparator implements Comparator<NBTBase> {

    /**
     * A comparator for NBT tags. (This is set in GeneralConfig)
     */
    public static Comparator<NBTBase> INSTANCE = new NBTBaseComparator(null);

    private final INbtPathNavigation ignoreNbtNavigation;

    public NBTBaseComparator(@Nullable INbtPathNavigation ignoreNbtNavigation) {
        this.ignoreNbtNavigation = ignoreNbtNavigation;
    }

    @Override
    public int compare(NBTBase o1, NBTBase o2) {
        return this.compare(o1, o2, this.ignoreNbtNavigation);
    }

    protected int compare(NBTBase o1, NBTBase o2, @Nullable INbtPathNavigation ignoreNbtNavigation) {
        if (o1.getId() == o2.getId()) {
            switch (o1.getId()) {
                case 0:
                    return 0;
                case 1:
                    return Byte.compare(((NBTTagByte) o1).func_150290_f(), ((NBTTagByte) o2).func_150290_f());
                case 2:
                    return Short.compare(((NBTTagShort) o1).func_150289_e(), ((NBTTagShort) o2).func_150289_e());
                case 3:
                    return Integer.compare(((NBTTagInt) o1).func_150287_d(), ((NBTTagInt) o2).func_150287_d());
                case 4:
                    return Long.compare(((NBTTagLong) o1).func_150291_c(), ((NBTTagLong) o2).func_150291_c());
                case 5:
                    return Float.compare(((NBTTagFloat) o1).func_150288_h(), ((NBTTagFloat) o2).func_150288_h());
                case 6:
                    return Double.compare(((NBTTagDouble) o1).func_150286_g(), ((NBTTagDouble) o2).func_150286_g());
                case 7:
                    return UnsignedBytes.lexicographicalComparator()
                        .compare(((NBTTagByteArray) o1).func_150292_c(), ((NBTTagByteArray) o2).func_150292_c());
                case 8:
                    return ((NBTTagString) o1).func_150285_a_()
                        .compareTo(((NBTTagString) o2).func_150285_a_());
                case 9:
                    NBTTagList l1 = (NBTTagList) o1;
                    NBTTagList l2 = (NBTTagList) o2;
                    if (l1.tagCount() != l2.tagCount()) {
                        return l1.tagCount() - l2.tagCount();
                    }
                    Iterator<NBTBase> it1 = l1.tagList.iterator();
                    Iterator<NBTBase> it2 = l1.tagList.iterator();
                    while (it1.hasNext()) {
                        int comp = this.compare(it1.next(), it2.next(), null);
                        if (comp != 0) {
                            return comp;
                        }
                    }
                    return 0;
                case 10:
                    NBTTagCompound t1 = (NBTTagCompound) o1;
                    NBTTagCompound t2 = (NBTTagCompound) o2;
                    Set<String> k1 = t1.func_150296_c();
                    Set<String> k2 = t2.func_150296_c();
                    if (ignoreNbtNavigation != null) {
                        k1 = k1.stream()
                            .filter(k -> !ignoreNbtNavigation.isLeafKey(k))
                            .collect(Collectors.toSet());
                        k2 = k2.stream()
                            .filter(k -> !ignoreNbtNavigation.isLeafKey(k))
                            .collect(Collectors.toSet());
                    }
                    if (!k1.equals(k2)) {
                        String[] k1a = k1.toArray(new String[0]);
                        String[] k2a = k2.toArray(new String[0]);
                        Arrays.sort(k1a);
                        Arrays.sort(k2a);
                        int minLength = Math.min(k1a.length, k2a.length);
                        for (int i = 0; i < minLength; i++) {
                            int result = k1a[i].compareTo(k2a[i]);
                            if (result != 0) {
                                return result;
                            }
                        }
                        return k1a.length - k2a.length;
                    }
                    for (String key : k1) {
                        int comp = this.compare(
                            t1.getTag(key),
                            t2.getTag(key),
                            ignoreNbtNavigation != null ? ignoreNbtNavigation.getNext(key) : null);
                        if (comp != 0) {
                            return comp;
                        }
                    }
                    return 0;
                case 11:
                    return Ints.lexicographicalComparator()
                        .compare(((NBTTagIntArray) o1).func_150302_c(), ((NBTTagIntArray) o2).func_150302_c());
                default:
                    return 0;
            }
        }
        return o1.getId() - o2.getId();
    }

}
