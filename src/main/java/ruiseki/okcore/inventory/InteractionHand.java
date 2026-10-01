package ruiseki.okcore.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.modcompat.backhand.BackhandHelpers;

public enum InteractionHand {

    MAIN_HAND,
    OFF_HAND;

    public ItemStack getItemInHand(EntityPlayer player) {
        if (Mods.Backhand.isModLoaded() && this == InteractionHand.OFF_HAND) { // off hand (requires backhand)
            return BackhandHelpers.getOffhandItem(player);
        } else { // main hand
            return player.getHeldItem();
        }
    }

    public static InteractionHand getHand(EntityPlayer player, ItemStack itemStack) {
        if (itemStack == null || player == null) return MAIN_HAND;

        if (Mods.Backhand.isModLoaded() && BackhandHelpers.isOffhand(player, itemStack)) {
            return OFF_HAND;
        }

        return MAIN_HAND;
    }
}
