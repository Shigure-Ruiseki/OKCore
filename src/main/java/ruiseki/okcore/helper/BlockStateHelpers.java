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

import ruiseki.okcore.datastructure.BlockStack;

public class BlockStateHelpers {

    @Nullable
    public static <T> T get(IBlockAccess world, int x, int y, int z, BlockProperty<T> property) {
        if (world == null || property == null) return null;

        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            return state.getPropertyValue(property);
        }
    }

    @Nullable
    public static <T> T get(BlockStatePool pool, IBlockAccess world, int x, int y, int z, BlockProperty<T> property) {
        if (world == null || property == null) return null;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            return state.getPropertyValue(property);
        }
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

    public static boolean has(IBlockAccess world, int x, int y, int z, BlockProperty<?> property) {
        if (world == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            return state.hasProperty(property);
        }
    }

    public static boolean has(BlockStatePool pool, IBlockAccess world, int x, int y, int z, BlockProperty<?> property) {
        if (world == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            return state.hasProperty(property);
        }
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

    public static <T> boolean set(World world, int x, int y, int z, BlockProperty<T> property, T value) {
        return set(world, x, y, z, property, value, 3);
    }

    public static <T> boolean set(World world, int x, int y, int z, BlockProperty<T> property, T value, int flags) {
        if (world == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(world, x, y, z)) {
            state.setPropertyValue(property, value);
            return state.place(world, x, y, z, flags);
        }
    }

    public static <T> boolean set(BlockStatePool pool, World world, int x, int y, int z, BlockProperty<T> property,
        T value) {
        return set(pool, world, x, y, z, property, value, 3);
    }

    public static <T> boolean set(BlockStatePool pool, World world, int x, int y, int z, BlockProperty<T> property,
        T value, int flags) {
        if (world == null || property == null) return false;

        try (BlockState state = BlockPropertyRegistry.getBlockState(pool, world, x, y, z)) {
            state.setPropertyValue(property, value);
            return state.place(world, x, y, z, flags);
        }
    }
}
