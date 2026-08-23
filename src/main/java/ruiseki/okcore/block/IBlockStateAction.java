package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;

public interface IBlockStateAction {

    /// Gets the {@link BlockState} to place
    ///
    /// @param world The world the block is being placed in
    /// @param pos The position the block is being placed at
    /// @param facing The side the block is being placed on
    /// @param hitX The X coordinate of the hit vector
    /// @param hitY The Y coordinate of the hit vector
    /// @param hitZ The Z coordinate of the hit vector
    /// @param meta The metadata of {@link ItemStack} as processed by {@link Item#getMetadata(int)}
    /// @param placer The entity placing the block
    /// @return The state to be placed in the world
    default BlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState();
    }

    /// Gets the default state for this block (metadata 0).
    /// @throws IllegalStateException if implemented on a class that does not extend {@link Block}.
    default BlockState getDefaultState() {
        if (this instanceof Block block) {
            return BlockStateHelpers.getState(block, 0);
        }
        throw new IllegalStateException(
            "IBlockStateAction can only be implemented by a class extending net.minecraft.block.Block! Target class: "
                + getClass().getName());
    }

    /// Gets the [Block] for a given [BlockState], when the [BlockState] is placed into a [World].
    default Block getBlockForState(BlockState state) {
        return state.getBlock();
    }

    /// Gets the metadata for a given [BlockState], when the [BlockState] is placed into a [World].
    default int getMetaForState(BlockState state) {
        return state.getBlockMeta(0);
    }
}
