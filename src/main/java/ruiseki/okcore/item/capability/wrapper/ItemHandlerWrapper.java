package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.capability.CapabilityItemHandler;

public class ItemHandlerWrapper implements ICapabilityProvider {

    private final IInventory inventory;
    @SuppressWarnings("unchecked")
    private final LazyOptional<IItemHandler>[] itemCaps = new LazyOptional[7];

    public ItemHandlerWrapper(IInventory inventory) {
        this.inventory = inventory;
        for (int i = 0; i < itemCaps.length; i++) {
            itemCaps[i] = LazyOptional.empty();
        }
    }

    private int getIndexForSide(@Nullable ForgeDirection side) {
        if (side == null || side == ForgeDirection.UNKNOWN) {
            return 6;
        }
        return side.ordinal();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            int index = getIndexForSide(side);
            LazyOptional<IItemHandler> cachedCap = itemCaps[index];

            if (!cachedCap.isPresent()) {
                ForgeDirection dir = (side == null) ? ForgeDirection.UNKNOWN : side;
                cachedCap = LazyOptional.of(() -> new InventoryHandlerWrapper(inventory, dir));
                itemCaps[index] = cachedCap;
            }

            return cachedCap.cast();
        }
        return LazyOptional.empty();
    }
}
