package ruiseki.okcore.block.state;

import net.minecraft.world.IBlockAccess;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.BlockPos;

/// Implemented on [IBlockAccess] types (World, ChunkCache) to expose [BlockStateStorage] lookups
/// without requiring a cast to [net.minecraft.world.World]. This allows block rendering code
/// (which only receives [IBlockAccess]) to read extended block state.
public interface IBlockStateIBA {

    @Nullable
    BlockStateStorage okcore$getBlockStateStorage(BlockPos pos);

    static @Nullable BlockStateStorage get(IBlockAccess iba, BlockPos pos) {
        if (iba instanceof IBlockStateIBA ext) {
            return ext.okcore$getBlockStateStorage(pos);
        }
        return null;
    }
}
