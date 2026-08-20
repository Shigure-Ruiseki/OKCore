package ruiseki.okcore.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.okcore.helper.KeyBoardHelpers;

public interface IItemToggle {

    String TOGGLE_TAG = "on_off";

    default void toggle(EntityPlayerMP player, ItemStack slotStack, int button) {
        toggle(player, slotStack);
    }

    @Deprecated
    default void toggle(EntityPlayer player, ItemStack slotStack) {}

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

    default boolean isModifierKeyDown(ItemStack stack) {
        return KeyBoardHelpers.isShiftKeyDown();
    }
}
