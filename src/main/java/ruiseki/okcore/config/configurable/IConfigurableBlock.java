package ruiseki.okcore.config.configurable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.okcore.block.IBlockTooltipProvider;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;

/**
 * Configurable blocks.
 *
 * @author rubensworks
 */
public interface IConfigurableBlock extends IConfigurable<BlockConfig>, IBlockPropertyProvider, IBlockTooltipProvider {

    /**
     * If this block has a corresponding GUI.
     * This required the block to implement {@link ruiseki.okcore.inventory.IGuiContainerProvider}.
     *
     * @return If it has a GUI.
     */
    public boolean hasGui();

    /**
     * Gets the {@link BlockState} to place
     *
     * @param world  The world the block is being placed in
     * @param pos    The position the block is being placed at
     * @param facing The side the block is being placed on
     * @param hitX   The X coordinate of the hit vector
     * @param hitY   The Y coordinate of the hit vector
     * @param hitZ   The Z coordinate of the hit vector
     * @param meta   The metadata of {@link ItemStack} as processed by {@link Item#getMetadata(int)}
     * @param placer The entity placing the block
     * @return The state to be placed in the world
     */
    default BlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        return BlockStateHelpers.getState(getConfig().getBlockInstance(), meta);
    }

    default BlockState getDefaultState(int meta) {
        return BlockStateHelpers.getState(getConfig().getBlockInstance(), meta);
    }
}
