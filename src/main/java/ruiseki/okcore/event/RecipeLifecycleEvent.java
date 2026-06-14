package ruiseki.okcore.event;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.DataLoader;
import ruiseki.okcore.event.data.DataEvent;
import ruiseki.okcore.network.packet.PacketUpdateRecipes;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;

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

    @SubscribeEvent
    public void onReload(DataEvent.Reload event) {
        DataLoader.loadAllDataAtServerStart(event.getServer());
        RecipeRegistry.processHolders(true);
        PacketUpdateRecipes packet = new PacketUpdateRecipes(
            RecipeManager.getManager()
                .getRecipes());

        List<EntityPlayerMP> players = event.getServer()
            .getConfigurationManager().playerEntityList;
        for (EntityPlayerMP playerMP : players) {
            OKCore.instance.getPacketHandler()
                .sendToPlayer(packet, playerMP);
        }
    }
}
