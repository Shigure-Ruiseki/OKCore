package ruiseki.okcore.helper;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderState;
import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.init.ModBase;

public class Helpers {

    private static final Map<Pair<String, IDType>, AtomicInteger> ID_COUNTERS = new ConcurrentHashMap<>();

    public static int getNewId(ModBase modId, IDType type) {
        return getNewId(modId.getModId(), type);
    }

    public static int getNewId(String modId, IDType type) {
        Pair<String, IDType> key = Pair.of(modId, type);
        return ID_COUNTERS.computeIfAbsent(key, k -> new AtomicInteger(0))
            .getAndIncrement();
    }

    public enum IDType {
        /**
         * Entity ID.
         */
        ENTITY,
        /**
         * GUI ID.
         */
        GUI,
        /**
         * Packet ID.
         */
        PACKET
    }

    public static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");

    public static <T> Stream<T> toStream(Optional<? extends T> optional) {
        return orElseGet(optional.map(Stream::of), Stream::empty);
    }

    public static <U> U orElseGet(final Optional<? extends U> optional, final Supplier<? extends U> other) {
        return optional.isPresent() ? optional.get() : other.get();
    }

    public static ResourceLocation parseLocation(String location) {
        if (location == null || location.isEmpty()) return AIR_ID;

        int idx = location.indexOf(':');
        if (idx == -1) {
            return new ResourceLocation("minecraft", location);
        }
        return new ResourceLocation(location.substring(0, idx), location.substring(idx + 1));
    }

    @Nullable
    public static ResourceLocation getLocation(Item item) {
        if (item == null) return null;
        String name = (String) Item.itemRegistry.getNameForObject(item);
        return name != null ? parseLocation(name) : null;
    }

    @Nullable
    public static ResourceLocation getLocation(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return null;
        return getLocation(stack.getItem());
    }

    @NotNull
    public static ResourceLocation getLocationOrDefault(Item item, @NotNull ResourceLocation fallback) {
        ResourceLocation loc = getLocation(item);
        return loc != null ? loc : fallback;
    }

    @NotNull
    public static ResourceLocation getLocationOrDefault(ItemStack stack, @NotNull ResourceLocation fallback) {
        if (stack == null) return fallback;
        return getLocationOrDefault(stack.getItem(), fallback);
    }

    @Nullable
    public static ResourceLocation getLocation(Block block) {
        if (block == null) return null;
        String name = (String) Block.blockRegistry.getNameForObject(block);
        return name != null ? parseLocation(name) : null;
    }

    @Nullable
    public static ResourceLocation getLocation(BlockStack stack) {
        if (stack == null || stack.getBlock() == null) return null;
        return getLocation(stack.getBlock());
    }

    @NotNull
    public static ResourceLocation getLocationOrDefault(Block block, @NotNull ResourceLocation fallback) {
        ResourceLocation loc = getLocation(block);
        return loc != null ? loc : fallback;
    }

    @NotNull
    public static ResourceLocation getLocationOrDefault(BlockStack stack, @NotNull ResourceLocation fallback) {
        if (stack == null) return fallback;
        return getLocationOrDefault(stack.getBlock(), fallback);
    }

    @Nullable
    public static ResourceLocation getLocation(Entity entity) {
        if (entity == null) return null;
        String entityName = EntityList.getEntityString(entity);
        return entityName != null ? parseLocation(entityName) : null;
    }

    @Nullable
    public static ResourceLocation getLocation(Fluid fluid) {
        if (fluid == null) return null;
        return parseLocation(fluid.getName());
    }

    public static String getServerName(MinecraftServer server) {
        return server.getFolderName();
    }

    public static File getServerFolder(MinecraftServer server) {
        String folderName = getServerName(server);
        return server.isDedicatedServer() ? new File(folderName)
            : new File(
                FMLCommonHandler.instance()
                    .getSavesDirectory(),
                folderName);
    }

    /**
     * Convert r, g and b colors to an integer representation.
     *
     * @param r red
     * @param g green
     * @param b blue
     * @return integer representation of the color.
     */
    public static int RGBToInt(int r, int g, int b) {
        return (int) r << 16 | (int) g << 8 | (int) b;
    }

    /**
     * Convert r, g, b and a colors to an integer representation.
     *
     * @param r red
     * @param g green
     * @param b blue
     * @param a alpha
     * @return integer representation of the color.
     */
    public static int RGBAToInt(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Add the given alpha value to the given RGB color.
     *
     * @param color The color.
     * @param alpha The alpha from 0-255
     * @return The color with alpha.
     */
    public static int addAlphaToColor(int color, int alpha) {
        return alpha << 24 | color;
    }

    /**
     * Add the given alpha value to the given RGB color.
     *
     * @param color The color.
     * @param alpha The alpha from 0-1
     * @return The color with alpha.
     */
    public static int addAlphaToColor(int color, float alpha) {
        return addAlphaToColor(color, Math.round(alpha * 255F));
    }

    /**
     * Convert a color in integer representation to seperated r, g and b colors.
     *
     * @param color The color in integer representation.
     * @return The separated r, g and b colors.
     */
    public static Triple<Float, Float, Float> intToRGB(int color) {
        float red, green, blue;
        red = (float) (color >> 16 & 255) / 255.0F;
        green = (float) (color >> 8 & 255) / 255.0F;
        blue = (float) (color & 255) / 255.0F;
        // this.alpha = (float)(color >> 24 & 255) / 255.0F;
        return Triple.of(red, green, blue);
    }

    /**
     * Take the sum of these two values capped at {@link Integer#MAX_VALUE}.
     * 
     * @param a Integer
     * @param b Integer
     * @return The safe sum.
     */
    public static int addSafe(int a, int b) {
        int sum = a + b;
        if (sum < a || sum < b) return Integer.MAX_VALUE;
        return sum;
    }

    /**
     * Cast a long value safely to an int.
     * If the casting would result in an overflow,
     * return the {@link Integer#MAX_VALUE}.
     * 
     * @param value A value to cast.
     * @return The casted value.
     */
    public static int castSafe(long value) {
        int casted = (int) value;
        if (casted != value) {
            return Integer.MAX_VALUE;
        }
        return casted;
    }

    /**
     * @return If minecraft is past the POST-init phase.
     */
    public static boolean isMinecraftInitialized() {
        return Loader.instance()
            .getLoaderState()
            .ordinal() > LoaderState.POSTINITIALIZATION.ordinal();
    }
}
