package ruiseki.okcore.event.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.event.data.AddReloadListenerEvent;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.tag.TagManager;

public class DataEventHandler {

    public static final DataEventHandler INSTANCE = new DataEventHandler();

    public DataEventHandler() {}

    @SubscribeEvent
    public void registerDataLoader(AddReloadListenerEvent event) {
        event.addListener(TagManager.getManager());
        event.addListener(RecipeManager.getManager());
    }
}
