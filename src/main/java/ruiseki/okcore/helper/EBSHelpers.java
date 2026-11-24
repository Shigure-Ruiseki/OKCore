package ruiseki.okcore.helper;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import ruiseki.okcore.datastructure.BlockPos;

public class EBSHelpers {

    public static ExtendedBlockStorage getEBS(World world, BlockPos pos) {
        return getEBS(world, pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    public static ExtendedBlockStorage getEBS(World world, int chunkX, int ebsY, int chunkZ) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        return getEBS(chunk, ebsY);
    }

    public static ExtendedBlockStorage getEBS(Chunk chunk, int ebsY) {
        return ebsY < 0 || ebsY >= chunk.getBlockStorageArray().length ? null : chunk.getBlockStorageArray()[ebsY];
    }
}
