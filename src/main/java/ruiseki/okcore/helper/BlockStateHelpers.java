package ruiseki.okcore.helper;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockStatePool;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import cpw.mods.fml.common.registry.GameData;
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

    /**
     * Deserializes a BlockState from a JSON element.
     * Supports both GTNHLib string format ("modid:block[meta=0]")
     * and JsonObject format ({"block": "modid:block", "meta": 0}).
     *
     * @param json The JsonObject containing the property.
     * @param key  The key to look for in the JsonObject.
     * @return The parsed BlockState, or null if missing or invalid.
     */
    @Nullable
    public static BlockState fromJson(JsonObject json, String key) {
        if (!json.has(key)) return null;
        JsonElement element = json.get(key);

        if (element.isJsonPrimitive()) {
            String str = element.getAsString();
            if (str.isEmpty()) return null;
            if (!str.contains("[")) {
                str = str + "[]";
            }

            return BlockState.fromString(null, str);
        }

        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            String blockName = GsonHelpers.getAsString(obj, "block");
            int meta = GsonHelpers.getAsInt(obj, "meta", 0);

            Block block = GameData.getBlockRegistry()
                .getObject(blockName);
            if (block == null || block == Blocks.air) {
                return null;
            }

            return BlockStateHelpers.getState(block, meta);
        }

        throw new IllegalArgumentException("Field '" + key + "' must be a JsonObject or a String!");
    }

    /**
     * Serializes a BlockState to a JsonPrimitive using its toString() method.
     *
     * @param state The BlockState to serialize.
     * @return JsonPrimitive containing the formatted string (e.g. "minecraft:sapling[meta=4]"),
     *         or JsonNull if state is null.
     */
    public static JsonElement toJson(@Nullable BlockState state) {
        if (state == null) return JsonNull.INSTANCE;
        return new JsonPrimitive(state.toString());
    }
}
