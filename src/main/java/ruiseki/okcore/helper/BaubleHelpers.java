package ruiseki.okcore.helper;

import baubles.common.container.InventoryBaubles;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import net.minecraft.inventory.Slot;
import ruiseki.okcore.lib.LibMods;

public class BaubleHelpers {

    public static boolean checkAndRegisterType(String slotType) {
        if (slotType == null || slotType.isEmpty()) return false;
        if (!BaubleExpandedSlots.isTypeRegistered(slotType)) {
            return BaubleExpandedSlots.tryRegisterType(slotType);
        }
        return true;
    }

    public static void assignSlot(String slotType) {
        BaubleExpandedSlots.tryAssignSlotOfType(slotType);
    }

    public static void assignSlotsUpToMinimum(String slotType, int size) {
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(slotType, size);
    }

    public static void unassignSlotsDownToMaximum(String slotType, int size) {
        BaubleExpandedSlots.tryUnassignSlotsDownToMaximum(slotType, size);
    }

    public static IInventory getBaubles(EntityPlayer player) {
        if (player == null) return null;
        if (!LibMods.Baubles.isModLoaded()) return null;
        return BaublesApi.getBaubles(player);
    }

    public static boolean isBaubles(Slot slot) {
        return isBaubles(slot.inventory);
    }

    public static boolean isBaubles(IInventory inventory) {
        if (inventory == null) return false;
        if (!LibMods.Baubles.isModLoaded()) return false;
        return inventory instanceof InventoryBaubles;
    }
}
