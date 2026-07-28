package ruiseki.okcore.helper;

import java.io.File;
import java.util.Optional;
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

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.okcore.datastructure.BlockStack;

public class Helpers {

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
}
