package ruiseki.okcore.helper;

import net.minecraft.nbt.NBTTagCompound;

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
     * Get the string value
     * 
     * @param notifier The notifier instance
     * @param valueId  The value id
     * @return The value
     */
    public static String getValueString(IValueNotifier notifier, int valueId) {
        NBTTagCompound tag = notifier.getValue(valueId);
        if (tag != null) {
            return tag.getString(KEY);
        }
        return null;
    }

}
