package ruiseki.okcore.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.entity.cooldown.ICooldownHandler;
import ruiseki.okcore.item.cooldown.IItemCooldown;

public class CooldownEvent {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR
            || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {

            EntityPlayer player = event.entityPlayer;
            ItemStack heldItem = player.getHeldItem();

            if (heldItem != null && heldItem.getItem() instanceof IItemCooldown) {
                if (player instanceof ICooldownHandler handler) {
                    if (handler.getCooldownTracker()
                        .hasCooldown(heldItem.getItem())) {
                        event.useItem = Result.DENY;
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
