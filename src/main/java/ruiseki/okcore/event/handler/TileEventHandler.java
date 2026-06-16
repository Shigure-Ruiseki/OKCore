package ruiseki.okcore.event.handler;

import net.minecraftforge.event.world.ChunkEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.tileentity.TileEntityOK;

public class TileEventHandler {

    public static final TileEventHandler INSTANCE = new TileEventHandler();

    public TileEventHandler() {}

    public void shutdown() {}

    @SubscribeEvent
    public void onChunkLoad(final ChunkEvent.Load load) {
        for (final Object te : load.getChunk().chunkTileEntityMap.values()) {
            if (te instanceof TileEntityOK teok) {
                teok.onChunkLoad();
            }
        }
    }

}
