package ruiseki.okcore.item.capability.wrapper;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.resolver.ICapabilityResolver;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

@NotNullByDefault
public class ChestHandlerResolver implements ICapabilityResolver {

    private final TileEntityChest chest;
    private final Map<ForgeDirection, LazyOptional<IItemHandler>> cache = new EnumMap<>(ForgeDirection.class);

    public ChestHandlerResolver(TileEntityChest chest) {
        this.chest = chest;
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return List.of(CapabilityItemHandler.ITEM_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable ForgeDirection side) {
        if (capability != CapabilityItemHandler.ITEM_HANDLER) return LazyOptional.empty();

        return (LazyOptional<T>) getCachedOrResolve(side == null ? ForgeDirection.UNKNOWN : side, cache, () -> {
            Block block = chest.getBlockType();
            if (block instanceof BlockChest) {
                IInventory inv = ((BlockChest) block)
                    .func_149951_m(chest.getWorldObj(), chest.xCoord, chest.yCoord, chest.zCoord);
                if (inv != null) {
                    return LazyOptional
                        .of(() -> new InventoryHandlerWrapper(inv, side == null ? ForgeDirection.UNKNOWN : side));
                }
            }
            return LazyOptional.empty();
        });
    }

    private static <T> LazyOptional<T> getCachedOrResolve(ForgeDirection side,
        Map<ForgeDirection, LazyOptional<T>> cache, java.util.function.Supplier<LazyOptional<T>> resolver) {
        LazyOptional<T> cached = cache.get(side);
        if (cached != null && cached.isPresent()) return cached;
        LazyOptional<T> resolved = resolver.get();
        cache.put(side, resolved);
        return resolved;
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable ForgeDirection side) {
        ForgeDirection s = (side == null) ? ForgeDirection.UNKNOWN : side;
        LazyOptional<IItemHandler> cap = cache.remove(s);
        if (cap != null && cap.isPresent()) cap.invalidate();
    }

    @Override
    public void invalidateAll() {
        cache.values()
            .forEach(cap -> { if (cap.isPresent()) cap.invalidate(); });
        cache.clear();
    }
}
