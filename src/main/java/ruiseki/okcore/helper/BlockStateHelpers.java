package ruiseki.okcore.helper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockStatePool;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.datastructure.DimPos;

public class BlockStateHelpers {

    // BlockState Getter Methods

    @Nullable
    public static BlockState getState(IBlockAccess world, int x, int y, int z) {
        if (world == null) return null;
        return BlockPropertyRegistry.getBlockState(world, x, y, z);
    }

    @Nullable
    public static BlockState getState(IBlockAccess world, BlockPos pos) {
        if (world == null || pos == null) return null;
        return getState(world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Nullable
    public static BlockState getState(DimPos dimPos) {
        if (dimPos == null) return null;
        World world = dimPos.getWorld();
        return world != null ? getState(world, dimPos.getBlockPos()) : null;
    }

    @Nullable
    public static BlockState getState(BlockStatePool pool, IBlockAccess world, int x, int y, int z) {
        if (world == null) return null;
        return BlockPropertyRegistry.getBlockState(pool, world, x, y, z);
    }

    @Nullable
    public static BlockState getState(BlockStatePool pool, IBlockAccess world, BlockPos pos) {
        if (world == null || pos == null) return null;
        return getState(pool, world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Nullable
    public static BlockState getState(BlockStatePool pool, DimPos dimPos) {
        if (dimPos == null) return null;
        World world = dimPos.getWorld();
        return world != null ? getState(pool, world, dimPos.getBlockPos()) : null;
    }

    @Nullable
    public static BlockState getState(ItemStack stack) {
        if (stack == null) return null;
        return BlockPropertyRegistry.getBlockState(stack);
    }

    @Nullable
    public static BlockState getState(BlockStatePool pool, ItemStack stack) {
        if (stack == null) return null;
        return BlockPropertyRegistry.getBlockState(pool, stack);
    }

    @Nullable
    public static BlockState getState(BlockStack stack) {
        if (stack == null) return null;
        return getState(stack.getBlock(), stack.getMeta());
    }

    @Nullable
    public static BlockState getState(BlockStatePool pool, BlockStack stack) {
        if (stack == null) return null;
        return getState(pool, stack.getBlock(), stack.getMeta());
    }

    @Nullable
    public static BlockState getState(Block block, int meta) {
        if (block == null) return null;
        return BlockPropertyRegistry.getBlockState(block, meta);
    }

    @Nullable
    public static BlockState getState(BlockStatePool pool, Block block, int meta) {
        if (block == null) return null;
        return BlockPropertyRegistry.getBlockState(pool, block, meta);
    }

    // Property Value Getter Methods (get)

    @Nullable
    public static <T> T get(IBlockAccess world, int x, int y, int z, BlockProperty<T> property) {
        if (world == null || property == null) return null;

        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            return state.getPropertyValue(property);
        }
    }

    @Nullable
    public static <T> T get(IBlockAccess world, BlockPos pos, BlockProperty<T> property) {
        if (pos == null) return null;
        return get(world, pos.getX(), pos.getY(), pos.getZ(), property);
    }

    @Nullable
    public static <T> T get(DimPos dimPos, BlockProperty<T> property) {
        if (dimPos == null) return null;
        World world = dimPos.getWorld();
        return world != null ? get(world, dimPos.getBlockPos(), property) : null;
    }

    @Nullable
    public static <T> T get(BlockStatePool pool, IBlockAccess world, int x, int y, int z, BlockProperty<T> property) {
        if (world == null || property == null) return null;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            return state.getPropertyValue(property);
        }
    }

    @Nullable
    public static <T> T get(BlockStatePool pool, IBlockAccess world, BlockPos pos, BlockProperty<T> property) {
        if (pos == null) return null;
        return get(pool, world, pos.getX(), pos.getY(), pos.getZ(), property);
    }

    @Nullable
    public static <T> T get(BlockStatePool pool, DimPos dimPos, BlockProperty<T> property) {
        if (dimPos == null) return null;
        World world = dimPos.getWorld();
        return world != null ? get(pool, world, dimPos.getBlockPos(), property) : null;
    }

    @Nullable
    public static <T> T get(ItemStack stack, BlockProperty<T> property) {
        if (stack == null || property == null) return null;

        try (BlockState state = BlockPropertyRegistry.getBlockState(stack)) {
            return state.getPropertyValue(property);
        }
    }

    @Nullable
    public static <T> T get(BlockStatePool pool, ItemStack stack, BlockProperty<T> property) {
        if (stack == null || property == null) return null;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, stack)) {
            return state.getPropertyValue(property);
        }
    }

    @Nullable
    public static <T> T get(BlockStack stack, BlockProperty<T> property) {
        if (stack == null || property == null) return null;

        return get(stack.getBlock(), stack.getMeta(), property);
    }

    @Nullable
    public static <T> T get(BlockStatePool pool, BlockStack stack, BlockProperty<T> property) {
        if (stack == null || property == null) return null;

        return get(pool, stack.getBlock(), stack.getMeta(), property);
    }

    @Nullable
    public static <T> T get(Block block, int meta, BlockProperty<T> property) {
        if (block == null || property == null) return null;

        try (BlockState state = BlockPropertyRegistry.getBlockState(block, meta)) {
            return state.getPropertyValue(property);
        }
    }

    @Nullable
    public static <T> T get(BlockStatePool pool, Block block, int meta, BlockProperty<T> property) {
        if (block == null || property == null) return null;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, block, meta)) {
            return state.getPropertyValue(property);
        }
    }

    // Property Presence Check Methods (has)

    public static boolean has(IBlockAccess world, int x, int y, int z, BlockProperty<?> property) {
        if (world == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            return state.hasProperty(property);
        }
    }

    public static boolean has(IBlockAccess world, BlockPos pos, BlockProperty<?> property) {
        if (pos == null) return false;
        return has(world, pos.getX(), pos.getY(), pos.getZ(), property);
    }

    public static boolean has(DimPos dimPos, BlockProperty<?> property) {
        if (dimPos == null) return false;
        World world = dimPos.getWorld();
        return world != null && has(world, dimPos.getBlockPos(), property);
    }

    public static boolean has(BlockStatePool pool, IBlockAccess world, int x, int y, int z, BlockProperty<?> property) {
        if (world == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            return state.hasProperty(property);
        }
    }

    public static boolean has(BlockStatePool pool, IBlockAccess world, BlockPos pos, BlockProperty<?> property) {
        if (pos == null) return false;
        return has(pool, world, pos.getX(), pos.getY(), pos.getZ(), property);
    }

    public static boolean has(BlockStatePool pool, DimPos dimPos, BlockProperty<?> property) {
        if (dimPos == null) return false;
        World world = dimPos.getWorld();
        return world != null && has(pool, world, dimPos.getBlockPos(), property);
    }

    public static boolean has(ItemStack stack, BlockProperty<?> property) {
        if (stack == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(stack)) {
            return state.hasProperty(property);
        }
    }

    public static boolean has(BlockStatePool pool, ItemStack stack, BlockProperty<?> property) {
        if (stack == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, stack)) {
            return state.hasProperty(property);
        }
    }

    // Property Value Setter Methods (set)

    public static <T> boolean set(World world, int x, int y, int z, BlockProperty<T> property, T value) {
        return set(world, x, y, z, property, value, 3);
    }

    public static <T> boolean set(World world, BlockPos pos, BlockProperty<T> property, T value) {
        if (pos == null) return false;
        return set(world, pos.getX(), pos.getY(), pos.getZ(), property, value, 3);
    }

    public static <T> boolean set(DimPos dimPos, BlockProperty<T> property, T value) {
        if (dimPos == null) return false;
        World world = dimPos.getWorld();
        return world != null && set(world, dimPos.getBlockPos(), property, value, 3);
    }

    public static <T> boolean set(World world, int x, int y, int z, BlockProperty<T> property, T value, int flags) {
        if (world == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            state.setPropertyValue(property, value);
            return state.place(world, x, y, z, flags);
        }
    }

    public static <T> boolean set(World world, BlockPos pos, BlockProperty<T> property, T value, int flags) {
        if (pos == null) return false;
        return set(world, pos.getX(), pos.getY(), pos.getZ(), property, value, flags);
    }

    public static <T> boolean set(DimPos dimPos, BlockProperty<T> property, T value, int flags) {
        if (dimPos == null) return false;
        World world = dimPos.getWorld();
        return world != null && set(world, dimPos.getBlockPos(), property, value, flags);
    }

    public static <T> boolean set(BlockStatePool pool, World world, int x, int y, int z, BlockProperty<T> property,
        T value) {
        return set(pool, world, x, y, z, property, value, 3);
    }

    public static <T> boolean set(BlockStatePool pool, World world, BlockPos pos, BlockProperty<T> property, T value) {
        if (pos == null) return false;
        return set(pool, world, pos.getX(), pos.getY(), pos.getZ(), property, value, 3);
    }

    public static <T> boolean set(BlockStatePool pool, DimPos dimPos, BlockProperty<T> property, T value) {
        if (dimPos == null) return false;
        World world = dimPos.getWorld();
        return world != null && set(pool, world, dimPos.getBlockPos(), property, value, 3);
    }

    public static <T> boolean set(BlockStatePool pool, World world, int x, int y, int z, BlockProperty<T> property,
        T value, int flags) {
        if (world == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            state.setPropertyValue(property, value);
            return state.place(world, x, y, z, flags);
        }
    }

    public static <T> boolean set(BlockStatePool pool, World world, BlockPos pos, BlockProperty<T> property, T value,
        int flags) {
        if (pos == null) return false;
        return set(pool, world, pos.getX(), pos.getY(), pos.getZ(), property, value, flags);
    }

    public static <T> boolean set(BlockStatePool pool, DimPos dimPos, BlockProperty<T> property, T value, int flags) {
        if (dimPos == null) return false;
        World world = dimPos.getWorld();
        return world != null && set(pool, world, dimPos.getBlockPos(), property, value, flags);
    }
}
