package ruiseki.okcore.tileentity;

import java.util.Collection;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Maps;

import ruiseki.okcore.inventory.SimpleInventory;
import ruiseki.okcore.persist.nbt.NBTPersist;

public class TileInventory extends TileInventoryBase {

    @NBTPersist
    protected SimpleInventory inventory;
    protected Map<ForgeDirection, int[]> slotSides;

    /**
     * Make new tile with an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     * @param inventoryName Internal name of the inventory.
     * @param stackSize     The maximum stacksize each slot can have
     */
    public TileInventory(int inventorySize, String inventoryName, int stackSize) {
        inventory = new SimpleInventory(inventorySize, inventoryName, stackSize);
        slotSides = Maps.newHashMap();
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            slotSides.put(side, new int[0]);
        }
    }

    /**
     * Make new tile with an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     * @param inventoryName Internal name of the inventory.
     */
    public TileInventory(int inventorySize, String inventoryName) {
        this(inventorySize, inventoryName, 64);
    }

    /**
     * Make new tile with an inventory.
     *
     * @param inventorySize Amount of slots in the inventory.
     */
    public TileInventory(int inventorySize) {
        this(inventorySize, "", 64);
    }

    /**
     * Add mappings to slots to a certain (normalized) side of this TileEntity.
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
    public SimpleInventory getInventory() {
        return inventory;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return slotSides.get(ForgeDirection.getOrientation(side));
    }
}
