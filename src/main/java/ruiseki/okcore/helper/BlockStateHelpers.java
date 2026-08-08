package ruiseki.okcore.helper;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNullByDefault;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockStatePool;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.BlockStack;

@NotNullByDefault
public class BlockStateHelpers {

    // BlockState Getter Methods

    public static BlockState getState(IBlockAccess world, int x, int y, int z) {
        return BlockPropertyRegistry.getBlockState(world, x, y, z);
    }

    public static BlockState getState(IBlockAccess world, BlockPos pos) {
        return getState(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockState getState(BlockStatePool pool, IBlockAccess world, int x, int y, int z) {
        return BlockPropertyRegistry.getBlockState(pool, world, x, y, z);
    }

    public static BlockState getState(BlockStatePool pool, IBlockAccess world, BlockPos pos) {
        return getState(pool, world, pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockState getState(ItemStack stack) {
        return BlockPropertyRegistry.getBlockState(stack);
    }

    public static BlockState getState(BlockStatePool pool, ItemStack stack) {
        return BlockPropertyRegistry.getBlockState(pool, stack);
    }

    public static BlockState getState(BlockStack stack) {
        return getState(stack.getBlock(), stack.getMeta());
    }

    public static BlockState getState(BlockStatePool pool, BlockStack stack) {
        return getState(pool, stack.getBlock(), stack.getMeta());
    }

    public static BlockState getState(Block block, int meta) {
        return BlockPropertyRegistry.getBlockState(block, meta);
    }

    public static BlockState getState(BlockStatePool pool, Block block, int meta) {
        return BlockPropertyRegistry.getBlockState(pool, block, meta);
    }

    // Property Value Getter Methods (get)

    public static <T> T get(IBlockAccess world, int x, int y, int z, BlockProperty<T> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            return state.getPropertyValue(property);
        }
    }

    public static <T> T get(IBlockAccess world, BlockPos pos, BlockProperty<T> property) {
        return get(world, pos.getX(), pos.getY(), pos.getZ(), property);
    }

    public static <T> T get(BlockStatePool pool, IBlockAccess world, int x, int y, int z, BlockProperty<T> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            return state.getPropertyValue(property);
        }
    }

    public static <T> T get(BlockStatePool pool, IBlockAccess world, BlockPos pos, BlockProperty<T> property) {
        return get(pool, world, pos.getX(), pos.getY(), pos.getZ(), property);
    }

    public static <T> T get(ItemStack stack, BlockProperty<T> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(stack)) {
            return state.getPropertyValue(property);
        }
    }

    public static <T> T get(BlockStatePool pool, ItemStack stack, BlockProperty<T> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, stack)) {
            return state.getPropertyValue(property);
        }
    }

    public static <T> T get(BlockStack stack, BlockProperty<T> property) {
        return get(stack.getBlock(), stack.getMeta(), property);
    }

    public static <T> T get(BlockStatePool pool, BlockStack stack, BlockProperty<T> property) {
        return get(pool, stack.getBlock(), stack.getMeta(), property);
    }

    public static <T> T get(Block block, int meta, BlockProperty<T> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(block, meta)) {
            return state.getPropertyValue(property);
        }
    }

    public static <T> T get(BlockStatePool pool, Block block, int meta, BlockProperty<T> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, block, meta)) {
            return state.getPropertyValue(property);
        }
    }

    // Property Presence Check Methods (has)

    public static boolean has(IBlockAccess world, int x, int y, int z, BlockProperty<?> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            return state.hasProperty(property);
        }
    }

    public static boolean has(IBlockAccess world, BlockPos pos, BlockProperty<?> property) {
        return has(world, pos.getX(), pos.getY(), pos.getZ(), property);
    }

    public static boolean has(BlockStatePool pool, IBlockAccess world, int x, int y, int z, BlockProperty<?> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            return state.hasProperty(property);
        }
    }

    public static boolean has(BlockStatePool pool, IBlockAccess world, BlockPos pos, BlockProperty<?> property) {
        return has(pool, world, pos.getX(), pos.getY(), pos.getZ(), property);
    }

    public static boolean has(ItemStack stack, BlockProperty<?> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(stack)) {
            return state.hasProperty(property);
        }
    }

    public static boolean has(BlockStatePool pool, ItemStack stack, BlockProperty<?> property) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, stack)) {
            return state.hasProperty(property);
        }
    }

    // Property Value Setter Methods (set)

    public static <T> boolean set(World world, int x, int y, int z, BlockProperty<T> property, T value) {
        return set(world, x, y, z, property, value, 3);
    }

    public static <T> boolean set(World world, BlockPos pos, BlockProperty<T> property, T value) {
        return set(world, pos.getX(), pos.getY(), pos.getZ(), property, value, 3);
    }

    public static <T> boolean set(World world, int x, int y, int z, BlockProperty<T> property, T value, int flags) {
        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            state.setPropertyValue(property, value);
            return state.place(world, x, y, z, flags);
        }
    }

    public static <T> boolean set(World world, BlockPos pos, BlockProperty<T> property, T value, int flags) {
        return set(world, pos.getX(), pos.getY(), pos.getZ(), property, value, flags);
    }

    public static <T> boolean set(BlockStatePool pool, World world, int x, int y, int z, BlockProperty<T> property,
        T value) {
        return set(pool, world, x, y, z, property, value, 3);
    }

    public static <T> boolean set(BlockStatePool pool, World world, BlockPos pos, BlockProperty<T> property, T value) {
        return set(pool, world, pos.getX(), pos.getY(), pos.getZ(), property, value, 3);
    }

    public static <T> boolean set(BlockStatePool pool, World world, int x, int y, int z, BlockProperty<T> property,
        T value, int flags) {

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            state.setPropertyValue(property, value);
            return state.place(world, x, y, z, flags);
        }
    }

    public static <T> boolean set(BlockStatePool pool, World world, BlockPos pos, BlockProperty<T> property, T value,
        int flags) {
        return set(pool, world, pos.getX(), pos.getY(), pos.getZ(), property, value, flags);
    }
}
