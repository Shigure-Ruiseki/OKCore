package ruiseki.okcore.block.state;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.block.state.property.unlisted.MetaProperty;
import ruiseki.okcore.datastructure.BlockPos;

public interface IBlockStateNative {

    BlockStateContainer getStateContainer();

    default void createBlockStateDefinition(BlockStateContainer.Builder builder) {
        builder.add(MetaProperty.INSTANCE);
    }

    void registerDefaultState(IBlockState state);

    IBlockState getDefaultState();

    default Block getBlockFromState(IBlockState state) {
        return (Block) this;
    }

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

    /// Creates the initial block state when this block is placed or replaces an
    /// unrelated block.
    ///
    /// The returned state is stored in [BlockStateStorage] and becomes the initial
    /// state for the new block.
    ///
    /// The default implementation starts with [getDefaultState] and initializes
    /// the metadata property using the supplied metadata.
    ///
    /// Example:
    /// - Initialize the block's facing direction from placement metadata.
    /// - Initialize block variants based on placement conditions.
    ///
    /// @param placedBlock the block instance being placed
    /// @param placedMeta the metadata supplied for the block placement
    /// @return the initial state for the newly placed block
    default IBlockState getInitialState(Block placedBlock, int placedMeta) {
        return this.getDefaultState()
            .withProperty(MetaProperty.INSTANCE, placedMeta);
    }

    /// Updates or migrates the block state when the block instance changes but
    /// both block instances represent the same logical block.
    ///
    /// This method is called when [isSameBlock] returns true. It allows the
    /// existing state to be transferred from the old block to the new block
    /// instead of discarding it and creating a completely new state.
    ///
    /// Use [IBlockState#getValue] to read properties from the old state, then use
    /// [getDefaultState] and [IBlockState#withProperty] to construct a state
    /// compatible with the new block.
    ///
    /// Example:
    /// When transitioning from an unlit furnace to a lit furnace, transfer
    /// compatible properties from the old furnace state to the new furnace state.
    ///
    /// ```java
    /// IBlockState newState = ((IBlockStateNative) newBlock).getDefaultState();
    ///
    /// newState = newState
    /// .withProperty(CUSTOM_PROPERTY, state.getValue(CUSTOM_PROPERTY))
    /// .withProperty(MetaProperty.INSTANCE, newMeta);
    ///
    /// return newState;
    /// ```
    ///
    /// Returning null preserves the existing state without applying migration.
    ///
    /// @param state the existing state before the block instance changes
    /// @param oldBlock the previous block instance
    /// @param newBlock the incoming block instance
    /// @param oldMeta the metadata of the previous block
    /// @param newMeta the requested metadata of the incoming block
    /// @return the migrated state, or null to preserve the existing state
    ///
    /// @see isSameBlock
    /// @see getInitialState
    default IBlockState onBlockChanged(IBlockState state, Block oldBlock, Block newBlock, int oldMeta, int newMeta) {
        return state.withProperty(MetaProperty.INSTANCE, newMeta);
    }

    default IBlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState()
            .withProperty(MetaProperty.INSTANCE, meta);
    }
}
