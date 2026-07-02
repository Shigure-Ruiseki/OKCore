package ruiseki.okcore.event.handler;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.DataLoader;
import ruiseki.okcore.event.data.AddReloadListenerEvent;
import ruiseki.okcore.event.data.DataEvent;
import ruiseki.okcore.network.packet.PacketUpdateRecipes;
import ruiseki.okcore.network.packet.PacketUpdateTags;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.tag.TagManager;

public class DataEventHandler {

    public static final DataEventHandler INSTANCE = new DataEventHandler();

    public DataEventHandler() {}

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP playerMP) {
            // Tags
            OKCore.instance.getPacketHandler()
                .sendToPlayer(
                    new PacketUpdateTags(
                        TagManager.getManager()
                            .getTags()),
                    playerMP);

            // Recipes
            OKCore.instance.getPacketHandler()
                .sendToPlayer(
                    new PacketUpdateRecipes(
                        RecipeManager.getManager()
                            .getRecipes()),
                    playerMP);
        }
    }

    @SubscribeEvent
    public void onReload(DataEvent.Reload event) {
        DataLoader.loadAllDataAtServerStart(event.getServer());
        List<EntityPlayerMP> players = event.getServer()
            .getConfigurationManager().playerEntityList;
        for (EntityPlayerMP playerMP : players) {
            // Tags
            OKCore.instance.getPacketHandler()
                .sendToPlayer(
                    new PacketUpdateTags(
                        TagManager.getManager()
                            .getTags()),
                    playerMP);

            // Recipes
            OKCore.instance.getPacketHandler()
                .sendToPlayer(
                    new PacketUpdateRecipes(
                        RecipeManager.getManager()
                            .getRecipes()),
                    playerMP);
        }
    }

    @SubscribeEvent
    public void registerDataLoader(AddReloadListenerEvent event) {
        event.addListener(TagManager.getManager());
        event.addListener(RecipeManager.getManager());
    }
}
