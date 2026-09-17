package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTBase;
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
     */
    public static void setValue(IValueNotifier notifier, int valueId, NBTBase value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag(KEY, value);
        notifier.setValue(valueId, tag);
    }

    /**
     * Set the int value
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
        if (value == null || value.isEmpty()) {
            value = " ";
        }
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
        if (values != null) {
            for (String value : values) {
                if (value == null || value.isEmpty()) {
                    value = " ";
                }
                list.appendTag(new NBTTagString(value));
            }
        }
        tag.setTag(KEY, list);
        notifier.setValue(valueId, tag);
    }

    /**
     * Set the {@link LangHelpers.UnlocalizedString} value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @param value    The unlocalized string
     */
    public static void setValueUnlocalizedString(IValueNotifier notifier, int valueId,
        LangHelpers.UnlocalizedString value) {
        NBTTagCompound tag = new NBTTagCompound();
        if (value != null) {
            tag.setTag(KEY, value.serializeNBT());
        }
        notifier.setValue(valueId, tag);
    }

    /**
     * Set the {@link LangHelpers.UnlocalizedString} list value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @param values   The unlocalized strings list
     */
    public static void setValueUnlocalizedStringList(IValueNotifier notifier, int valueId,
        List<LangHelpers.UnlocalizedString> values) {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        if (values != null) {
            for (LangHelpers.UnlocalizedString value : values) {
                if (value != null) {
                    list.appendTag(value.serializeNBT());
                }
            }
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
    public static NBTBase getValueNbt(IValueNotifier notifier, int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null) {
            return tag.getTag(KEY);
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
            String val = tag.getString(KEY);
            return " ".equals(val) ? "" : val;
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
                String val = listTag.getStringTagAt(i);
                list.add(" ".equals(val) ? "" : val);
            }
            return list;
        }
        return null;
    }

    /**
     * Get the {@link LangHelpers.UnlocalizedString} value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @return The unlocalized string
     */
    @Nullable
    public static LangHelpers.UnlocalizedString getValueUnlocalizedString(IValueNotifier notifier, int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null && tag.hasKey(KEY, Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound compound = tag.getCompoundTag(KEY);
            LangHelpers.UnlocalizedString unlocalizedString = new LangHelpers.UnlocalizedString();
            unlocalizedString.deserializeNBT(compound);
            return unlocalizedString;
        }
        return null;
    }

    /**
     * Get the {@link LangHelpers.UnlocalizedString} list value
     *
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @return The list of unlocalized strings
     */
    @Nullable
    public static List<LangHelpers.UnlocalizedString> getValueUnlocalizedStringList(IValueNotifier notifier,
        int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null) {
            NBTTagList listTag = tag.getTagList(KEY, Constants.NBT.TAG_COMPOUND);
            List<LangHelpers.UnlocalizedString> list = new ArrayList<>();
            for (int i = 0; i < listTag.tagCount(); i++) {
                NBTTagCompound compound = listTag.getCompoundTagAt(i);
                LangHelpers.UnlocalizedString unlocalizedString = new LangHelpers.UnlocalizedString();
                unlocalizedString.deserializeNBT(compound);
                list.add(unlocalizedString);
            }
            return list;
        }
        return null;
    }

}
