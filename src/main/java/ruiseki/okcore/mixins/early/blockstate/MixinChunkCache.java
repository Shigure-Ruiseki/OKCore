package ruiseki.okcore.mixins.early.blockstate;

import net.minecraft.world.ChunkCache;
import net.minecraft.world.chunk.Chunk;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import ruiseki.okcore.block.state.BlockStateStorage;
import ruiseki.okcore.block.state.IBlockStateIBA;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.EBSHelpers;

@Mixin(ChunkCache.class)
public class MixinChunkCache implements IBlockStateIBA {

    @Shadow
    private int chunkX;
    @Shadow
    private int chunkZ;
    @Shadow
    private Chunk[][] chunkArray;

    @Override
    public @Nullable BlockStateStorage okcore$getBlockStateStorage(BlockPos pos) {
        int cx = (pos.getX() >> 4) - chunkX;
        int cz = (pos.getZ() >> 4) - chunkZ;
        if (cx < 0 || cz < 0 || cx >= chunkArray.length || cz >= chunkArray[cx].length) return null;
        Chunk chunk = chunkArray[cx][cz];
        if (chunk == null) return null;
        return BlockStateStorage.getStorage(EBSHelpers.getEBS(chunk, pos.getY() >> 4), false);
    }
}
