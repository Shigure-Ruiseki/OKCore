package ruiseki.okcore.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.KeyBoardHelpers;

public interface IItemToggle {

    String TOGGLE_TAG = "on_off";

    void toggle(EntityPlayerMP player, ItemStack slotStack, int button);

    default boolean isOn(ItemStack held) {
        if (!ItemHelpers.hasTagCompound(held)) return false;
        return ItemHelpers.getTagCompound(held)
            .getBoolean(TOGGLE_TAG);
    }

    default void setOn(ItemStack held, boolean on) {
        if (!ItemHelpers.hasTagCompound(held)) ItemHelpers.setTagCompound(held, new NBTTagCompound());
        ItemHelpers.getTagCompound(held)
            .setBoolean(TOGGLE_TAG, on);
    }

    default boolean canMouseClicked(ItemStack stack, int button) {
        // Right Click
        return button == 1;
    }

    default boolean isModifierKeyDown(ItemStack stack) {
        return KeyBoardHelpers.isShiftKeyDown();
    }

    default void playSound(EntityPlayer player, ItemStack stack, int button) {
        player.worldObj.playSoundAtEntity(player, "random.click", 0.3F, 0.5F);
    }
}
