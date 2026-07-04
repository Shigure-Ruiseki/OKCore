package ruiseki.okcore.helper;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
