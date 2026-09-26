package ruiseki.okcore.inventory;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.okcore.client.gui.ContainerType;

/**
 * Used for receiving values from servers to clients in guis.
 *
 * @see IValueNotifier
 * @author rubensworks
 */
public interface IValueNotifiable {

    /**
     * @return The container type.
     */
    public ContainerType<?> getValueNotifiableType();

    /**
     * Called by the server if the value has changed.
     *
     * @param valueId The value id.
     * @param value   The new value.
     */
    void onUpdate(int valueId, NBTTagCompound value);

}
