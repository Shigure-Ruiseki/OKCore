package ruiseki.okcore.helper;

import java.util.Optional;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Contains helper methods for various tile entity specific things.
 *
 * @author rubensworks
 */
public final class TileHelpers {

    /**
     * Safely cast a tile entity.
     *
     * @param dimPos      The dimensional position of the block providing the tile entity.
     * @param targetClazz The class to cast to.
     * @param <T>         The type of tile to cast at.
     * @return The tile entity or null.
     */
    public static <T> T getSafeTile(DimPos dimPos, Class<T> targetClazz) {
        World world = dimPos.getWorld();
        if (world == null) {
            return null;
        }
        return getSafeTile(world, dimPos.getBlockPos(), targetClazz);
    }

    /**
     * Safely cast a tile entity.
     *
     * @param world       The world.
     * @param x,          y, z The position of the block providing the tile entity.
     * @param targetClazz The class to cast to.
     * @param <T>         The type of tile to cast at.
     * @return The tile entity or null.
     */
    public static <T> T getSafeTile(IBlockAccess world, int x, int y, int z, Class<T> targetClazz) {
        return getSafeTile(world, new BlockPos(x, y, z), targetClazz);
    }

    /**
     * Safely cast a tile entity.
     *
     * @param world       The world.
     * @param pos         The position of the block providing the tile entity.
     * @param targetClazz The class to cast to.
     * @param <T>         The type of tile to cast at.
     * @return The tile entity or null.
     */
    public static <T> T getSafeTile(IBlockAccess world, BlockPos pos, Class<T> targetClazz) {
        TileEntity tile = pos.getTileEntity(world);
        try {
            return targetClazz.cast(tile);
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * Safely get a capability from a tile.
     *
     * @param dimPos     The dimensional position of the block providing the tile entity.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> C getCapability(DimPos dimPos, Capability<C> capability) {
        World world = dimPos.getWorld();
        if (world == null) {
            return null;
        }
        return getCapability(world, dimPos.getBlockPos(), null, capability);
    }

    /**
     * Safely get a capability from a tile.
     *
     * @param dimPos     The dimensional position of the block providing the tile entity.
     * @param side       The side to get the capability from.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> C getCapability(DimPos dimPos, ForgeDirection side, Capability<C> capability) {
        World world = dimPos.getWorld();
        if (world == null) {
            return null;
        }
        return getCapability(world, dimPos.getBlockPos(), side, capability);
    }

    /**
     * Safely get a capability from a tile.
     *
     * @param world      The world.
     * @param pos        The position of the block of the tile entity providing the capability.
     * @param side       The side to get the capability from.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> C getCapability(World world, BlockPos pos, ForgeDirection side, Capability<C> capability) {
        return getCapability((IBlockAccess) world, pos, side, capability);
    }

    /**
     * Safely get a capability from a tile.
     *
     * @param world      The world.
     * @param pos        The position of the block of the tile entity providing the capability.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> C getCapability(IBlockAccess world, BlockPos pos, Capability<C> capability) {
        return getCapability(world, pos, null, capability);
    }

    /**
     * Safely get a capability from a tile.
     *
     * @param world      The world.
     * @param pos        The position of the block of the tile entity providing the capability.
     * @param side       The side to get the capability from.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> C getCapability(IBlockAccess world, BlockPos pos, ForgeDirection side, Capability<C> capability) {
        TileEntity tile = TileHelpers.getSafeTile(world, pos, TileEntity.class);
        if (tile instanceof ICapabilityProvider provider) {
            return provider.getCapability(capability, side);
        }
        return null;
    }

    public static Optional<TileEntity> getTileEntity(@Nullable IBlockAccess level, BlockPos pos) {
        if (level == null || pos == null) return Optional.empty();
        return Optional.ofNullable(pos.getTileEntity(level));
    }

    public static Optional<TileEntity> getLoadedTileEntity(@Nullable World level, BlockPos pos) {
        if (level != null && pos != null && pos.isLoaded(level)) {
            return Optional.ofNullable(pos.getTileEntity(level));
        }
        return Optional.empty();
    }

    public static <T> Optional<T> getLoadedTileEntity(@Nullable World level, BlockPos pos, Class<T> teClass) {
        if (level != null && pos != null && pos.isLoaded(level)) {
            return Optional.ofNullable(getSafeTile(level, pos, teClass));
        }
        return Optional.empty();
    }

    public static <T> Optional<T> getTileEntity(@Nullable IBlockAccess level, BlockPos pos, Class<T> teClass) {
        if (level == null || pos == null) return Optional.empty();
        return Optional.ofNullable(getSafeTile(level, pos, teClass));
    }

    public static void notifyBlockUpdate(TileEntity tile) {
        if (tile == null) return;

        World world = tile.getWorldObj();
        if (world == null) return;

        tile.markDirty();
        world.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
    }

}
