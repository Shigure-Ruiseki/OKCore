package ruiseki.okcore.helper;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.okcore.entity.cooldown.ICooldownHandler;
import ruiseki.okcore.entity.cooldown.ItemCooldowns;

public class EntityHelpers {

    public static ItemCooldowns getItemCooldowns(EntityPlayer player) {
        if (player == null) return null;
        try {
            ICooldownHandler provider = (ICooldownHandler) (Object) player;

            return provider.getItemCooldowns();

        } catch (ClassCastException ignored) {
            return null;
        }
    }
}
