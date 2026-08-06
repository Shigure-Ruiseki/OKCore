package ruiseki.commoncapabilities.modcompat.vanilla.capability.itemhandler;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.commoncapabilities.modcompat.vanilla.capability.VanillaEntityItemCapabilityDelegator;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

/**
 * An item handler for entity items that have an item handler.
 *
 * @author rubensworks
 */
public class VanillaEntityItemItemHandler extends VanillaEntityItemCapabilityDelegator<IItemHandler>
    implements IItemHandler {

    public VanillaEntityItemItemHandler(EntityItem entity, ForgeDirection side) {
        super(entity, side);
    }

    @Override
    protected Capability<IItemHandler> getCapabilityType() {
        return CapabilityItemHandler.ITEM_HANDLER;
    }

    @Override
    public int getSlots() {
        IItemHandler itemHandler = getCapability().getOrNull();
        if (itemHandler != null) {
            return itemHandler.getSlots();
        }
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        IItemHandler itemHandler = getCapability().getOrNull();
        if (itemHandler != null) {
            return itemHandler.getStackInSlot(slot);
        }
        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return getCapability().map(itemHandler -> {
            ItemStack innerStack = getItemStack();
            ItemStack ret = itemHandler.insertItem(slot, stack, simulate);
            if (ret != null && stack.stackSize != ret.stackSize && !simulate) {
                updateItemStack(innerStack);
            }
            return ret;
        })
            .orElse(null);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return getCapability().map(itemHandler -> {
            ItemStack innerStack = getItemStack();
            ItemStack ret = itemHandler.extractItem(slot, amount, simulate);
            if (ret != null && !simulate) {
                updateItemStack(innerStack);
            }
            return ret;
        })
            .orElse(null);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getCapability().map(itemHandler -> itemHandler.getSlotLimit(slot))
            .orElse(0);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return getCapability().map(itemHandler -> itemHandler.isItemValid(slot, stack))
            .orElse(false);
    }
}
