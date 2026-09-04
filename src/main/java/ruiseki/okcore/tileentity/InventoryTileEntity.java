package ruiseki.okcore.tileentity;

import java.util.Collection;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Maps;

import ruiseki.okcore.inventory.SimpleInventory;

/**
 * A TileEntity with a static internal inventory.
 *
 * @author rubensworks
 */
public abstract class InventoryTileEntity extends InventoryTileEntityBase {

    protected SimpleInventory inventory;
    protected Map<ForgeDirection, int[]> slotSides;

    /**
     * Make new tile with an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     * @param inventoryName Internal name of the inventory.
     * @param stackSize     The maximum stacksize each slot can have
     */
    public InventoryTileEntity(int inventorySize, String inventoryName, int stackSize) {
        this.inventory = createInventory(inventorySize, inventoryName, stackSize);
        this.slotSides = Maps.newHashMap();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            this.slotSides.put(side, new int[0]);
        }
    }

    /**
     * Make new tile with an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     * @param inventoryName Internal name of the inventory.
     */
    public InventoryTileEntity(int inventorySize, String inventoryName) {
        this(inventorySize, inventoryName, 64);
    }

    protected SimpleInventory createInventory(int inventorySize, String inventoryName, int stackSize) {
        return new SimpleInventory(inventorySize, inventoryName, stackSize);
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
    public @NotNull SimpleInventory getInventory() {
        return inventory;
    }

    @Override
    public int[] getSlotsForFace(ForgeDirection side) {
        return slotSides.get(side);
    }
}
