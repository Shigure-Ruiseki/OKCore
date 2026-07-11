package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityCache;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;

public class ItemHandlerWrapper implements ICapabilityProvider {

    private final CapabilityCache cache = new CapabilityCache();

    public ItemHandlerWrapper(IInventory inventory) {
        cache.addCapabilityResolver(new ItemHandlerResolver(inventory));
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        return cache.getCapability(cap, side);
    }
}
