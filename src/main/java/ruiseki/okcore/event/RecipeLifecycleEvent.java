package ruiseki.okcore.event;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.network.packet.PacketUpdateRecipes;
import ruiseki.okcore.recipe.RecipeManager;

public class RecipeLifecycleEvent {

    public static final RecipeLifecycleEvent INSTANCE = new RecipeLifecycleEvent();

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP playerMP) {
            PacketUpdateRecipes packet = new PacketUpdateRecipes(
                RecipeManager.getManager()
                    .getRecipes());
            OKCore.instance.getPacketHandler()
                .sendToPlayer(packet, playerMP);
        }
    }
}
