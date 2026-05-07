package ruiseki.okcore.helper;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.okcore.entity.cooldown.CooldownTracker;
import ruiseki.okcore.entity.cooldown.ICooldownHandler;

public class EntityHelpers {

    public static CooldownTracker getCooldownTracker(EntityPlayer player) {
        if (player == null) return null;
        try {
            ICooldownHandler provider = (ICooldownHandler) (Object) player;

            return provider.getCooldownTracker();

        } catch (ClassCastException ignored) {
            return null;
        }
    }
}
