package ruiseki.okcore.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemToggle {

    String TOGGLE_TAG = "on_off";

    void toggle(EntityPlayer player, ItemStack held);

    default boolean isOn(ItemStack held) {
        if (held == null || !held.hasTagCompound()) return false;
        return held.getTagCompound()
            .getBoolean(TOGGLE_TAG);
    }

    default void setOn(ItemStack held, boolean on) {
        if (held == null) return;
        if (!held.hasTagCompound()) held.setTagCompound(new NBTTagCompound());
        held.getTagCompound()
            .setBoolean(TOGGLE_TAG, on);
    }

    default boolean canMouseClicked(ItemStack stack, int button) {
        // Right Click
        return button == 1;
    }

    default boolean needsShiftClick(ItemStack stack) {
        return true;
    }
}
