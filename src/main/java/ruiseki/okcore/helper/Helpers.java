package ruiseki.okcore.helper;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class Helpers {

    public static <T> Stream<T> toStream(Optional<? extends T> optional) {
        return orElseGet(optional.map(Stream::of), Stream::empty);
    }

    public static <U> U orElseGet(final Optional<? extends U> optional, final Supplier<? extends U> other) {
        if (optional.isPresent()) {
            return optional.get();
        }
        return other.get();
    }

    public static ResourceLocation parseLocation(String location) {
        int idx = location.indexOf(':');
        if (idx == -1) {
            return new ResourceLocation("minecraft", location);
        }
        return new ResourceLocation(location.substring(0, idx), location.substring(idx + 1));
    }

    public static ResourceLocation getLocation(Item item) {
        if (item == null) return null;
        String nameObj = Item.itemRegistry.getNameForObject(item);
        return nameObj != null ? parseLocation(nameObj) : null;
    }

    public static ResourceLocation getLocation(Block block) {
        if (block == null) return null;
        String nameObj = Block.blockRegistry.getNameForObject(block);
        return nameObj != null ? parseLocation(nameObj) : null;
    }

    public static ResourceLocation getLocation(Entity entity) {
        if (entity == null) return null;
        String entityName = EntityList.getEntityString(entity);
        return entityName != null ? parseLocation(entityName) : null;
    }

    public static ResourceLocation getLocation(Fluid fluid) {
        if (fluid == null) return null;
        return parseLocation(fluid.getName());
    }
}
