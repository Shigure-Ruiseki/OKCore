package ruiseki.okcore.helper;

import java.util.Optional;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Contains helper methods for various tile entity specific things.
 *
 * @author rubensworks
 */
public class TileHelpers {

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
     * @param x, y, z     The position of the block providing the tile entity.
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
        if (world == null || pos == null) return null;
        TileEntity tile = pos.getTileEntity(world);
        try {
            return targetClazz.cast(tile);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static <T> Optional<T> get(@Nullable IBlockAccess world, int x, int y, int z, Class<T> teClass) {
        if (world == null) return Optional.empty();
        return Optional.ofNullable(getSafeTile(world, x, y, z, teClass));
    }

    public static <T> Optional<T> get(@Nullable IBlockAccess world, @Nullable BlockPos pos, Class<T> teClass) {
        if (world == null || pos == null) return Optional.empty();
        return Optional.ofNullable(getSafeTile(world, pos, teClass));
    }

    public static <T> Optional<T> get(@Nullable DimPos dimPos, Class<T> teClass) {
        if (dimPos == null) return Optional.empty();
        return Optional.ofNullable(getSafeTile(dimPos, teClass));
    }

    public static Optional<TileEntity> getTileEntity(@Nullable IBlockAccess world, int x, int y, int z) {
        if (world == null) return Optional.empty();
        return Optional.ofNullable(world.getTileEntity(x, y, z));
    }

    public static Optional<TileEntity> getTileEntity(@Nullable IBlockAccess world, BlockPos pos) {
        if (world == null || pos == null) return Optional.empty();
        return Optional.ofNullable(pos.getTileEntity(world));
    }

    public static Optional<TileEntity> getLoadedTileEntity(@Nullable World world, BlockPos pos) {
        if (world != null && pos != null && pos.isLoaded(world)) {
            return Optional.ofNullable(pos.getTileEntity(world));
        }
        return Optional.empty();
    }

    public static <T> Optional<T> getLoadedTileEntity(@Nullable World world, BlockPos pos, Class<T> teClass) {
        if (world != null && pos != null && pos.isLoaded(world)) {
            return Optional.ofNullable(getSafeTile(world, pos, teClass));
        }
        return Optional.empty();
    }

    public static <T> Optional<T> getTileEntity(@Nullable IBlockAccess world, BlockPos pos, Class<T> teClass) {
        if (world == null || pos == null) return Optional.empty();
        return Optional.ofNullable(getSafeTile(world, pos, teClass));
    }

    public static void notifyBlockUpdate(TileEntity tile) {
        if (tile == null) return;

        World world = tile.getWorldObj();
        if (world == null) return;

        tile.markDirty();
        world.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
    }
}
