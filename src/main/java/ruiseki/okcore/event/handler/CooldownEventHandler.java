package ruiseki.okcore.event.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.entity.cooldown.ItemCooldowns;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.item.IItemCooldown;
import ruiseki.okcore.item.UseCooldown;

public class CooldownEventHandler {

    public static final CooldownEventHandler INSTANCE = new CooldownEventHandler();

    public CooldownEventHandler() {}

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

            if (!(stack.getItem() instanceof IItemCooldown cooldown)) return;
            UseCooldown useCooldown = cooldown.getUseCooldown(stack);
            if (useCooldown != null) {
                useCooldown.apply(stack, player);
            }
        }
    }
}
