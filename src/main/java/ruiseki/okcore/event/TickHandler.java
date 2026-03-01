package ruiseki.okcore.event;

import net.minecraftforge.event.world.ChunkEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.tileentity.TileEntityOK;

public class TickHandler {

    public static final TickHandler INSTANCE = new TickHandler();

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
