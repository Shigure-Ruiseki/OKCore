package ruiseki.okcore.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.entity.cooldown.ItemCooldowns;

public class CooldownHelpers {

    public static float getCooldownPercent(ItemStack stack, EntityPlayer player, float a) {
        ItemCooldowns tracker = EntityHelpers.getItemCooldowns(player);
        if (tracker == null) return 0f;
        return EntityHelpers.getItemCooldowns(player)
            .getCooldownPercent(stack, a);
    }

    public static void addCooldown(ItemStack stack, EntityPlayer player, int cooldown) {
        ItemCooldowns tracker = EntityHelpers.getItemCooldowns(player);
        if (tracker == null) return;
        EntityHelpers.getItemCooldowns(player)
            .addCooldown(stack, cooldown);
    }

    public static void removeCooldown(ItemStack stack, EntityPlayer player) {
        ItemCooldowns tracker = EntityHelpers.getItemCooldowns(player);
        if (tracker == null) return;
        EntityHelpers.getItemCooldowns(player)
            .removeCooldown(stack);
    }

    public static void isOnCooldown(ItemStack stack, EntityPlayer player) {
        ItemCooldowns tracker = EntityHelpers.getItemCooldowns(player);
        if (tracker == null) return;
        EntityHelpers.getItemCooldowns(player)
            .isOnCooldown(stack);
    }
}
