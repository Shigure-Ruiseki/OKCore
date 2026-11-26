package ruiseki.okcore.block.state;

import net.minecraft.block.Block;

public interface IBlockStateNative {

    BlockStateContainer getStateContainer();

    default void createBlockStateDefinition(BlockStateContainer.Builder builder) {

    }

    void registerDefaultState(IBlockState state);

    IBlockState getDefaultState();

    Block getBlockFromState(IBlockState state);

    default boolean isSameBlock(Block newBlock) {
        return newBlock == this;
    }

    default IBlockState getInitialState(Block placedBlock, int placedMeta) {
        return getDefaultState();
    }

    default IBlockState onBlockChanged(IBlockState state, Block oldBlock, Block newBlock, int oldMeta, int newMeta) {
        return null;
    }
}
