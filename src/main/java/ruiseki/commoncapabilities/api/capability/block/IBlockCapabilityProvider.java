package ruiseki.commoncapabilities.api.capability.block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * Equivalent of capability providers for blocks that do not have a TileEntity / internal state.
 * Register a provider at {@link BlockCapabilities}.
 * 
 * @author rubensworks
 */
public interface IBlockCapabilityProvider {

    /**
     * Retrieves the handler for the capability of the given block requested at the given position.
     * The position is identified by the World, BlockPos and Direction.
     *
     * @param blockState The blockstate to retrieve the capability from
     * @param capability The capability to check
     * @param world      The world in which the given block exists
     * @param pos        The position at which the given block exists
     * @param side       The Side to check from:
     *                   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @param <T>        The capability type.
     * @return A LazyOptional containing the requested capability instance, or LazyOptional.empty() if unsupported.
     */
    @Nonnull
    <T> LazyOptional<T> getCapability(@Nonnull BlockState blockState, @Nonnull Capability<T> capability,
        @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable ForgeDirection side);
}
