package ruiseki.okcore.block.state;

import net.minecraft.block.Block;

import ruiseki.okcore.block.state.property.unlisted.MetaProperty;

public interface IBlockStateNative {

    BlockStateContainer getStateContainer();

    default void createBlockStateDefinition(BlockStateContainer.Builder builder) {
        builder.add(MetaProperty.INSTANCE);
    }

    void registerDefaultState(IBlockState state);

    IBlockState getDefaultState();

    Block getBlockFromState(IBlockState state);

    default int getMetaFromState(IBlockState state) {
        return state.getValue(MetaProperty.INSTANCE);
    }

    default int getStateId(IBlockState state) {
        return this.getStateContainer()
            .getStateId(state);
    }

    default IBlockState getStateById(int id) {
        return this.getStateContainer()
            .getStateById(id);
    }

    default boolean isSameBlock(Block newBlock) {
        return newBlock == this;
    }

    default IBlockState getInitialState(Block placedBlock, int placedMeta) {
        return this.getDefaultState();
    }

    default IBlockState onBlockChanged(IBlockState state, Block oldBlock, Block newBlock, int oldMeta, int newMeta) {
        return state.withProperty(MetaProperty.INSTANCE, newMeta);
    }
}
