package ruiseki.okcore.tileentity;

import java.util.Collection;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Maps;

import ruiseki.okcore.item.handler.BaseItemStackHandler;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

/**
 * A TileEntity with a static internal inventory.
 *
 * @author rubensworks
 */
public abstract class InventoryTileEntity extends InventoryTileEntityBase {

    protected BaseItemStackHandler inventory;
    protected Map<ForgeDirection, int[]> slotSides;

    /**
     * Make new tile with an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     * @param stackSize     The maximum stacksize each slot can have
     */
    public InventoryTileEntity(int inventorySize, int stackSize) {
        this.inventory = createInventory(inventorySize, stackSize);
        this.slotSides = Maps.newHashMap();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            this.slotSides.put(side, new int[0]);
        }
    }

    /**
     * Make new tile with an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     */
    public InventoryTileEntity(int inventorySize) {
        this(inventorySize, 64);
    }

    protected BaseItemStackHandler createInventory(int inventorySize, int stackSize) {
        return BaseItemStackHandler.create(inventorySize, builder -> { builder.setDefaultSlotLimit(stackSize); });
    }

    /**
     * Add mappings to slots to a certain side of this TileEntity.
     *
     * @param side  The side to map this slots to.
     * @param slots The numerical representations of the slots to map.
     */
    protected void addSlotsToSide(ForgeDirection side, Collection<Integer> slots) {
        int[] currentSlots = slotSides.get(side);
        int[] newSlots = new int[currentSlots.length + slots.size()];
        System.arraycopy(currentSlots, 0, newSlots, 0, currentSlots.length);
        int offset = currentSlots.length;
        for (int slot : slots) {
            newSlots[offset++] = slot;
        }
        slotSides.put(side, newSlots);
    }

    @Override
    public @NotNull IItemHandlerModifiable getInventory() {
        return inventory;
    }

    @Override
    public int[] getSlotsForFace(ForgeDirection side) {
        return slotSides.get(side);
    }
}
