package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.inventory.IValueNotifier;

/**
 * Helper methods for {@link ruiseki.okcore.inventory.IValueNotifiable} and
 * {@link ruiseki.okcore.inventory.IValueNotifier}.
 *
 * @author rubensworks
 */
public class ValueNotifierHelpers {

    public static String KEY = "v";

    /**
     * Set the NBT value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @param value    The value
     */
    public static void setValue(IValueNotifier notifier, int valueId, NBTTagCompound value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(KEY, value);
        notifier.setValue(valueId, tag);
    }

    /**
     * Set the int value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @param value    The value
     */
    public static void setValue(IValueNotifier notifier, int valueId, int value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(KEY, value);
        notifier.setValue(valueId, tag);
    }

    /**
     * Set the boolean value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @param value    The value
     */
    public static void setValue(IValueNotifier notifier, int valueId, boolean value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean(KEY, value);
        notifier.setValue(valueId, tag);
    }

    /**
     * Set the string value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @param value    The value
     */
    public static void setValue(IValueNotifier notifier, int valueId, String value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(KEY, value);
        notifier.setValue(valueId, tag);
    }

    /**
     * Set the {@link String} list value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @param values   The values
     */
    public static void setValue(IValueNotifier notifier, int valueId, List<String> values) {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (String value : values) {
            list.appendTag(new NBTTagString(value));
        }
        tag.setTag(KEY, list);
        notifier.setValue(valueId, tag);
    }

    /**
     * get the NBT value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @return The value
     */
    @Nullable
    public static NBTTagCompound getValueNbt(IValueNotifier notifier, int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null) {
            return tag.getCompoundTag(KEY);
        }
        return null;
    }

    /**
     * get the int value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @return The value
     */
    public static int getValueInt(IValueNotifier notifier, int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null) {
            return tag.getInteger(KEY);
        }
        return 0;
    }

    /**
     * get the boolean value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @return The value
     */
    public static boolean getValueBoolean(IValueNotifier notifier, int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null) {
            return tag.getBoolean(KEY);
        }
        return false;
    }

    /**
     * Get the string value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @return The value
     */
    @Nullable
    public static String getValueString(IValueNotifier notifier, int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null) {
            return tag.getString(KEY);
        }
        return null;
    }

    /**
     * Get the {@link String} list value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @return The value
     */
    @Nullable
    public static List<String> getValueStringList(IValueNotifier notifier, int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null) {
            NBTTagList listTag = tag.getTagList(KEY, Constants.NBT.TAG_STRING);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < listTag.tagCount(); i++) {
                list.add(listTag.getStringTagAt(i));
            }
            return list;
        }
        return null;
    }

}
