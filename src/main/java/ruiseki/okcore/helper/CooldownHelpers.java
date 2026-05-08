package ruiseki.okcore.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class CooldownHelpers {

    public static float getCooldown(Item item, EntityPlayer player) {
        return EntityHelpers.getCooldownTracker(player)
            .getCooldown(item, 0f);
    }

    public static void setCooldown(Item item, EntityPlayer player, int cooldown) {
        EntityHelpers.getCooldownTracker(player)
            .setCooldown(item, cooldown);
    }

    public static void removeCooldown(Item item, EntityPlayer player) {
        EntityHelpers.getCooldownTracker(player)
            .removeCooldown(item);
    }

    public static void hasCooldown(Item item, EntityPlayer player) {
        EntityHelpers.getCooldownTracker(player)
            .hasCooldown(item);
    }
}
