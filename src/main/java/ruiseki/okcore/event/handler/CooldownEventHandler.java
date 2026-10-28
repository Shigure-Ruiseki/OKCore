
package ruiseki.okcore.event.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.entity.cooldown.ItemCooldowns;
import ruiseki.okcore.event.interaction.PlayerInteractEvent;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.item.IItemCooldown;
import ruiseki.okcore.item.UseCooldown;

public class CooldownEventHandler {

    public static final CooldownEventHandler INSTANCE = new CooldownEventHandler();

    private CooldownEventHandler() {}

    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        this.handleCooldown(event);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        this.handleCooldown(event);
    }

    private void handleCooldown(PlayerInteractEvent event) {
        EntityPlayer player = event.getEntity();
        ItemStack stack = player.getHeldItem();

        if (stack == null) return;
        if (!(stack.getItem() instanceof IItemCooldown cooldown)) return;

        ItemCooldowns tracker = EntityHelpers.getItemCooldowns(player);
        if (tracker == null) return;

        if (tracker.isOnCooldown(stack)) {
            if (event instanceof PlayerInteractEvent.RightClickItem rightClick) {
                rightClick.setUseItem(Result.DENY);
            } else if (event instanceof PlayerInteractEvent.RightClickBlock rightClick) {
                rightClick.setUseBlock(Result.DENY);
            }

            event.setCanceled(true);
            return;
        }

        UseCooldown useCooldown = cooldown.getUseCooldown(stack);
        useCooldown.apply(stack, player);
    }
}
