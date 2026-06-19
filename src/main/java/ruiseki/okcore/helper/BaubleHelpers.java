package ruiseki.okcore.helper;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import ruiseki.okcore.lib.LibMods;

public class BaubleHelpers {

    public static boolean checkAndRegisterType(String slotType) {
        if (slotType == null || slotType.isEmpty() || !LibMods.BaublesExpanded.isModLoaded()) return false;
        return BaubleExpandedSlots.isTypeRegistered(slotType) || BaubleExpandedSlots.tryRegisterType(slotType);
    }

    public static void assignSlot(String slotType) {
        if (!LibMods.BaublesExpanded.isModLoaded()) return;
        BaubleExpandedSlots.tryAssignSlotOfType(slotType);
    }

    public static void assignSlotsUpToMinimum(String slotType, int size) {
        if (!LibMods.BaublesExpanded.isModLoaded()) return;
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(slotType, size);
    }

    public static void unassignSlotsDownToMaximum(String slotType, int size) {
        if (!LibMods.BaublesExpanded.isModLoaded()) return;
       BaubleExpandedSlots.tryUnassignSlotsDownToMaximum(slotType, size);
    }

    public static IInventory getBaubles(EntityPlayer player) {
        if (player == null || !LibMods.Baubles.isModLoaded()) return null;
        return BaublesApi.getBaubles(player);
    }

    public static boolean isBaubles(Slot slot) {
        return slot != null && isBaubles(slot.inventory);
    }

    public static boolean isBaubles(IInventory inventory) {
        if (inventory == null || !LibMods.Baubles.isModLoaded()) return false;
        String className = inventory.getClass().getName();
        return "baubles.common.container.InventoryBaubles".equals(className);
    }
}
