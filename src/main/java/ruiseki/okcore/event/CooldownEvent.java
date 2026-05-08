package ruiseki.okcore.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.datacomponent.component.UseCooldown;
import ruiseki.okcore.datacomponent.init.DataComponents;
import ruiseki.okcore.entity.cooldown.ItemCooldowns;
import ruiseki.okcore.helper.DataComponentHelpers;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.item.cooldown.IItemCooldown;

public class CooldownEvent {

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR
            && event.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        EntityPlayer player = event.entityPlayer;
        ItemStack stack = player.getHeldItem();

        if (stack != null && stack.getItem() instanceof IItemCooldown) {
            ItemCooldowns tracker = EntityHelpers.getItemCooldowns(player);
            if (tracker == null) return;
            if (tracker.isOnCooldown(stack)) {
                event.useItem = Result.DENY;
                if (event.isCancelable()) event.setCanceled(true);
                return;
            }

            UseCooldown cooldownData = DataComponentHelpers.get(stack, DataComponents.USE_COOLDOWN);
            if (cooldownData != null) {
                cooldownData.apply(stack, player);
            }
        }
    }
}
