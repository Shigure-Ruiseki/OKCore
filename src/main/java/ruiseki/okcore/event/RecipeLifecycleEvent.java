package ruiseki.okcore.event;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.event.data.OKDataEvent;
import ruiseki.okcore.network.packet.PacketUpdateRecipes;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistries;

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
    public void onGlobalPostLoad(OKDataEvent.Post event) {
        RecipeRegistries.processGlobalHolders();
        OKCore.okLog("Successfully built and frozen global base recipes.");
    }

    @SubscribeEvent
    public void onWorldPreLoad(OKDataEvent.WorldPre event) {
        RecipeManager.validateManager();
        OKCore.okLog(
            "Preparing recipe registry for world: " + event.getWorldDir()
                .getName());
    }

    @SubscribeEvent
    public void onWorldPostLoad(OKDataEvent.WorldPost event) {
        RecipeRegistries.processWorldHolders();
    }

    @SubscribeEvent
    public void onWorldUnload(OKDataEvent.WorldUnload event) {
        RecipeManager.invalidateManager();
        OKCore.okLog("Successfully invalidated and cleared all world-bound recipes cache.");
    }

    @SubscribeEvent
    public void onReload(OKDataEvent.Reload event) {
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
